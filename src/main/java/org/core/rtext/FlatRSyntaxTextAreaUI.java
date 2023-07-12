/*
 * Copyright 2021 FormDev Software GmbH
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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rtextarea.ConfigurableCaret;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * @author Karl Tauber
 */
class FlatRSyntaxTextAreaUI
	extends RSyntaxTextAreaUI
{
	FlatRSyntaxTextAreaUI( JComponent rSyntaxTextArea ) {
		super( rSyntaxTextArea );
	}

	@Override
	protected Caret createCaret() {
		Caret caret = new FlatConfigurableCaret();
		caret.setBlinkRate( 500 );
		return caret;
	}

	@Override
	protected void paintCurrentLineHighlight( Graphics g, Rectangle visibleRect ) {
		if( !textArea.getHighlightCurrentLine() )
			return;

		// paint current line highlight always in the line where the caret is
		try {
			int dot = textArea.getCaret().getDot();
			Rectangle dotRect = textArea.modelToView( dot );
			int height = textArea.getLineHeight();

			g.setColor( textArea.getCurrentLineHighlightColor() );
			g.fillRect( visibleRect.x, dotRect.y, visibleRect.width, height );
		} catch( BadLocationException ex ) {
			super.paintCurrentLineHighlight( g, visibleRect );
		}
	}

	//---- class FlatConfigurableCaret ----------------------------------------

	private static class FlatConfigurableCaret
		extends ConfigurableCaret
	{
		private boolean isWordSelection;
		private boolean isLineSelection;
		private int dragSelectionStart;
		private int dragSelectionEnd;

		@Override
		public void mousePressed( MouseEvent e ) {
			super.mousePressed( e );

			JTextComponent c = getComponent();

			// left double-click starts word selection
			isWordSelection = e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( e ) && !e.isConsumed();

			// left triple-click starts line selection
			isLineSelection = e.getClickCount() == 3 && SwingUtilities.isLeftMouseButton( e ) && (!e.isConsumed() || c.getDragEnabled());

			// select line
			// (this is also done in DefaultCaret.mouseClicked(), but this event is
			// sent when the mouse is released, which is too late for triple-click-and-drag)
			if( isLineSelection ) {
				ActionMap actionMap = c.getActionMap();
				Action selectLineAction = (actionMap != null)
					? actionMap.get( DefaultEditorKit.selectLineAction )
					: null;
				if( selectLineAction != null ) {
					selectLineAction.actionPerformed( new ActionEvent( c,
						ActionEvent.ACTION_PERFORMED, null, e.getWhen(), e.getModifiers() ) );
				}
			}

			// remember selection where word/line selection starts to keep it always selected while dragging
			if( isWordSelection || isLineSelection ) {
				int mark = getMark();
				int dot = getDot();
				dragSelectionStart = Math.min( dot, mark );
				dragSelectionEnd = Math.max( dot, mark );
			}
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			isWordSelection = false;
			isLineSelection = false;
			super.mouseReleased( e );
		}

		@Override
		public void mouseDragged( MouseEvent e ) {
			if( (isWordSelection || isLineSelection) &&
				!e.isConsumed() && SwingUtilities.isLeftMouseButton( e ) )
			{
				// fix Swing's double/triple-click-and-drag behavior so that dragging after
				// a double/triple-click extends selection by whole words/lines
				JTextComponent c = getComponent();
				int pos = c.viewToModel( e.getPoint() );
				if( pos < 0 )
					return;

				try {
					if( pos > dragSelectionEnd )
						select( dragSelectionStart, isWordSelection ? Utilities.getWordEnd( c, pos ) : Utilities.getRowEnd( c, pos ) );
					else if( pos < dragSelectionStart )
						select( dragSelectionEnd, isWordSelection ? Utilities.getWordStart( c, pos ) : Utilities.getRowStart( c, pos ) );
					else
						select( dragSelectionStart, dragSelectionEnd );
				} catch( BadLocationException ex ) {
					UIManager.getLookAndFeel().provideErrorFeedback( c );
				}
			} else
				super.mouseDragged( e );
		}

		private void select( int mark, int dot ) {
			if( mark != getMark() )
				setDot( mark );
			if( dot != getDot() )
				moveDot( dot );
		}
	}
}
