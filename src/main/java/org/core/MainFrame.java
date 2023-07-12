/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.core;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;
import org.core.extras.ExtrasPanel;
import org.core.intellijthemes.IJThemesPanel;
import org.core.menu.CoreMenuBar;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author Karl Tauber
 */
class MainFrame extends JFrame {

    MainFrame() {
        int tabIndex = AdminPrefs.getState().getInt(SwingAdmin.KEY_TAB, 0);


        initComponents();
        initAccentColors();
        controlBar.initialize(this, tabbedPane);

        setIconImages(FlatSVGUtils.createWindowIconImages(ConfigMgr.getInstance().getOr("app.icon", "/FlatLaf.svg")));

        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount() && tabIndex != tabbedPane.getSelectedIndex())
            tabbedPane.setSelectedIndex(tabIndex);

        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // hide menu items that are in macOS application menu
            exitMenuItem.setVisible(false);
            aboutMenuItem.setVisible(false);

            // do not use HTML text in menu items because this is not supported in macOS screen menu
            htmlMenuItem.setText("some text");

            if (SystemInfo.isMacFullWindowContentSupported) {
                // expand window content into window title bar and make title bar transparent
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);

                // hide window title
                if (SystemInfo.isJava_17_orLater)
                    getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
                else setTitle(null);

                // add gap to left side of toolbar
                toolBar.add(Box.createHorizontalStrut(70), 0);
            }

            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater) getRootPane().putClientProperty("apple.awt.fullscreenable", true);
        }

        // integrate into macOS screen menu
        FlatDesktop.setPreferencesHandler(this::showPreferences);
        FlatDesktop.setQuitHandler(FlatDesktop.QuitResponse::performQuit);

    }

    @Override
    public void dispose() {
        super.dispose();

        FlatUIDefaultsInspector.hide();
    }


    private void showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show();
    }

    private void newActionPerformed() {
        NewDialog newDialog = new NewDialog(this);
        newDialog.setVisible(true);
    }

    private void openActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(this);
    }

    private void saveAsActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(this);
    }

    private void exitActionPerformed() {
        dispose();
    }


    private void showPreferences() {
        JOptionPane.showMessageDialog(this, "Sorry, but FlatLaf Demo does not have preferences. :(\n" + "This dialog is here to demonstrate usage of class 'FlatDesktop' on macOS.", "Preferences", JOptionPane.PLAIN_MESSAGE);
    }

    private void selectedTabChanged() {
        AdminPrefs.getState().putInt(SwingAdmin.KEY_TAB, tabbedPane.getSelectedIndex());
    }

    private void menuItemActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE);
        });
    }

    // the real colors are defined in
    // flatlaf-demo/src/main/resources/FlatLightLaf.properties and
    // flatlaf-demo/src/main/resources/FlatDarkLaf.properties
    private static String[] accentColorKeys = {"Demo.accent.default", "Demo.accent.blue", "Demo.accent.purple", "Demo.accent.red", "Demo.accent.orange", "Demo.accent.yellow", "Demo.accent.green",};
    private static String[] accentColorNames = {"Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green",};
    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];
    private JLabel accentColorLabel;
    private Color accentColor;

    private void initAccentColors() {
        accentColorLabel = new JLabel("Accent color: ");

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(accentColorLabel);

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i] = new JToggleButton(new AccentColorIcon(accentColorKeys[i]));
            accentColorButtons[i].setToolTipText(accentColorNames[i]);
            accentColorButtons[i].addActionListener(this::accentColorChanged);
            toolBar.add(accentColorButtons[i]);
            group.add(accentColorButtons[i]);
        }

        accentColorButtons[0].setSelected(true);

        FlatLaf.setSystemColorGetter(name -> {
            return name.equals("accent") ? accentColor : null;
        });

        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) updateAccentColorButtons();
        });
        updateAccentColorButtons();
    }

    private void accentColorChanged(ActionEvent e) {
        String accentColorKey = null;
        for (int i = 0; i < accentColorButtons.length; i++) {
            if (accentColorButtons[i].isSelected()) {
                accentColorKey = accentColorKeys[i];
                break;
            }
        }

        accentColor = (accentColorKey != null && accentColorKey != accentColorKeys[0]) ? UIManager.getColor(accentColorKey) : null;

        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        try {
            FlatLaf.setup(lafClass.getDeclaredConstructor().newInstance());
            FlatLaf.updateUI();
        } catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    private void updateAccentColorButtons() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        boolean isAccentColorSupported = lafClass == FlatLightLaf.class || lafClass == FlatDarkLaf.class || lafClass == FlatIntelliJLaf.class || lafClass == FlatDarculaLaf.class || lafClass == FlatMacLightLaf.class || lafClass == FlatMacDarkLaf.class;

        accentColorLabel.setVisible(isAccentColorSupported);
        for (int i = 0; i < accentColorButtons.length; i++)
            accentColorButtons[i].setVisible(isAccentColorSupported);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        JMenuBar menuBar1 = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem newMenuItem = new JMenuItem();
        JMenuItem openMenuItem = new JMenuItem();
        JMenuItem saveAsMenuItem = new JMenuItem();
        JMenuItem closeMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        JMenu editMenu = new JMenu();
        JMenuItem undoMenuItem = new JMenuItem();
        JMenuItem redoMenuItem = new JMenuItem();
        JMenuItem cutMenuItem = new JMenuItem();
        JMenuItem copyMenuItem = new JMenuItem();
        JMenuItem pasteMenuItem = new JMenuItem();
        JMenuItem deleteMenuItem = new JMenuItem();
        JMenu viewMenu = new JMenu();
        JCheckBoxMenuItem checkBoxMenuItem1 = new JCheckBoxMenuItem();
        JMenu menu1 = new JMenu();
        JMenu subViewsMenu = new JMenu();
        JMenu subSubViewsMenu = new JMenu();
        JMenuItem errorLogViewMenuItem = new JMenuItem();
        JMenuItem searchViewMenuItem = new JMenuItem();
        JMenuItem projectViewMenuItem = new JMenuItem();
        JMenuItem structureViewMenuItem = new JMenuItem();
        JMenuItem propertiesViewMenuItem = new JMenuItem();
        scrollingPopupMenu = new JMenu();
        JMenuItem menuItem2 = new JMenuItem();
        htmlMenuItem = new JMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();
        fontMenu = new JMenu();
        JMenu helpMenu = new JMenu();
        aboutMenuItem = new JMenuItem();
        toolBar = new JToolBar();
        JButton backButton = new JButton();
        JButton forwardButton = new JButton();
        JButton cutButton = new JButton();
        JButton copyButton = new JButton();
        JButton pasteButton = new JButton();
        JButton refreshButton = new JButton();
        JToggleButton showToggleButton = new JToggleButton();
        JPanel contentPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        BasicComponentsPanel basicComponentsPanel = new BasicComponentsPanel();
        MoreComponentsPanel moreComponentsPanel = new MoreComponentsPanel();
        DataComponentsPanel dataComponentsPanel = new DataComponentsPanel();
        TabsPanel tabsPanel = new TabsPanel();
        OptionPanePanel optionPanePanel = new OptionPanePanel();
        ExtrasPanel extrasPanel1 = new ExtrasPanel();
        controlBar = new ControlBar();
        themesPanel = new IJThemesPanel();

        //======== this ========
        setTitle(ConfigMgr.getInstance().getOr("app.title", "Swing Admin"));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        setJMenuBar(new CoreMenuBar());

        //======== toolBar ========
        {
            toolBar.setMargin(new Insets(3, 3, 0, 3));
            toolBar.setVisible(false);
            //---- backButton ----
            backButton.setToolTipText("Back");
            toolBar.add(backButton);

            //---- forwardButton ----
            forwardButton.setToolTipText("Forward");
            toolBar.add(forwardButton);
            toolBar.addSeparator();

            //---- cutButton ----
            cutButton.setToolTipText("Cut");
            toolBar.add(cutButton);

            //---- copyButton ----
            copyButton.setToolTipText("Copy");
            toolBar.add(copyButton);

            //---- pasteButton ----
            pasteButton.setToolTipText("Paste");
            toolBar.add(pasteButton);
            toolBar.addSeparator();

            //---- refreshButton ----
            refreshButton.setToolTipText("Refresh");
            toolBar.add(refreshButton);
            toolBar.addSeparator();

            //---- showToggleButton ----
            showToggleButton.setSelected(true);
            showToggleButton.setToolTipText("Show Details");
            toolBar.add(showToggleButton);
        }
        contentPane.add(toolBar, BorderLayout.NORTH);

        //======== contentPanel ========
        {
            contentPanel.setLayout(new MigLayout("insets dialog,hidemode 3",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));

            //======== tabbedPane ========
            {
                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                tabbedPane.addChangeListener(e -> selectedTabChanged());
                tabbedPane.addTab("Basic Components", basicComponentsPanel);
                tabbedPane.addTab("More Components", moreComponentsPanel);
                tabbedPane.addTab("Data Components", dataComponentsPanel);
                tabbedPane.addTab("Tabs", tabsPanel);
                tabbedPane.addTab("Option Pane", optionPanePanel);
                tabbedPane.addTab("Extras", extrasPanel1);
            }
            contentPanel.add(tabbedPane, "cell 0 0");
        }
        contentPane.add(new RootFrame(), BorderLayout.CENTER);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioButtonMenuItem1);
        buttonGroup1.add(radioButtonMenuItem2);
        buttonGroup1.add(radioButtonMenuItem3);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        // add "Users" button to menubar

        undoMenuItem.setIcon(new FlatSVGIcon("icons/undo.svg"));
        redoMenuItem.setIcon(new FlatSVGIcon("icons/redo.svg"));

        cutMenuItem.setIcon(new FlatSVGIcon("icons/menu-cut.svg"));
        copyMenuItem.setIcon(new FlatSVGIcon("icons/copy.svg"));
        pasteMenuItem.setIcon(new FlatSVGIcon("icons/menu-paste.svg"));

        backButton.setIcon(new FlatSVGIcon("icons/back.svg"));
        forwardButton.setIcon(new FlatSVGIcon("icons/forward.svg"));
        cutButton.setIcon(new FlatSVGIcon("icons/menu-cut.svg"));
        copyButton.setIcon(new FlatSVGIcon("icons/copy.svg"));
        pasteButton.setIcon(new FlatSVGIcon("icons/menu-paste.svg"));
        refreshButton.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        showToggleButton.setIcon(new FlatSVGIcon("icons/show.svg"));

        cutMenuItem.addActionListener(new DefaultEditorKit.CutAction());
        copyMenuItem.addActionListener(new DefaultEditorKit.CopyAction());
        pasteMenuItem.addActionListener(new DefaultEditorKit.PasteAction());

        scrollingPopupMenu.add("Large menus are scrollable");
        scrollingPopupMenu.add("Use mouse wheel to scroll");
        scrollingPopupMenu.add("Or use up/down arrows at top/bottom");
        for (int i = 1; i <= 100; i++)
            scrollingPopupMenu.add("Item " + i);


        // remove contentPanel bottom insets
        MigLayout layout = (MigLayout) contentPanel.getLayout();
        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
        UnitValue[] insets = lc.getInsets();
        lc.setInsets(new UnitValue[]{insets[0], insets[1], new UnitValue(0, UnitValue.PIXEL, null), insets[3]});
        layout.setLayoutConstraints(lc);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuItem exitMenuItem;
    private JMenu scrollingPopupMenu;
    private JMenuItem htmlMenuItem;
    private JMenu fontMenu;
    private JMenu optionsMenu;
    private JMenuItem aboutMenuItem;
    private JToolBar toolBar;
    private JTabbedPane tabbedPane;
    private ControlBar controlBar;
    IJThemesPanel themesPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    //---- class AccentColorIcon ----------------------------------------------

    private static class AccentColorIcon extends FlatAbstractIcon {
        private final String colorKey;

        AccentColorIcon(String colorKey) {
            super(16, 16, null);
            this.colorKey = colorKey;
        }

        @Override
        protected void paintIcon(Component c, Graphics2D g) {
            Color color = UIManager.getColor(colorKey);
            if (color == null) color = Color.lightGray;
            else if (!c.isEnabled()) {
                color = FlatLaf.isLafDark() ? ColorFunctions.shade(color, 0.5f) : ColorFunctions.tint(color, 0.6f);
            }

            g.setColor(color);
            g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
        }
    }
}
