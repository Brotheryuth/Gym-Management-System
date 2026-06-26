package com.gym.gui;

import com.gym.service.*;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

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
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // sidebar
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new GridLayout(6, 1, 10, 10));
        sideBar.setPreferredSize(new Dimension(200, 0));
        sideBar.setBackground(new Color(43, 43, 43));
        sideBar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel("Gym Admin", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        sideBar.add(titleLabel);

        JButton btnDashboard = createNavButton("Dashboard");
        JButton btnMembers = createNavButton("Members");
        JButton btnMembership = createNavButton("Membership");
        JButton btnPayment = createNavButton("Payment");
        JButton btnPlan = createNavButton("Plans");

        sideBar.add(btnDashboard);
        sideBar.add(btnMembers);
        sideBar.add(btnMembership);
        sideBar.add(btnPayment);
        sideBar.add(btnPlan);
        add(sideBar, BorderLayout.WEST);

        // card layout

        cardLayout = new CardLayout();
        contentPanel = new JPanel();

        DashboardPanel dashboardPanel = new DashboardPanel(
            memberService,
            membershipService,
            membershipPlanService,
            paymentService
        );
        MemberPanel memberPanel = new MemberPanel(
            memberService,
            paymentService
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

        //for switching panel
        btnDashboard.addActionListener(e -> {
            dashboardPanel.refreshStat();
            cardLayout.show(contentPanel, "Dashboard");
        });

        btnMembers.addActionListener(e -> {
            memberPanel.refreshStat();
            cardLayout.show(contentPanel, "Members");
        });

        btnMembership.addActionListener(e -> {
            membershipPanel.refreshTable();
            cardLayout.show(contentPanel, "Memberships");
        });

        btnPlan.addActionListener(e -> {
            planPanel.refreshTable();
            cardLayout.show(contentPanel, "Plans");
        });

        btnPayment.addActionListener(e -> {
            paymentPanel.refreshTable();
            cardLayout.show(contentPanel, "Payment");
        });
    } //end constructor

    /**
     * a helper function to create button
     * @param name button name
     * @return
     */
    private JButton createNavButton(String name) {
        JButton btn = new JButton(name);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 63, 65));
        btn.setForeground(Color.white);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(new Color(75, 75, 75)));
        return btn;
    }
}
