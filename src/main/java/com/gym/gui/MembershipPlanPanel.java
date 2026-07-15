package com.gym.gui;

import com.gym.model.MembershipPlan;
import com.gym.service.MembershipPlanService;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MembershipPlanPanel extends JPanel {

    private final MembershipPlanService planService;
    private final JTable tblPlan;
    private final DefaultTableModel model;

    public MembershipPlanPanel(MembershipPlanService planService) {
        this.planService = planService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Column headers for the plan list
        String[] cols = {
            "Plan ID",
            "Name",
            "Plan Price",
            "Duration (months)",
        };

        // Table model configured to be read-only
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize table and add to scroll pane in the center
        tblPlan = new JTable(model);
        tblPlan.setFont(new Font("Arial", Font.PLAIN, 13));
        tblPlan.setRowHeight(22);
        add(new JScrollPane(tblPlan), BorderLayout.CENTER);

        // Buttons container panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));

        // --- ADD BUTTON (Green Outline) ---
        Color greenTheme = new Color(46, 204, 113);
        JButton btnAdd = new JButton("Add New Plan");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setOpaque(false);
        btnAdd.setForeground(greenTheme);
        btnAdd.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(greenTheme, 2),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
            )
        );

        btnAdd.addMouseListener(
            new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnAdd.setOpaque(true);
                    btnAdd.setBackground(greenTheme);
                    btnAdd.setForeground(Color.WHITE);
                    btnAdd.repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnAdd.setOpaque(false);
                    btnAdd.setForeground(greenTheme);
                    btnAdd.repaint();
                }
            }
        );

        // --- DELETE BUTTON (Red Outline) ---
        Color redTheme = new Color(231, 76, 60);
        JButton btnDel = new JButton("Delete Plan");
        btnDel.setFont(new Font("Arial", Font.BOLD, 13));
        btnDel.setFocusPainted(false);
        btnDel.setContentAreaFilled(false);
        btnDel.setOpaque(false);
        btnDel.setForeground(redTheme);
        btnDel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(redTheme, 2),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
            )
        );

        btnDel.addMouseListener(
            new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnDel.setOpaque(true);
                    btnDel.setBackground(redTheme);
                    btnDel.setForeground(Color.WHITE);
                    btnDel.repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnDel.setOpaque(false);
                    btnDel.setForeground(redTheme);
                    btnDel.repaint();
                }
            }
        );

        // Add action listeners for backend functions
        btnAdd.addActionListener(e -> createPlanForm());
        btnDel.addActionListener(e -> deleteSelectedPlan());

        btnPanel.add(btnAdd);
        btnPanel.add(btnDel);
        add(btnPanel, BorderLayout.SOUTH);

        // Load data on startup
        refreshTable();
    }

    /**
     * Refreshes the plan table with the latest database records
     */
    public void refreshTable() {
        model.setRowCount(0);
        for (MembershipPlan plan : planService.findAll()) {
            model.addRow(new Object[] {
                plan.getPlanID(),
                plan.getPlanName(),
                "$" + plan.getPlanPrice(),
                plan.getDuration(),
            });
        }
    }

    /**
     * Shows a formatted input form dialog to add a new plan
     */
    private void createPlanForm() {
        Font inputFont = new Font("Arial", Font.PLAIN, 16);
        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel nameLbl = new JLabel("Plan Name:", SwingConstants.CENTER);
        nameLbl.setFont(labelFont);
        JTextField nameFld = new JTextField(15);
        nameFld.setFont(inputFont);
        nameFld.setHorizontalAlignment(JTextField.CENTER);

        JLabel priceLbl = new JLabel("Price ($):", SwingConstants.CENTER);
        priceLbl.setFont(labelFont);
        JTextField priceFld = new JTextField(15);
        priceFld.setFont(inputFont);
        priceFld.setHorizontalAlignment(JTextField.CENTER);

        JLabel durLbl = new JLabel(
            "Duration (in months):",
            SwingConstants.CENTER
        );
        durLbl.setFont(labelFont);
        JTextField durFld = new JTextField(15);
        durFld.setFont(inputFont);
        durFld.setHorizontalAlignment(JTextField.CENTER);

        Object[] message = {
            nameLbl,
            nameFld,
            priceLbl,
            priceFld,
            durLbl,
            durFld,
        };

        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            "Add New Membership Plan",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameFld.getText().trim();
                double price = Double.parseDouble(priceFld.getText().trim());
                int duration = Integer.parseInt(durFld.getText().trim());

                planService.createPlan(name, price, duration);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Confirms and deletes the selected plan from the list
     */
    private void deleteSelectedPlan() {
        int row = tblPlan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a plan to delete.");
            return;
        }

        String planId = (String) tblPlan.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this plan?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                planService.deletePlan(planId);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not delete plan: " + ex.getMessage(),
                    "Database Constraint Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
