/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.core.rtext;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaUI;
import org.fife.ui.rtextarea.RUndoManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A text area that supports editing FlatLaf themes.
 *
 * @author Karl Tauber
 */
class FlatSyntaxTextArea
	extends TextEditorPane
{
	private RUndoManager undoManager;
	private boolean useColorOfColorTokens;

	final FlatThemePropertiesSupport propertiesSupport = new FlatThemePropertiesSupport( this );
	private final Map<String, Color> parsedColorsMap = new HashMap<>();

	FlatSyntaxTextArea() {
		// this is necessary because RTextAreaBase.init() always sets foreground to black
		setForeground( UIManager.getColor( "TextArea.foreground" ) );

		// remove Ctrl+Tab and Ctrl+Shift+Tab focus traversal keys to allow tabbed pane to process them
		setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet() );
		setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet() );

		// add editor actions
		ActionMap actionMap = getActionMap();
		// add editor key strokes
		InputMap inputMap = getInputMap();
		int defaultModifier = RTextArea.getDefaultModifier();
		int alt = InputEvent.ALT_DOWN_MASK;
		// add Ctrl+7 for German keyboards where Ctrl+/ does not work
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_7,    defaultModifier), RSyntaxTextAreaEditorKit.rstaToggleCommentAction );
	}

	@Override
	protected RTextAreaUI createRTextAreaUI() {
		return new FlatRSyntaxTextAreaUI( this );
	}

	@Override
	protected RUndoManager createUndoManager() {
		undoManager = super.createUndoManager();
		return undoManager;
	}

	void runWithoutUndo( Runnable runnable ) {
		getDocument().removeUndoableEditListener( undoManager );
		try {
			runnable.run();
		} finally {
			getDocument().addUndoableEditListener( undoManager );
		}
	}

	boolean isUseColorOfColorTokens() {
		return useColorOfColorTokens;
	}

	void setUseColorOfColorTokens( boolean useColorOfColorTokens ) {
		this.useColorOfColorTokens = useColorOfColorTokens;
		setHighlightCurrentLine( !useColorOfColorTokens );
	}

	@Override
	public Color getBackgroundForToken( Token t ) {
		return super.getBackgroundForToken( t );
	}

	@Override
	public Color getForegroundForToken( Token t ) {
				return super.getForegroundForToken( t );
	}


	private int colorLuminance( Color c ) {
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		int min = Math.min( red, Math.min( green, blue ) );
		int max = Math.max( red, Math.max( green, blue ) );

		return (max + min) / 2;
	}

	private boolean isCurrentLineHighlighted( int offset ) {
		try {
			return getHighlightCurrentLine() &&
				getSelectionStart() == getSelectionEnd() &&
				getLineOfOffset( offset ) == getLineOfOffset( getSelectionStart() );
		} catch( BadLocationException ex ) {
			return false;
		}
	}
}
