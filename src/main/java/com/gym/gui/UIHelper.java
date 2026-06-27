package com.gym.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Helper class containing static utility methods to create styled UI components,
 * preventing code duplication and keeping GUI classes short and cohesive.
 */
public class UIHelper {

    /**
     * Creates a modern outline style button that fills on hover.
     */
    public static JButton createOutlineButton(String text, Color themeColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(themeColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeColor, 2),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setOpaque(true);
                btn.setBackground(themeColor);
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setOpaque(false);
                btn.setForeground(themeColor);
                btn.repaint();
            }
        });
        return btn;
    }

    /**
     * Creates a modern solid styled button that changes color shade on hover.
     */
    public static JButton createSolidButton(String text, Color bgColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    /**
     * Creates a read-only table model to prevent user-edits directly inside table grids.
     */
    public static DefaultTableModel createReadOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }
}
