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

import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.UIScale;
import org.core.fonts.FlatJetBrainsMonoFont;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A pane that supports editing FlatLaf themes.
 *
 * @author Karl Tauber
 */
public class FlatThemeEditorPane extends JPanel {
    public static final List<FlatThemeEditorPane> panes = new ArrayList<>();
    static final String DIRTY_PROPERTY = TextEditorPane.DIRTY_PROPERTY;

    private static final String FLATLAF_STYLE = "text/flatlaf";

    private static boolean findReplaceVisible;
    private static SearchContext findReplaceContext;

    private final JPanel editorPanel;
    private final RTextScrollPane scrollPane;
    private final FlatSyntaxTextArea textArea;
    private final ErrorStrip errorStrip;
    private FlatFindReplaceBar findReplaceBar;


    private File file;

    public FlatThemeEditorPane() {
        super(new BorderLayout());
        panes.add(this);

        // register FlatLaf token maker
        AbstractTokenMakerFactory tmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        tmf.putMapping(FLATLAF_STYLE, FlatThemeTokenMaker.class.getName());

        // create text area
        textArea = new FlatSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        textArea.setMarkOccurrences(true);
        //textArea.addParser( new FlatThemeParser() );
//		textArea.setUseColorOfColorTokens( true );

        textArea.addPropertyChangeListener(TextEditorPane.DIRTY_PROPERTY, e -> {
            firePropertyChange(DIRTY_PROPERTY, e.getOldValue(), e.getNewValue());
        });

        // autocomplete
        CompletionProvider provider = new FlatCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setAutoCompleteSingleChoices(false);
        ac.setAutoActivationEnabled(true);
        ac.setParameterAssistanceEnabled(true);
        ac.setChoicesWindowSize(UIScale.scale(300), UIScale.scale(400));
        ac.setDescriptionWindowSize(UIScale.scale(300), UIScale.scale(400));
        ac.install(textArea);

        // create overlay layer

        // create scroll pane
        scrollPane = new RTextScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setLineNumbersEnabled(true);

        // map Ctrl+PageUp/Down to a not-existing action to avoid that the scrollpane catches them
        InputMap inputMap = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ctrl PAGE_UP"), "__dummy__");
        inputMap.put(KeyStroke.getKeyStroke("ctrl PAGE_DOWN"), "__dummy__");

        // create error strip
        errorStrip = new ErrorStrip(textArea);

        // create editor panel
        editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(scrollPane);
        editorPanel.add(errorStrip, BorderLayout.LINE_END);
        add(editorPanel, BorderLayout.CENTER);

        JMenuItem item = new JMenuItem("Find And Replace");

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                find();
            }};
        String keyStrokeAndKey = "control F";
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);
        textArea.getInputMap().put(keyStroke, keyStrokeAndKey);
        textArea.getActionMap().put(keyStrokeAndKey, action);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                find();
            }
        });
        textArea.getPopupMenu().add(item);

        updateTheme();
    }

    private void find() {
        showFindReplaceBar(true);
    }

    public void updateTheme() {
        Font font = createEditorFont(0);

        textArea.setFont(font);
        textArea.setBackground(UIManager.getColor("FlatThemeEditorPane.background"));
        textArea.setCaretColor(UIManager.getColor("FlatThemeEditorPane.caretColor"));
        textArea.setSelectionColor(UIManager.getColor("FlatThemeEditorPane.selectionBackground"));
        textArea.setCurrentLineHighlightColor(UIManager.getColor("FlatThemeEditorPane.currentLineHighlight"));
        textArea.setMarkAllHighlightColor(UIManager.getColor("FlatThemeEditorPane.markAllHighlightColor"));
        textArea.setMarkOccurrencesColor(UIManager.getColor("FlatThemeEditorPane.markOccurrencesColor"));
        textArea.setMatchedBracketBGColor(UIManager.getColor("FlatThemeEditorPane.matchedBracketBackground"));
        textArea.setMatchedBracketBorderColor(UIManager.getColor("FlatThemeEditorPane.matchedBracketBorderColor"));
        textArea.setPaintMatchedBracketPair(true);
        textArea.setAnimateBracketMatching(false);

        // syntax
        textArea.setSyntaxScheme(new FlatSyntaxScheme(font));

        // gutter
        Gutter gutter = scrollPane.getGutter();
        gutter.setBackground(UIManager.getColor("FlatThemeEditorPane.gutter.background"));
        gutter.setBorderColor(UIManager.getColor("FlatThemeEditorPane.gutter.borderColor"));
        gutter.setLineNumberColor(UIManager.getColor("FlatThemeEditorPane.gutter.lineNumberColor"));
        gutter.setLineNumberFont(font);

        // error strip
        errorStrip.setCaretMarkerColor(UIManager.getColor("FlatThemeEditorPane.errorstrip.caretMarkerColor"));

    }

    void updateFontSize(int sizeIncr) {
        Font font = createEditorFont(sizeIncr);

        textArea.setFont(font);
        textArea.setSyntaxScheme(new FlatSyntaxScheme(font));
        scrollPane.getGutter().setLineNumberFont(font);
    }

    private static Font createEditorFont(int sizeIncr) {
        int size = UIManager.getFont("defaultFont").getSize() + sizeIncr;
        Font font = FontUtils.getCompositeFont(FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, size);
        if (isFallbackFont(font)) {
            Font defaultFont = RTextArea.getDefaultFont();
            font = defaultFont.deriveFont((float) size);
        }
        return font;
    }

    private static boolean isFallbackFont(Font font) {
        return Font.DIALOG.equalsIgnoreCase(font.getFamily());
    }

    void selected() {
        if (findReplaceVisible) showFindReplaceBar(false);
        else hideFindReplaceBar();
    }

    @Override
    public boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();
    }


    File getFile() {
        return file;
    }

    void load(File file) throws IOException {
        this.file = file;

        textArea.load(FileLocation.create(file), "UTF-8");
    }

    boolean reloadIfNecessary() {
        if (!file.isFile()) {
            if (textArea.isDirty()) {
                if (JOptionPane.showOptionDialog(this, "The file '" + textArea.getFileName() + "' has been deleted. Replace the editor contents with these changes?", getWindowTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Save", "Close"}, "Save") == JOptionPane.YES_OPTION) {
                    saveIfDirty();
                    return true;
                }
            }

            return false;
        }

        if (textArea.isModifiedOutsideEditor()) {
            if (textArea.isDirty()) {
                if (JOptionPane.showConfirmDialog(this, "The file '" + textArea.getFileName() + "' has been changed. Replace the editor contents with these changes?", getWindowTitle(), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    textArea.syncLastSaveOrLoadTimeToActualFile();
                    return true;
                }
            }

            try {
                int selectionStart = textArea.getSelectionStart();
                int selectionEnd = textArea.getSelectionEnd();

                textArea.reload();

                textArea.select(selectionStart, selectionEnd);

                if (findReplaceBar != null && findReplaceBar.isShowing()) findReplaceBar.markAll();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to reload '" + textArea.getFileName() + "'\n\nReason: " + ex.getMessage(), getWindowTitle(), JOptionPane.WARNING_MESSAGE);
            }
        }

        return true;
    }

    boolean saveIfDirty() {
        try {
            if (textArea.isDirty()) textArea.save();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save '" + textArea.getFileName() + "'\n\nReason: " + ex.getMessage(), getWindowTitle(), JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    boolean isDirty() {
        return textArea.isDirty();
    }

    private String getWindowTitle() {
        Window window = SwingUtilities.windowForComponent(this);
        return (window instanceof JFrame) ? ((JFrame) window).getTitle() : null;
    }

    void showFindReplaceBar(boolean findEditorSelection) {
        if (findReplaceBar == null) {
            findReplaceBar = new FlatFindReplaceBar(textArea);
            findReplaceBar.addPropertyChangeListener(FlatFindReplaceBar.PROP_CLOSED, e -> {
                findReplaceVisible = false;
                textArea.requestFocusInWindow();
            });
            editorPanel.add(findReplaceBar, BorderLayout.SOUTH);
            editorPanel.revalidate();
        }

        findReplaceVisible = true;
        if (findReplaceContext == null) findReplaceContext = findReplaceBar.getSearchContext();
        else findReplaceBar.setSearchContext(findReplaceContext);

        findReplaceBar.setVisible(true);
        findReplaceBar.activate(findEditorSelection);
    }

    void hideFindReplaceBar() {
        if (findReplaceBar != null) findReplaceBar.setVisible(false);
    }

    void notifyTextAreaAction(String actionKey) {
        Action action = textArea.getActionMap().get(actionKey);
        if (action != null && action.isEnabled())
            action.actionPerformed(new ActionEvent(textArea, ActionEvent.ACTION_PERFORMED, null));
    }

    //---- class FlatSyntaxScheme ---------------------------------------------

    private static class FlatSyntaxScheme extends SyntaxScheme {
        FlatSyntaxScheme(Font baseFont) {
            super(false);

            Style[] styles = getStyles();
            for (int i = 0; i < styles.length; i++)
                styles[i] = new Style(Color.red);

            init("property", FlatThemeTokenMaker.TOKEN_PROPERTY, baseFont);
            init("variable", FlatThemeTokenMaker.TOKEN_VARIABLE, baseFont);
            init("number", FlatThemeTokenMaker.TOKEN_NUMBER, baseFont);
            init("color", FlatThemeTokenMaker.TOKEN_COLOR, baseFont);
            init("string", FlatThemeTokenMaker.TOKEN_STRING, baseFont);
            init("function", FlatThemeTokenMaker.TOKEN_FUNCTION, baseFont);
            init("type", FlatThemeTokenMaker.TOKEN_TYPE, baseFont);
            init("reservedWord", TokenTypes.RESERVED_WORD, baseFont);
            init("literalBoolean", TokenTypes.LITERAL_BOOLEAN, baseFont);
            init("operator", TokenTypes.OPERATOR, baseFont);
            init("separator", TokenTypes.SEPARATOR, baseFont);
            init("whitespace", TokenTypes.WHITESPACE, baseFont);
            init("comment", TokenTypes.COMMENT_EOL, baseFont);
        }

        private void init(String key, int token, Font baseFont) {
            String prefix = "FlatThemeEditorPane.style.";
            Color fg = UIManager.getColor(prefix + key);
            Color bg = UIManager.getColor(prefix + key + ".background");
            boolean italic = UIManager.getBoolean(prefix + key + ".italic");
            Font font = Style.DEFAULT_FONT;
            if (italic) font = baseFont.deriveFont(Font.ITALIC);
            getStyles()[token] = new Style(fg, bg, font);
        }
    }
}
