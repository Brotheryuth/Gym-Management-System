package com.gym.gui;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.service.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {
    private final MemberService memberService;
    private final PaymentService paymentService;
    private final MembershipPlanService planService;
    private final MembershipService membershipService;

    private final JLabel lblActiveMembers;
    private final JLabel lblTotalRevenue;
    private final JTable tblRecentMembers;
    private final DefaultTableModel recentModel;

    public DashboardPanel(
        MemberService memberService,
        MembershipService membershipService,
        MembershipPlanService planService,
        PaymentService paymentService
    ) {
        this.memberService = memberService;
        this.membershipService = membershipService;
        this.planService = planService;
        this.paymentService = paymentService;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP STATS PANEL ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        
        JPanel pnlActive = createMetricCard("Active Members", "0", new Color(46, 204, 113));
        lblActiveMembers = (JLabel) pnlActive.getClientProperty("valLabel");

        JPanel pnlRevenue = createMetricCard("Total Revenue", "$0.00", new Color(52, 152, 219));
        lblTotalRevenue = (JLabel) pnlRevenue.getClientProperty("valLabel");

        statsPanel.add(pnlActive);
        statsPanel.add(pnlRevenue);
        add(statsPanel, BorderLayout.NORTH);

        // --- CENTER TABLE PANEL (RECENT REGISTRATIONS) ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Recent Registrations (Last 5 Members)"
        ));

        String[] columns = {"Member ID", "Full Name", "Gender", "Phone Number", "Status"};
        recentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRecentMembers = new JTable(recentModel);
        tblRecentMembers.setRowHeight(22);
        tblRecentMembers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        centerPanel.add(new JScrollPane(tblRecentMembers), BorderLayout.CENTER);

        // Fast Action Buttons below the recent list
        JPanel centerActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnRecentPay = new JButton("Pay Outstanding Fee");
        JButton btnRecentDelete = new JButton("Delete Member");

        // Native Look and feel custom coloring
        btnRecentPay.setFocusPainted(false);
        btnRecentPay.setContentAreaFilled(false);
        btnRecentPay.setOpaque(true);
        btnRecentPay.setBackground(new Color(230, 126, 34)); // Premium Orange
        btnRecentPay.setForeground(Color.WHITE);

        btnRecentDelete.setFocusPainted(false);
        btnRecentDelete.setContentAreaFilled(false);
        btnRecentDelete.setOpaque(true);
        btnRecentDelete.setBackground(new Color(231, 76, 60)); // Premium Red
        btnRecentDelete.setForeground(Color.WHITE);

        // Action: Pay Selected Member
        btnRecentPay.addActionListener(e -> {
            int selectedRow = tblRecentMembers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a recent member to pay fees.");
                return;
            }
            String memberId = (String) tblRecentMembers.getValueAt(selectedRow, 0);

            Payment target = null;
            for (Payment p : paymentService.findAll()) {
                if (p.getStatus() == PaymentStatus.PENDING && 
                    p.getMembership() != null && 
                    p.getMembership().getMember() != null && 
                    p.getMembership().getMember().getId().equals(memberId)) {
                    target = p;
                    break;
                }
            }

            if (target == null) {
                JOptionPane.showMessageDialog(this, "This member has no pending fees.");
                return;
            }

            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            PaymentDialog dialog = new PaymentDialog(topFrame, target, paymentService);
            dialog.setVisible(true);
            refreshStat(); // Refresh metrics
        });

        // Action: Delete Selected Member
        btnRecentDelete.addActionListener(e -> {
            int selectedRow = tblRecentMembers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a recent member to delete.");
                return;
            }
            String memberId = (String) tblRecentMembers.getValueAt(selectedRow, 0);
            String name = (String) tblRecentMembers.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete member: " + name + "?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    memberService.deleteMember(memberId);
                    refreshStat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting member: " + ex.getMessage());
                }
            }
        });

        centerActionPanel.add(btnRecentPay);
        centerActionPanel.add(btnRecentDelete);
        centerPanel.add(centerActionPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM ENROLLMENT ACTION ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnQuickEnroll = new JButton("Enrollment Wizard");
        btnQuickEnroll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuickEnroll.setPreferredSize(new Dimension(200, 48));
        
        Color purpleTheme = new Color(155, 89, 182);
        btnQuickEnroll.setFocusPainted(false);
        btnQuickEnroll.setContentAreaFilled(false);
        btnQuickEnroll.setOpaque(false);
        btnQuickEnroll.setForeground(purpleTheme);
        btnQuickEnroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(purpleTheme, 2),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        btnQuickEnroll.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnQuickEnroll.setOpaque(true);
                btnQuickEnroll.setBackground(purpleTheme);
                btnQuickEnroll.setForeground(Color.WHITE);
                btnQuickEnroll.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnQuickEnroll.setOpaque(false);
                btnQuickEnroll.setForeground(purpleTheme);
                btnQuickEnroll.repaint();
            }
        });

        btnQuickEnroll.addActionListener(e -> {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            QuickEnrollmentDialog dialog = new QuickEnrollmentDialog(
                topFrame, 
                memberService, 
                planService, 
                membershipService, 
                paymentService
            );
            dialog.setVisible(true);
            refreshStat();
        });

        bottomPanel.add(btnQuickEnroll);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshStat();
    }

    public void refreshStat() {
        long activeCount = memberService.findAll().stream()
                .filter(m -> m.getMemberStatus() == MemberStatus.ACTIVE)
                .count();

        double totalRevenue = paymentService.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(p -> p.getBaseAmount() * (1.0 - (p.getDiscount() / 100.0)))
                .sum();

        lblActiveMembers.setText(String.valueOf(activeCount));
        lblTotalRevenue.setText(String.format("$%.2f", totalRevenue));

        recentModel.setRowCount(0);
        List<Member> allMembers = memberService.findAll();
        int limit = Math.max(0, allMembers.size() - 5);
        for (int i = allMembers.size() - 1; i >= limit; i--) {
            Member m = allMembers.get(i);
            recentModel.addRow(new Object[]{
                m.getId(), 
                m.getFullName(), 
                m.getGender(), 
                m.getPhoneNumber(), 
                m.getMemberStatus()
            });
        }
    }

    private JPanel createMetricCard(String title, String val, Color bg) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblVal.setForeground(Color.WHITE);

        card.add(lblTitle);
        card.add(lblVal);
        
        card.putClientProperty("valLabel", lblVal);
        return card;
    }
}
