package com.gym.gui;

import com.gym.model.Member;
import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import com.gym.service.MemberService;
import java.awt.*;
import java.sql.Date;
import javax.swing.*;

public class MemberFormDialog extends JDialog {
    private final MemberService memberService;
    private final Member existingMember; // Null if registering new member

    private final JTextField txtName = new JTextField();
    private final JComboBox<Gender> cbGender = new JComboBox<>(Gender.values());
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtDOB = new JTextField("YYYY-MM-DD");
    private final JComboBox<MemberStatus> cbStatus = new JComboBox<>(MemberStatus.values());

    public MemberFormDialog(Frame owner, Member member, MemberService service) {
        super(owner, member == null ? "Register New Member" : "Edit Member Detail", true);
        this.memberService = service;
        this.existingMember = member;

        setSize(420, 360);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
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

        // 5. Status
        JLabel lblStatus = new JLabel("Member Status:", SwingConstants.CENTER);
        lblStatus.setFont(labelFont);
        cbStatus.setFont(inputFont);
        ((JLabel)cbStatus.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblStatus);
        formPanel.add(cbStatus);

        // Prepopulate data if Editing
        if (existingMember != null) {
            txtName.setText(existingMember.getFullName());
            cbGender.setSelectedItem(existingMember.getGender());
            txtPhone.setText(existingMember.getPhoneNumber());
            txtDOB.setText(existingMember.getDob().toString());
            cbStatus.setSelectedItem(existingMember.getMemberStatus());
        }

        add(formPanel, BorderLayout.CENTER);

        // Bottom Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Save Changes");
        
        btnSave.setFocusPainted(false);
        btnSave.setContentAreaFilled(false);
        btnSave.setOpaque(true);
        btnSave.setBackground(new Color(46, 204, 113)); // Solid Green
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        btnSave.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSave.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSave.setBackground(new Color(46, 204, 113));
            }
        });

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveForm());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveForm() {
        try {
            String name = txtName.getText().trim();
            Gender gender = (Gender) cbGender.getSelectedItem();
            String phone = txtPhone.getText().trim();
            Date dob = Date.valueOf(txtDOB.getText().trim());
            MemberStatus status = (MemberStatus) cbStatus.getSelectedItem();

            if (existingMember == null) {
                Member newMem = new Member(name, gender, phone, dob, status);
                memberService.registerMember(newMem);
            } else {
                existingMember.setFullName(name);
                existingMember.setGender(gender);
                existingMember.setPhoneNumber(phone);
                existingMember.setDob(dob);
                existingMember.setMemberStatus(status);
                memberService.updateMember(existingMember.getId(), existingMember);
            }

            JOptionPane.showMessageDialog(this, "Member details saved successfully!");
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Format Error:\nDate of birth must be YYYY-MM-DD\n\nDetails: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database/System Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
