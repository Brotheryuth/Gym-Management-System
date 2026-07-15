package com.gym.gui;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.service.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class QuickEnrollmentDialog extends JDialog {
    private final MemberService memberService;
    private final MembershipPlanService planService;
    private final MembershipService membershipService;
    private final PaymentService paymentService;

    // Form inputs (centered text and styled layout)
    private final JTextField txtName = new JTextField();
    private final JComboBox<Gender> cbGender = new JComboBox<>(Gender.values());
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtDOB = new JTextField("YYYY-MM-DD");
    private final JComboBox<String> cbPlans = new JComboBox<>();
    private final JTextField txtDiscount = new JTextField("0");
    private final JComboBox<PaymentMethod> cbPaymentMethod = new JComboBox<>(PaymentMethod.values());

    private List<MembershipPlan> planList;

    public QuickEnrollmentDialog(
        Frame owner, 
        MemberService ms, 
        MembershipPlanService mps, 
        MembershipService mbs, 
        PaymentService ps
    ) {
        super(owner, "Quick Member Enrollment Wizard", true);
        this.memberService = ms;
        this.planService = mps;
        this.membershipService = mbs;
        this.paymentService = ps;

        setSize(480, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Full Name
        JLabel lblName = new JLabel("Full Name:", SwingConstants.CENTER);
        lblName.setFont(labelFont);
        txtName.setFont(inputFont);
        txtName.setHorizontalAlignment(JTextField.CENTER);
        formPanel.add(lblName);
        formPanel.add(txtName);

        // 2. Gender
        JLabel lblGender = new JLabel("Gender:", SwingConstants.CENTER);
        lblGender.setFont(labelFont);
        cbGender.setFont(inputFont);
        ((JLabel)cbGender.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblGender);
        formPanel.add(cbGender);

        // 3. Phone Number
        JLabel lblPhone = new JLabel("Phone Number:", SwingConstants.CENTER);
        lblPhone.setFont(labelFont);
        txtPhone.setFont(inputFont);
        txtPhone.setHorizontalAlignment(JTextField.CENTER);
        formPanel.add(lblPhone);
        formPanel.add(txtPhone);

        // 4. DOB
        JLabel lblDOB = new JLabel("Date of Birth:", SwingConstants.CENTER);
        lblDOB.setFont(labelFont);
        txtDOB.setFont(inputFont);
        txtDOB.setHorizontalAlignment(JTextField.CENTER);
        formPanel.add(lblDOB);
        formPanel.add(txtDOB);

        // 5. Select Plan
        JLabel lblPlan = new JLabel("Membership Plan:", SwingConstants.CENTER);
        lblPlan.setFont(labelFont);
        cbPlans.setFont(inputFont);
        ((JLabel)cbPlans.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Fetch plans from DB to populate dropdown list
        planList = planService.findAll();
        for (MembershipPlan plan : planList) {
            cbPlans.addItem(plan.getPlanName() + " ($" + plan.getPlanPrice() + " for " + plan.getDuration() + "m)");
        }
        formPanel.add(lblPlan);
        formPanel.add(cbPlans);

        // 6. Discount
        JLabel lblDiscount = new JLabel("Discount % (0-100):", SwingConstants.CENTER);
        lblDiscount.setFont(labelFont);
        txtDiscount.setFont(inputFont);
        txtDiscount.setHorizontalAlignment(JTextField.CENTER);
        formPanel.add(lblDiscount);
        formPanel.add(txtDiscount);

        // 7. Payment Method
        JLabel lblMethod = new JLabel("Payment Method:", SwingConstants.CENTER);
        lblMethod.setFont(labelFont);
        cbPaymentMethod.setFont(inputFont);
        ((JLabel)cbPaymentMethod.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblMethod);
        formPanel.add(cbPaymentMethod);

        add(formPanel, BorderLayout.CENTER);

        // --- BUTTONS ACTION BOTTOM PANEL ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancel = new JButton("Cancel");
        
        JButton btnSubmit = new JButton("Complete Process");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Native L&F color overrides
        btnSubmit.setFocusPainted(false);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setOpaque(true);
        btnSubmit.setBackground(new Color(46, 204, 113)); // Solid Green
        btnSubmit.setForeground(Color.WHITE);             // White Text
        btnSubmit.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        // Submit Button Hover Animation
        btnSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSubmit.setBackground(new Color(39, 174, 96)); // Darker green on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSubmit.setBackground(new Color(46, 204, 113)); // Return to normal green
            }
        });

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> runWizard());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSubmit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Sequence-safe transaction executor (Member -> Subscription -> Payment)
     */
    private void runWizard() {
        try {
            // Read member data
            String name = txtName.getText().trim();
            Gender gender = (Gender) cbGender.getSelectedItem();
            String phone = txtPhone.getText().trim();
            Date dob = Date.valueOf(txtDOB.getText().trim());

            if (planList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Add a membership plan first before enrolling members.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MembershipPlan plan = planList.get(cbPlans.getSelectedIndex());

            int discount = Integer.parseInt(txtDiscount.getText().trim());
            PaymentMethod method = (PaymentMethod) cbPaymentMethod.getSelectedItem();

            // Step 1: Add new inactive Member (backend validates phone uniqueness)
            Member member = new Member(name, gender, phone, dob, MemberStatus.INACTIVE);
            boolean isMemberRegistered = memberService.registerMember(member);
            if (!isMemberRegistered) {
                JOptionPane.showMessageDialog(this, "Registration failed.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 2: Enroll member in plan (Creates pending transaction record automatically)
            Date start = Date.valueOf(LocalDate.now());
            Membership membership = membershipService.subscribeMember(member, plan, start, discount, method);
            if (membership == null || membership.getPayment() == null) {
                JOptionPane.showMessageDialog(this, "Failed to create subscription.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 3: Authorize transaction payment (Activates both member and membership)
            boolean paySuccess = paymentService.processPayment(membership.getPayment().getId(), method);
            if (paySuccess) {
                JOptionPane.showMessageDialog(this, 
                    "Enrollment successful!\nMember '" + name + "' is now ACTIVE.", 
                    "Process Completed", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to capture payment.", "Process Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Format Error:\n1. Date of Birth must be YYYY-MM-DD (e.g. 1998-05-15)\n2. Discount must be numeric\n\nDetails: " + e.getMessage(), 
                "Input Validation Error", 
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Process crashed: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
