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

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import org.core.fonts.FlatInterFont;
import org.core.fonts.FlatJetBrainsMonoFont;
import org.core.fonts.FlatRobotoFont;
import org.core.fonts.FlatRobotoMonoFont;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * @author Karl Tauber
 */
public class SwingAdmin {
    static final String PREFS_ROOT_PATH = "/SwingAdmin";
    static final String KEY_TAB = "tab";

    static boolean screenshotsMode = Boolean.parseBoolean(System.getProperty("flatlaf.demo.screenshotsMode"));

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            System.setProperty("apple.awt.application.name", "FlatLaf Demo");

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (must be set on main thread and before AWT/Swing is initialized;
            //  setting it on AWT thread does not work)
            System.setProperty("apple.awt.application.appearance", "system");
        }

        // Linux
        if (SystemInfo.isLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        if (SwingAdmin.screenshotsMode && !SystemInfo.isJava_9_orLater && System.getProperty("flatlaf.uiScale") == null)
            System.setProperty("flatlaf.uiScale", "2x");

        SwingUtilities.invokeLater(() -> {
            AdminPrefs.init(PREFS_ROOT_PATH);

            // install fonts for lazy loading
            FlatInterFont.installLazy();
            FlatJetBrainsMonoFont.installLazy();
            FlatRobotoFont.installLazy();
            FlatRobotoMonoFont.installLazy();

            // use Inter font by default
//			FlatLaf.setPreferredFontFamily( FlatInterFont.FAMILY );
//			FlatLaf.setPreferredLightFontFamily( FlatInterFont.FAMILY_LIGHT );
//			FlatLaf.setPreferredSemiboldFontFamily( FlatInterFont.FAMILY_SEMIBOLD );

            // use Roboto font by default
//			FlatLaf.setPreferredFontFamily( FlatRobotoFont.FAMILY );
//			FlatLaf.setPreferredLightFontFamily( FlatRobotoFont.FAMILY_LIGHT );
//			FlatLaf.setPreferredSemiboldFontFamily( FlatRobotoFont.FAMILY_SEMIBOLD );

            // use JetBrains Mono font
//			FlatLaf.setPreferredMonospacedFontFamily( FlatJetBrainsMonoFont.FAMILY );

            // use Roboto Mono font
//			FlatLaf.setPreferredMonospacedFontFamily( FlatRobotoMonoFont.FAMILY );

            // application specific UI defaults
            FlatLaf.registerCustomDefaultsSource("org.core");

            // set look and feel
            AdminPrefs.setupLaf(args);

            // install inspectors
            FlatInspector.install("ctrl shift alt X");
            FlatUIDefaultsInspector.install("ctrl shift alt Y");

            // create frame
            MainFrame frame = new MainFrame();

            if (SwingAdmin.screenshotsMode) {
                frame.setPreferredSize(SystemInfo.isJava_9_orLater
                        ? new Dimension(830, 440)
                        : new Dimension(1660, 880));
            }
            // show frame
            frame.setSize(900,550);
            //frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
