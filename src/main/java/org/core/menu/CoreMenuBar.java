package org.core.menu;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.ui.JBRCustomDecorations;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import org.core.AdminPrefs;
import org.core.ConfigMgr;
import org.core.HintManager;
import org.core.intellijthemes.IJThemeInfo;
import org.core.rtext.FlatThemeEditorPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import static org.core.HintManager.hideAllHints;
import static org.core.HintManager.showHint;

public class CoreMenuBar extends JMenuBar {

    //=====================font begin================
    private String[] availableFontFamilyNames;
    private int initialFontMenuItemCount = -1;
    private JMenu fontMenu;
    //=====================font end================

    //===============options begin=================
    private JMenu optionsMenu;

    private JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem;
    private JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem;
    private JCheckBoxMenuItem unifiedTitleBarMenuItem;
    private JCheckBoxMenuItem showTitleBarIconMenuItem;
    private JCheckBoxMenuItem underlineMenuSelectionMenuItem;
    private JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
    private JCheckBoxMenuItem animatedLafChangeMenuItem;
    //===============options end==================

    public CoreMenuBar() {
        initComponent();
    }

    private void initComponent() {
        initFontMenu();
        initOptionsMenu();
        initHelpMenu();
        initRightIconMenu();
    }

    private void initRightIconMenu() {

        FlatButton usersButton = new FlatButton();
        usersButton.setIcon(new FlatSVGIcon("icons/users.svg"));
        usersButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        usersButton.setFocusable(false);
        usersButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Hello User! How are you?", "User", JOptionPane.INFORMATION_MESSAGE));
        add(Box.createGlue());
        add(usersButton);
    }

    private void initHelpMenu() {
        //======== helpMenu ========
        {
            JMenu helpMenu = new JMenu();
            helpMenu.setText("Help");
            helpMenu.setMnemonic('H');

            //---- aboutMenuItem ----
            JMenuItem aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText("About");
            aboutMenuItem.setMnemonic('A');
            aboutMenuItem.addActionListener(e -> aboutActionPerformed());

            FlatDesktop.setAboutHandler(this::aboutActionPerformed);
            helpMenu.add(aboutMenuItem);
            add(helpMenu);
        }
    }

    private void aboutActionPerformed() {
        JLabel titleLabel = new JLabel(ConfigMgr.getInstance().getOr("menu.help.about.title", ""));
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        String link = "https://www.formdev.com/flatlaf/";
        JLabel linkLabel = new JLabel("<html><a href=\"#\">" + link + "</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(link));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(linkLabel, "Failed to open '" + link + "' in browser.", "About", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });


        JOptionPane.showMessageDialog(this.getParent(), new Object[]{titleLabel, ConfigMgr.getInstance().getOr("menu.help.about.text", ""), linkLabel,}, "About", JOptionPane.PLAIN_MESSAGE);
    }

    private void initOptionsMenu() {
        if (ConfigMgr.getInstance().getBoolOr("menu.options.show", true)) {
            optionsMenu = new JMenu();
            optionsMenu.setText("Options");
            windowDecorationsCheckBoxMenuItem = new JCheckBoxMenuItem();
            menuBarEmbeddedCheckBoxMenuItem = new JCheckBoxMenuItem();
            unifiedTitleBarMenuItem = new JCheckBoxMenuItem();
            showTitleBarIconMenuItem = new JCheckBoxMenuItem();
            underlineMenuSelectionMenuItem = new JCheckBoxMenuItem();
            alwaysShowMnemonicsMenuItem = new JCheckBoxMenuItem();
            animatedLafChangeMenuItem = new JCheckBoxMenuItem();
            JCheckBoxMenuItem showHintsMenuItem = new JCheckBoxMenuItem();
            JCheckBoxMenuItem showUIDefaultsInspectorMenuItem = new JCheckBoxMenuItem();

            //---- windowDecorationsCheckBoxMenuItem ----
            windowDecorationsCheckBoxMenuItem.setText("Window decorations");
            windowDecorationsCheckBoxMenuItem.addActionListener(e -> windowDecorationsChanged());
            optionsMenu.add(windowDecorationsCheckBoxMenuItem);

            //---- menuBarEmbeddedCheckBoxMenuItem ----
            menuBarEmbeddedCheckBoxMenuItem.setText("Embedded menu bar");
            menuBarEmbeddedCheckBoxMenuItem.addActionListener(e -> menuBarEmbeddedChanged());
            optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem);

            //---- unifiedTitleBarMenuItem ----
            unifiedTitleBarMenuItem.setText("Unified window title bar");
            unifiedTitleBarMenuItem.addActionListener(e -> unifiedTitleBar());
            optionsMenu.add(unifiedTitleBarMenuItem);

            //---- showTitleBarIconMenuItem ----
            showTitleBarIconMenuItem.setText("Show window title bar icon");
            showTitleBarIconMenuItem.addActionListener(e -> showTitleBarIcon());
            optionsMenu.add(showTitleBarIconMenuItem);

            //---- underlineMenuSelectionMenuItem ----
            underlineMenuSelectionMenuItem.setText("Use underline menu selection");
            underlineMenuSelectionMenuItem.addActionListener(e -> underlineMenuSelection());
            optionsMenu.add(underlineMenuSelectionMenuItem);

            //---- alwaysShowMnemonicsMenuItem ----
            alwaysShowMnemonicsMenuItem.setText("Always show mnemonics");
            alwaysShowMnemonicsMenuItem.addActionListener(e -> alwaysShowMnemonics());
            optionsMenu.add(alwaysShowMnemonicsMenuItem);

            //---- animatedLafChangeMenuItem ----
            animatedLafChangeMenuItem.setText("Animated Laf Change");
            animatedLafChangeMenuItem.setSelected(true);
            animatedLafChangeMenuItem.addActionListener(e -> animatedLafChangeChanged());
            optionsMenu.add(animatedLafChangeMenuItem);

            //---- showHintsMenuItem ----
            showHintsMenuItem.setText("Show hints");
            showHintsMenuItem.addActionListener(e -> showHintsChanged());
            optionsMenu.add(showHintsMenuItem);

            //---- showUIDefaultsInspectorMenuItem ----
            showUIDefaultsInspectorMenuItem.setText("Show UI Defaults Inspector");
            showUIDefaultsInspectorMenuItem.addActionListener(e -> showUIDefaultsInspector());
            optionsMenu.add(showUIDefaultsInspectorMenuItem);
            initThemeMenu();

            if (FlatLaf.supportsNativeWindowDecorations() || (SystemInfo.isLinux && JFrame.isDefaultLookAndFeelDecorated())) {
                if (SystemInfo.isLinux) unsupported(windowDecorationsCheckBoxMenuItem);
                else windowDecorationsCheckBoxMenuItem.setSelected(FlatLaf.isUseNativeWindowDecorations());
                menuBarEmbeddedCheckBoxMenuItem.setSelected(UIManager.getBoolean("TitlePane.menuBarEmbedded"));
                unifiedTitleBarMenuItem.setSelected(UIManager.getBoolean("TitlePane.unifiedBackground"));
                showTitleBarIconMenuItem.setSelected(UIManager.getBoolean("TitlePane.showIcon"));

                if (JBRCustomDecorations.isSupported()) {
                    // If the JetBrains Runtime is used, it forces the use of it's own custom
                    // window decoration, which can not disabled.
                    windowDecorationsCheckBoxMenuItem.setEnabled(false);
                }
            } else {
                unsupported(windowDecorationsCheckBoxMenuItem);
                unsupported(menuBarEmbeddedCheckBoxMenuItem);
                unsupported(unifiedTitleBarMenuItem);
                unsupported(showTitleBarIconMenuItem);
            }

            if (SystemInfo.isMacOS) unsupported(underlineMenuSelectionMenuItem);
            add(optionsMenu);
            SwingUtilities.invokeLater(this::showHints);
        }
    }

    private void initThemeMenu() {
        ButtonGroup group = new ButtonGroup();
        optionsMenu.addSeparator();
        JMenu select = new JMenu("Select Theme");
        optionsMenu.add(select);
        List<IJThemeInfo> themes = new ArrayList<>();
        themes.add(new IJThemeInfo("FlatLaf Light", null, false, null, null, null, null, null, FlatLightLaf.class.getName()));
        themes.add(new IJThemeInfo("FlatLaf Dark", null, true, null, null, null, null, null, FlatDarkLaf.class.getName()));
        themes.add(new IJThemeInfo("FlatLaf IntelliJ", null, false, null, null, null, null, null, FlatIntelliJLaf.class.getName()));
        themes.add(new IJThemeInfo("FlatLaf Darcula", null, true, null, null, null, null, null, FlatDarculaLaf.class.getName()));
        themes.add(new IJThemeInfo("FlatLaf macOS Light", null, false, null, null, null, null, null, FlatMacLightLaf.class.getName()));
        themes.add(new IJThemeInfo("FlatLaf macOS Dark", null, true, null, null, null, null, null, FlatMacDarkLaf.class.getName()));
        themes.forEach(new Consumer<IJThemeInfo>() {
            @Override
            public void accept(IJThemeInfo ijThemeInfo) {
                JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(ijThemeInfo.getName());
                group.add(menuItem);
                select.add(menuItem);
                menuItem.addActionListener(e -> setTheme(ijThemeInfo));
            }
        });
    }

    private void showInformationDialog(String message, Exception ex) {
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this),
                message + "\n\n" + ex.getMessage(),
                "FlatLaf", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setTheme(IJThemeInfo themeInfo) {
        if (themeInfo == null)
            return;

        // change look and feel
        if (themeInfo.getLafClassName() != null) {
            if (themeInfo.getLafClassName().equals(UIManager.getLookAndFeel().getClass().getName()))
                return;

            FlatAnimatedLafChange.showSnapshot();

            try {
                UIManager.setLookAndFeel(themeInfo.getLafClassName());
            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
                showInformationDialog("Failed to create '" + themeInfo.getLafClassName() + "'.", ex);
            }
        } else if (themeInfo.getThemeFile() != null) {
            FlatAnimatedLafChange.showSnapshot();

            try {
                if (themeInfo.getThemeFile().getName().endsWith(".properties")) {
                    FlatLaf.setup(new FlatPropertiesLaf(themeInfo.getName(), themeInfo.getThemeFile()));
                } else
                    FlatLaf.setup(IntelliJTheme.createLaf(new FileInputStream(themeInfo.getThemeFile())));

                AdminPrefs.getState().put(AdminPrefs.KEY_LAF_THEME, AdminPrefs.FILE_PREFIX + themeInfo.getThemeFile());
            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
                showInformationDialog("Failed to load '" + themeInfo.getThemeFile() + "'.", ex);
            }
        } else {
            FlatAnimatedLafChange.showSnapshot();

            IntelliJTheme.setup(getClass().getResourceAsStream(THEMES_PACKAGE + themeInfo.getResourceName()));
            AdminPrefs.getState().put(AdminPrefs.KEY_LAF_THEME, AdminPrefs.RESOURCE_PREFIX + themeInfo.getResourceName());
        }

        // update all components
        FlatLaf.updateUI();
        FlatThemeEditorPane.panes.forEach(new Consumer<FlatThemeEditorPane>() {
            @Override
            public void accept(FlatThemeEditorPane flatThemeEditorPane) {
                flatThemeEditorPane.updateTheme();
            }
        });
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static final String THEMES_PACKAGE = "/themes/";

    private void showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show();
    }

    private void unsupported(JCheckBoxMenuItem menuItem) {
        menuItem.setEnabled(false);
        menuItem.setSelected(false);
        menuItem.setToolTipText("Not supported on your system.");
    }


    private void showHintsChanged() {
        clearHints();
        showHints();
    }

    private void showHints() {
        HintManager.Hint fontMenuHint = new HintManager.Hint("Use 'Font' menu to increase/decrease font size or try different fonts.", fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", null);

        HintManager.Hint optionsMenuHint = new HintManager.Hint("Use 'Options' menu to try out various FlatLaf options.", optionsMenu, SwingConstants.BOTTOM, "hint.optionsMenu", fontMenuHint);

        showHint(optionsMenuHint);
    }

    private void clearHints() {
        hideAllHints();

        Preferences state = AdminPrefs.getState();
        state.remove("hint.fontMenu");
        state.remove("hint.optionsMenu");
        state.remove("hint.themesPanel");
    }

    private void windowDecorationsChanged() {
        boolean windowDecorations = windowDecorationsCheckBoxMenuItem.isSelected();

        // change window decoration of all frames and dialogs
        FlatLaf.setUseNativeWindowDecorations(windowDecorations);

        menuBarEmbeddedCheckBoxMenuItem.setEnabled(windowDecorations);
        unifiedTitleBarMenuItem.setEnabled(windowDecorations);
        showTitleBarIconMenuItem.setEnabled(windowDecorations);
    }

    private void menuBarEmbeddedChanged() {
        UIManager.put("TitlePane.menuBarEmbedded", menuBarEmbeddedCheckBoxMenuItem.isSelected());
        FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
    }

    private void unifiedTitleBar() {
        UIManager.put("TitlePane.unifiedBackground", unifiedTitleBarMenuItem.isSelected());
        FlatLaf.repaintAllFramesAndDialogs();
    }

    private void showTitleBarIcon() {
        boolean showIcon = showTitleBarIconMenuItem.isSelected();

        // for main frame (because already created)
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICON, showIcon);

        // for other not yet created frames/dialogs
        UIManager.put("TitlePane.showIcon", showIcon);
    }

    private void underlineMenuSelection() {
        UIManager.put("MenuItem.selectionType", underlineMenuSelectionMenuItem.isSelected() ? "underline" : null);
    }

    private void alwaysShowMnemonics() {
        UIManager.put("Component.hideMnemonics", !alwaysShowMnemonicsMenuItem.isSelected());
        repaint();
    }

    private void animatedLafChangeChanged() {
        System.setProperty("flatlaf.animatedLafChange", String.valueOf(animatedLafChangeMenuItem.isSelected()));
    }


    private void initFontMenu() {
        if (ConfigMgr.getInstance().getBoolOr("menu.font.show", true)) {
            availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
            Arrays.sort(availableFontFamilyNames);
            fontMenu = new JMenu();
            fontMenu.setText("Font");
            JMenuItem restoreFontMenuItem = new JMenuItem();
            JMenuItem incrFontMenuItem = new JMenuItem();
            JMenuItem decrFontMenuItem = new JMenuItem();
            //---- restoreFontMenuItem ----
            restoreFontMenuItem.setText("Restore Font");
            restoreFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            restoreFontMenuItem.addActionListener(e -> restoreFont());
            fontMenu.add(restoreFontMenuItem);

            //---- incrFontMenuItem ----
            incrFontMenuItem.setText("Increase Font Size");
            incrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            incrFontMenuItem.addActionListener(e -> incrFont());
            fontMenu.add(incrFontMenuItem);

            //---- decrFontMenuItem ----
            decrFontMenuItem.setText("Decrease Font Size");
            decrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            decrFontMenuItem.addActionListener(e -> decrFont());
            fontMenu.add(decrFontMenuItem);
            add(fontMenu);
            updateFontMenuItems();
        }
    }

    private void restoreFont() {
        UIManager.put("defaultFont", null);
        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void incrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) (font.getSize() + 1));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void decrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Math.max(font.getSize() - 1, 10));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    public void updateFontMenuItems() {
        if (initialFontMenuItemCount < 0) initialFontMenuItemCount = fontMenu.getItemCount();
        else {
            // remove old font items
            for (int i = fontMenu.getItemCount() - 1; i >= initialFontMenuItemCount; i--)
                fontMenu.remove(i);
        }

        // get current font
        Font currentFont = UIManager.getFont("Label.font");
        String currentFamily = currentFont.getFamily();
        String currentSize = Integer.toString(currentFont.getSize());

        // add font families
        fontMenu.addSeparator();
        ArrayList<String> families = new ArrayList<>(Arrays.asList("Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans", "Dialog", "Inter", "Liberation Sans", "Noto Sans", "Open Sans", "Roboto", "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
        if (!families.contains(currentFamily)) families.add(currentFamily);
        families.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup familiesGroup = new ButtonGroup();
        for (String family : families) {
            if (Arrays.binarySearch(availableFontFamilyNames, family) < 0) continue; // not available

            JCheckBoxMenuItem item = new JCheckBoxMenuItem(family);
            item.setSelected(family.equals(currentFamily));
            item.addActionListener(this::fontFamilyChanged);
            fontMenu.add(item);

            familiesGroup.add(item);
        }

        // add font sizes
        fontMenu.addSeparator();
        ArrayList<String> sizes = new ArrayList<>(Arrays.asList("10", "11", "12", "14", "16", "18", "20", "24", "28"));
        if (!sizes.contains(currentSize)) sizes.add(currentSize);
        sizes.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup sizesGroup = new ButtonGroup();
        for (String size : sizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(size);
            item.setSelected(size.equals(currentSize));
            item.addActionListener(this::fontSizeChanged);
            fontMenu.add(item);

            sizesGroup.add(item);
        }

        // enabled/disable items
        boolean enabled = UIManager.getLookAndFeel() instanceof FlatLaf;
        for (Component item : fontMenu.getMenuComponents())
            item.setEnabled(enabled);
    }


    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = e.getActionCommand();

        FlatAnimatedLafChange.showSnapshot();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = FontUtils.getCompositeFont(fontFamily, font.getStyle(), font.getSize());
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private void fontSizeChanged(ActionEvent e) {
        String fontSizeStr = e.getActionCommand();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Integer.parseInt(fontSizeStr));
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
    }
}
