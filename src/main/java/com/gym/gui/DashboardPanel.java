package com.gym.gui;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.service.*;
import java.awt.*;
import java.sql.Date;
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
    
    // Popular Plan Progress Bars
    private final JProgressBar barStandard;
    private final JProgressBar barPremium;
    private final JProgressBar barElite;
    
    // Left Table: Recent Members
    private final JTable tblRecentMembers;
    private final DefaultTableModel recentModel;

    // Right Table: Expired & Expiring memberships
    private final JTable tblExpiringMembers;
    private final DefaultTableModel expiringModel;

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

        setLayout(new BorderLayout(20, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP STATS PANEL (3 Columns) ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        
        JPanel pnlActive = createMetricCard("Active Members", "0", new Color(46, 204, 113)); // Green
        lblActiveMembers = (JLabel) pnlActive.getClientProperty("valLabel");

        JPanel pnlRevenue = createMetricCard("Total Revenue", "$0.00", new Color(52, 152, 219)); // Blue
        lblTotalRevenue = (JLabel) pnlRevenue.getClientProperty("valLabel");

        // Popularity card with 3 progress bars
        JPanel pnlPopularity = new JPanel(new BorderLayout(5, 5));
        pnlPopularity.setBackground(Color.WHITE);
        pnlPopularity.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblPopTitle = new JLabel("Plan Distribution");
        lblPopTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPopTitle.setForeground(new Color(120, 125, 135));
        pnlPopularity.add(lblPopTitle, BorderLayout.NORTH);

        JPanel barsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        barsPanel.setOpaque(false);

        // Standard Plan Bar
        JPanel rowStandard = new JPanel(new BorderLayout(5, 2));
        rowStandard.setOpaque(false);
        JLabel lblStd = new JLabel("Standard:");
        lblStd.setPreferredSize(new Dimension(65, 0));
        lblStd.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rowStandard.add(lblStd, BorderLayout.WEST);
        barStandard = new JProgressBar(0, 100);
        barStandard.setStringPainted(true);
        barStandard.setForeground(new Color(52, 152, 219)); // Blue
        rowStandard.add(barStandard, BorderLayout.CENTER);

        // Premium Plan Bar
        JPanel rowPremium = new JPanel(new BorderLayout(5, 2));
        rowPremium.setOpaque(false);
        JLabel lblPrem = new JLabel("Premium:");
        lblPrem.setPreferredSize(new Dimension(65, 0));
        lblPrem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rowPremium.add(lblPrem, BorderLayout.WEST);
        barPremium = new JProgressBar(0, 100);
        barPremium.setStringPainted(true);
        barPremium.setForeground(new Color(155, 89, 182)); // Purple
        rowPremium.add(barPremium, BorderLayout.CENTER);

        // Elite Plan Bar
        JPanel rowElite = new JPanel(new BorderLayout(5, 2));
        rowElite.setOpaque(false);
        JLabel lblElite = new JLabel("Elite:");
        lblElite.setPreferredSize(new Dimension(65, 0));
        lblElite.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rowElite.add(lblElite, BorderLayout.WEST);
        barElite = new JProgressBar(0, 100);
        barElite.setStringPainted(true);
        barElite.setForeground(new Color(230, 126, 34)); // Orange
        rowElite.add(barElite, BorderLayout.CENTER);

        barsPanel.add(rowStandard);
        barsPanel.add(rowPremium);
        barsPanel.add(rowElite);
        pnlPopularity.add(barsPanel, BorderLayout.CENTER);

        statsPanel.add(pnlActive);
        statsPanel.add(pnlRevenue);
        statsPanel.add(pnlPopularity);
        add(statsPanel, BorderLayout.NORTH);

        // --- CENTER SPLIT-TABLE PANEL ---
        JPanel tablesContainer = new JPanel(new GridLayout(1, 2, 20, 0));

        // 1. Left Table: Recent Registrations
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), "Recent Registrations (Last 5)"
        ));
        recentModel = UIHelper.createReadOnlyModel(new String[]{"ID", "Name", "Phone", "Status"});
        tblRecentMembers = new JTable(recentModel);
        tblRecentMembers.setRowHeight(22);
        leftPanel.add(new JScrollPane(tblRecentMembers), BorderLayout.CENTER);

        JPanel leftActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnRecentPay = UIHelper.createSolidButton("Pay / Subscribe Plan", new Color(230, 126, 34), new Color(211, 110, 26));
        JButton btnRecentDelete = UIHelper.createSolidButton("Delete Member", new Color(231, 76, 60), new Color(217, 59, 43));
        
        btnRecentPay.addActionListener(e -> {
            int selectedRow = tblRecentMembers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a recent member to pay.");
                return;
            }
            String memberId = (String) tblRecentMembers.getValueAt(selectedRow, 0);
            Member m = memberService.findById(memberId);
            if (m != null) {
                Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                new PayMemberDialog(topFrame, m, planService, membershipService, paymentService).setVisible(true);
                refreshStat();
            }
        });

        btnRecentDelete.addActionListener(e -> {
            int selectedRow = tblRecentMembers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a recent member to delete.");
                return;
            }
            String memberId = (String) tblRecentMembers.getValueAt(selectedRow, 0);
            String name = (String) tblRecentMembers.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete member " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                memberService.deleteMember(memberId);
                refreshStat();
            }
        });

        leftActionPanel.add(btnRecentPay);
        leftActionPanel.add(btnRecentDelete);
        leftPanel.add(leftActionPanel, BorderLayout.SOUTH);

        // 2. Right Table: Expired & Expiring Subscriptions
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), "Expired & Expiring Subscriptions (Next 7 Days)"
        ));
        expiringModel = UIHelper.createReadOnlyModel(new String[]{"Sub ID", "Member", "Plan", "End Date", "Status"});
        tblExpiringMembers = new JTable(expiringModel);
        tblExpiringMembers.setRowHeight(22);
        rightPanel.add(new JScrollPane(tblExpiringMembers), BorderLayout.CENTER);

        JPanel rightActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnRenew = UIHelper.createSolidButton("Renew Subscription", new Color(155, 89, 182), new Color(142, 68, 173));
        
        btnRenew.addActionListener(e -> {
            int selectedRow = tblExpiringMembers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a membership row to renew.");
                return;
            }
            String subId = (String) tblExpiringMembers.getValueAt(selectedRow, 0);
            Membership ms = membershipService.findById(subId);
            if (ms != null && ms.getMember() != null) {
                Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                new PayMemberDialog(topFrame, ms.getMember(), planService, membershipService, paymentService).setVisible(true);
                refreshStat();
            } else {
                JOptionPane.showMessageDialog(this, "Membership details not found.");
            }
        });

        rightActionPanel.add(btnRenew);
        rightPanel.add(rightActionPanel, BorderLayout.SOUTH);

        tablesContainer.add(leftPanel);
        tablesContainer.add(rightPanel);
        add(tablesContainer, BorderLayout.CENTER);

        // --- BOTTOM WIZARD BAR ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnQuickEnroll = UIHelper.createOutlineButton("Enrollment ", new Color(155, 89, 182));
        btnQuickEnroll.setPreferredSize(new Dimension(200, 42));
        
        btnQuickEnroll.addActionListener(e -> {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            new QuickEnrollmentDialog(topFrame, memberService, planService, membershipService, paymentService).setVisible(true);
            refreshStat();
        });

        bottomPanel.add(btnQuickEnroll);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshStat();
    }

    /**
     * Refreshes stats, lists, and checks for expirations.
     */
    public void refreshStat() {
        membershipService.checkAndExpireMemberships();

        // 1. Calculate Active Members
        long activeCount = memberService.findAll().stream()
                .filter(m -> m.getMemberStatus() == MemberStatus.ACTIVE)
                .count();

        // 2. Calculate Total Revenue
        double totalRevenue = paymentService.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(p -> p.getBaseAmount() * (1.0 - (p.getDiscount() / 100.0)))
                .sum();

        lblActiveMembers.setText(String.valueOf(activeCount));
        lblTotalRevenue.setText(String.format("$%.2f", totalRevenue));

        // 3. Calculate Popular Plan Percentages
        List<Membership> allMemberships = membershipService.findAll();
        long totalSubs = allMemberships.size();
        
        long countStandard = 0;
        long countPremium = 0;
        long countElite = 0;

        for (Membership ms : allMemberships) {
            if (ms.getPlan() != null) {
                String planName = ms.getPlan().getPlanName();
                if (planName.toLowerCase().contains("standard")) {
                    countStandard++;
                } else if (planName.toLowerCase().contains("premium")) {
                    countPremium++;
                } else if (planName.toLowerCase().contains("elite")) {
                    countElite++;
                }
            }
        }

        int pctStandard = totalSubs > 0 ? (int) ((countStandard * 100) / totalSubs) : 0;
        int pctPremium = totalSubs > 0 ? (int) ((countPremium * 100) / totalSubs) : 0;
        int pctElite = totalSubs > 0 ? (int) ((countElite * 100) / totalSubs) : 0;

        barStandard.setValue(pctStandard);
        barStandard.setString(pctStandard + "%");

        barPremium.setValue(pctPremium);
        barPremium.setString(pctPremium + "%");

        barElite.setValue(pctElite);
        barElite.setString(pctElite + "%");

        // 4. Load Left Table (Last 5 Registrations)
        recentModel.setRowCount(0);
        List<Member> allMembers = memberService.findAll();
        int limit = Math.max(0, allMembers.size() - 5);
        for (int i = allMembers.size() - 1; i >= limit; i--) {
            Member m = allMembers.get(i);
            recentModel.addRow(new Object[]{
                m.getId(), m.getFullName(), m.getPhoneNumber(), m.getMemberStatus()
            });
        }

        // 5. Load Right Table (Expired & Expiring Subscriptions)
        expiringModel.setRowCount(0);
        long sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000;
        long todayMs = System.currentTimeMillis();

        for (Membership ms : allMemberships) {
            boolean isExpired = ms.getStatus() == MembershipStatus.EXPIRED;
            boolean isExpiringSoon = false;

            if (ms.getStatus() == MembershipStatus.ACTIVE && ms.getEndDate() != null) {
                long diff = ms.getEndDate().getTime() - todayMs;
                if (diff >= 0 && diff <= sevenDaysInMillis) {
                    isExpiringSoon = true;
                }
            }

            if (isExpired || isExpiringSoon) {
                String memberName = ms.getMember() != null ? ms.getMember().getFullName() : "N/A";
                String planName = ms.getPlan() != null ? ms.getPlan().getPlanName() : "N/A";
                String statusStr = isExpired ? "EXPIRED" : "EXPIRING SOON";
                
                expiringModel.addRow(new Object[]{
                    ms.getId(),
                    memberName,
                    planName,
                    ms.getEndDate(),
                    statusStr
                });
            }
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
