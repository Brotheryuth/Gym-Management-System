package com.gym.gui;

import com.gym.service.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    // Tracking lists for sidebar navigation styling
    private final List<JButton> navButtons = new ArrayList<>();
    private JButton activeBtn = null;

    // Sidebar colors
    private final Color sidebarBg = new Color(24, 28, 36);       // Modern dark slate
    private final Color activeBg = new Color(38, 45, 59);        // Dark highlight for active button
    private final Color hoverBg = new Color(33, 39, 50);         // Subtle hover color
    private final Color textActive = Color.WHITE;
    private final Color textInactive = new Color(160, 165, 175); // Muted light gray
    private final Color blueIndicator = new Color(59, 130, 246);  // Elegant blue indicator line

    public MainFrame(
        MemberService memberService,
        MembershipService membershipService,
        MembershipPlanService membershipPlanService,
        PaymentService paymentService,
        StaffService staffService
    ) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Gym Management System");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- MODERN SIDEBAR ---
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setPreferredSize(new Dimension(220, 0));
        sideBar.setBackground(sidebarBg);
        sideBar.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));

        // Sidebar Logo/Title Header
        JLabel titleLabel = new JLabel("GYM MANAGER");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideBar.add(titleLabel);

        // Gap below title
        sideBar.add(Box.createVerticalStrut(30));

        // Creating Navigation Buttons
        JButton btnDashboard = createNavButton("Dashboard");
        JButton btnMembers = createNavButton("Members");
        JButton btnMembership = createNavButton("Membership");
        JButton btnPayment = createNavButton("Payment");
        JButton btnPlan = createNavButton("Plans");

        // Add buttons to sidebar with neat vertical gaps
        sideBar.add(btnDashboard);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnMembers);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnMembership);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnPayment);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnPlan);

        add(sideBar, BorderLayout.WEST);

        // --- CONTENT PANEL (CardLayout) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        DashboardPanel dashboardPanel = new DashboardPanel(
            memberService,
            membershipService,
            membershipPlanService,
            paymentService
        );
        MemberPanel memberPanel = new MemberPanel(
            memberService,
            paymentService,
            membershipPlanService,
            membershipService
        );
        MembershipPanel membershipPanel = new MembershipPanel(
            membershipService,
            memberService,
            membershipPlanService
        );
        MembershipPlanPanel planPanel = new MembershipPlanPanel(
            membershipPlanService
        );
        PaymentPanel paymentPanel = new PaymentPanel(membershipService);

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(memberPanel, "Members");
        contentPanel.add(membershipPanel, "Memberships");
        contentPanel.add(planPanel, "Plans");
        contentPanel.add(paymentPanel, "Payments");

        add(contentPanel, BorderLayout.CENTER);

        // --- NAVIGATION EVENT LISTENERS ---
        btnDashboard.addActionListener(e -> {
            dashboardPanel.refreshStat();
            selectTab(btnDashboard, "Dashboard");
        });

        btnMembers.addActionListener(e -> {
            memberPanel.refreshStat();
            selectTab(btnMembers, "Members");
        });

        btnMembership.addActionListener(e -> {
            membershipPanel.refreshTable();
            selectTab(btnMembership, "Memberships");
        });

        btnPlan.addActionListener(e -> {
            planPanel.refreshTable();
            selectTab(btnPlan, "Plans");
        });

        btnPayment.addActionListener(e -> {
            paymentPanel.refreshTable();
            selectTab(btnPayment, "Payments");
        });

        // Load the Dashboard by default on launch
        selectTab(btnDashboard, "Dashboard");
    }

    /**
     * Updates navigation buttons' styles and swaps views
     */
    private void selectTab(JButton targetBtn, String cardName) {
        // Reset all navigation buttons to unselected state
        for (JButton btn : navButtons) {
            btn.setBackground(sidebarBg);
            btn.setForeground(textInactive);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 15));
        }

        // Apply active styled highlight and left bar indicator
        targetBtn.setBackground(activeBg);
        targetBtn.setForeground(textActive);
        targetBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, blueIndicator), // Blue left indicator line
            BorderFactory.createEmptyBorder(10, 13, 10, 15)
        ));

        activeBtn = targetBtn;
        cardLayout.show(contentPanel, cardName);
    }

    /**
     * A helper function to build modern hoverable navigation buttons
     */
    private JButton createNavButton(String name) {
        JButton btn = new JButton(name);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 42)); // Fixed uniform modern button size

        // Default Inactive Styling
        btn.setBackground(sidebarBg);
        btn.setForeground(textInactive);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeBtn) {
                    btn.setBackground(hoverBg);
                    btn.setForeground(textActive);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeBtn) {
                    btn.setBackground(sidebarBg);
                    btn.setForeground(textInactive);
                }
            }
        });

        navButtons.add(btn);
        return btn;
    }
}
