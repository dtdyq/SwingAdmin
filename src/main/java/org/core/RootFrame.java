package org.core;

import org.core.rtext.FlatThemeEditorPane;

import javax.swing.*;
import java.awt.*;

public class RootFrame extends JPanel {
    public RootFrame() {
        setLayout(new BorderLayout());
        add(new FlatThemeEditorPane(), BorderLayout.CENTER);
    }
}
