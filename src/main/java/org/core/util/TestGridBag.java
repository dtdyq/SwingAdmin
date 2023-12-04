package org.core.util;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestGridBag {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        JLabel un = new JLabel("username:");
        JLabel pd = new JLabel("password:");
        JLabel desc = new JLabel("descript:");
        JTextField unF = new JTextField();
        JTextField pdF = new JTextField();
        JScrollPane descPane = new JScrollPane(new JTextPane());
        descPane.setPreferredSize(new Dimension(200,110));
        Map<JComponent,String> layout = new LinkedHashMap<>();
        layout.put(un,"cell 0 0,insets 5");
        layout.put(unF,"cell 1 0,wt 1 0,fill h,insets 5");
        layout.put(pd,"cell 0 1,insets 5");
        layout.put(pdF,"cell 1 1,fill h,insets 5");
        layout.put(desc,"cell 0 2,insets 5,anchor north");
        layout.put(descPane,"cell 1 2,wt 1 1,fill both,insets 5");

        JPanel h = new JPanel();
        BoxLayout boxLayout = new BoxLayout(h,BoxLayout.X_AXIS);
        JButton b = new JButton("Ok");
        h.add(b);
        h.add(new JButton("Cancel"));
        layout.put(h,"cell 1 3");

        GridBagUtil.layout(panel,layout);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setTitle("GridBagTest");
        b.grabFocus();
        frame.setVisible(true);
    }
}
