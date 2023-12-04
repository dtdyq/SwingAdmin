package org.core.util;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.BiConsumer;

public class GridBagUtil {
    public static void layout(JPanel panel, Map<JComponent, String> components) {
        // cell 组件所在位置
        // wt 组件占用的空间比例0-1
        // span 组件占用的纵横格子数
        // fill 组件填充方向 none v h both
        // anchor 组件在格子中的位置 north east ...
        // ipad  组件内部空隙
        // insets  组件间距  一个值或四个值,逆时针
        // eg(cmd x y template) : "cell 0 0,wt 1 1,span 2 1,fill both,anchor north,ipad 1 1,insets 5"
        panel.setLayout(new GridBagLayout());
        components.forEach(new BiConsumer<JComponent, String>() {
            @Override
            public void accept(JComponent c, String s) {
                String[] constraints = s.split(",");
                GridBagConstraints gbc = new GridBagConstraints();
                for (String constraint : constraints) {
                    constraint = constraint.trim();
                    if ("".equals(constraint)) {
                        continue;
                    }
                    String[] cmd = constraint.split(" ");
                    if (cmd.length == 0) {
                        continue;
                    }
                    switch (cmd[0]) {
                        case "cell" -> {
                            gbc.gridx = Integer.parseInt(cmd[1]);
                            gbc.gridy = Integer.parseInt(cmd[2]);
                        }
                        case "wt" -> {
                            gbc.weightx = Float.parseFloat(cmd[1]);
                            gbc.weighty = Float.parseFloat(cmd[2]);
                        }
                        case "span" -> {
                            gbc.gridwidth = Integer.parseInt(cmd[1]);
                            gbc.gridheight = Integer.parseInt(cmd[2]);
                        }
                        case "fill" -> {
                            if ("none".equalsIgnoreCase(cmd[1])) {
                                gbc.fill = GridBagConstraints.NONE;
                            } else if ("both".equalsIgnoreCase(cmd[1])) {
                                gbc.fill = GridBagConstraints.BOTH;
                            } else if ("h".equalsIgnoreCase(cmd[1])) {
                                gbc.fill = GridBagConstraints.HORIZONTAL;
                            } else {
                                gbc.fill = GridBagConstraints.VERTICAL;
                            }
                        }
                        case "ipad" -> {
                            gbc.ipadx = Integer.parseInt(cmd[1]);
                            gbc.ipady = Integer.parseInt(cmd[2]);
                        }
                        case "insets" -> {
                            if (cmd.length == 2) {
                                int i = Integer.parseInt(cmd[1]);
                                gbc.insets = new Insets(i, i, i, i);
                            }
                            if (cmd.length == 5) {
                                gbc.insets = new Insets(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]), Integer.parseInt(cmd[4]));
                            }
                        }
                        case "anchor" ->{
                            if ("north".equalsIgnoreCase(cmd[1])) {
                                gbc.anchor = GridBagConstraints.NORTH;
                            }
                        }
                    }
                }
                System.out.printf("%d:%d-%f:%f%n", gbc.gridx, gbc.gridy, gbc.weightx, gbc.weighty);
                panel.add(c, gbc);
            }
        });
    }
}
