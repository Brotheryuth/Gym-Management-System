package com.gym.gui;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.service.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class PayMemberDialog extends JDialog {
    private final Member member;
    private final MembershipPlanService planService;
    private final MembershipService membershipService;
    private final PaymentService paymentService;

    private final JComboBox<String> cbPlans = new JComboBox<>();
    private final JTextField txtDiscount = new JTextField("0");
    private final JComboBox<PaymentMethod> cbMethod = new JComboBox<>(PaymentMethod.values());
    private List<MembershipPlan> planList;

    public PayMemberDialog(
        Frame owner, 
        Member member, 
        MembershipPlanService planService, 
        MembershipService membershipService, 
        PaymentService paymentService
    ) {
        super(owner, "Process Payment - " + member.getFullName(), true);
        this.member = member;
        this.planService = planService;
        this.membershipService = membershipService;
        this.paymentService = paymentService;

        setSize(420, 340);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        Font valFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font lblFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel pnl = new JPanel(new GridLayout(5, 2, 10, 15));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Member name (read-only)
        pnl.add(new JLabel("Member Name:", SwingConstants.CENTER));
        JLabel lblName = new JLabel(member.getFullName(), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pnl.add(lblName);

        // 2. Member phone (read-only)
        pnl.add(new JLabel("Phone Number:", SwingConstants.CENTER));
        JLabel lblPhone = new JLabel(member.getPhoneNumber(), SwingConstants.CENTER);
        lblPhone.setFont(valFont);
        pnl.add(lblPhone);

        // 3. Plan Selection
        pnl.add(new JLabel("Select Plan:", SwingConstants.CENTER));
        planList = planService.findAll();
        for (MembershipPlan p : planList) {
            cbPlans.addItem(p.getPlanName() + " ($" + p.getPlanPrice() + ")");
        }
        cbPlans.setFont(valFont);
        ((JLabel)cbPlans.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnl.add(cbPlans);

        // 4. Discount
        pnl.add(new JLabel("Discount % (0-100):", SwingConstants.CENTER));
        txtDiscount.setFont(valFont);
        txtDiscount.setHorizontalAlignment(JTextField.CENTER);
        pnl.add(txtDiscount);

        // 5. Payment Method
        pnl.add(new JLabel("Payment Method:", SwingConstants.CENTER));
        cbMethod.setFont(valFont);
        ((JLabel)cbMethod.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnl.add(cbMethod);

        add(pnl, BorderLayout.CENTER);

        // --- BOTTOM ACTION BUTTONS ---
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancel = new JButton("Cancel");
        JButton btnPay = new JButton("Confirm Payment & Activate");
        
        btnPay.setFocusPainted(false);
        btnPay.setContentAreaFilled(false);
        btnPay.setOpaque(true);
        btnPay.setBackground(new Color(46, 204, 113)); // Solid Green
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        btnPay.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPay.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPay.setBackground(new Color(46, 204, 113));
            }
        });

        btnCancel.addActionListener(e -> dispose());
        
        btnPay.addActionListener(e -> {
            try {
                if (planList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Add a membership plan first.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                MembershipPlan plan = planList.get(cbPlans.getSelectedIndex());
                int discount = Integer.parseInt(txtDiscount.getText().trim());
                PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();

                // Step 1: Create a new membership subscription
                Date start = Date.valueOf(LocalDate.now());
                Membership ms = membershipService.subscribeMember(member, plan, start, discount, method);
                if (ms == null || ms.getPayment() == null) {
                    JOptionPane.showMessageDialog(this, "Could not create subscription.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Step 2: Complete payment processing (Activates Member and Subscription)
                boolean success = paymentService.processPayment(ms.getPayment().getId(), method);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Subscription successfully activated!", "Payment Processed", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to capture payment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Discount must be numeric.", "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPnl.add(btnCancel);
        btnPnl.add(btnPay);
        add(btnPnl, BorderLayout.SOUTH);
    }
}
