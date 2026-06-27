package com.gym.gui;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.service.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MemberPanel extends JPanel {
    private final MemberService memberService;
    private final PaymentService paymentService;
    private final MembershipPlanService planService;
    private final MembershipService membershipService;

    private final JTable tblMembers;
    private final DefaultTableModel tableModel;
    private final JTextField txtSearch;

    public MemberPanel(
        MemberService memberService, 
        PaymentService paymentService,
        MembershipPlanService planService,
        MembershipService membershipService
    ) {
        this.memberService = memberService;
        this.paymentService = paymentService;
        this.planService = planService;
        this.membershipService = membershipService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header Panel (Search & Register)
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel searchSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchSubPanel.add(new JLabel("Search Member (Name/Phone):"));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchSubPanel.add(txtSearch);
        
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> performSearch());
        searchSubPanel.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            refreshStat();
        });
        searchSubPanel.add(btnReset);

        // Register button styled as green outline via UIHelper
        JButton btnAdd = UIHelper.createOutlineButton("+ Register Member", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            new MemberFormDialog(owner, null, memberService).setVisible(true);
            refreshStat();
        });

        headerPanel.add(searchSubPanel, BorderLayout.WEST);
        headerPanel.add(btnAdd, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Member Table (Read-only)
        String[] columns = {"ID", "Full Name", "Gender", "Phone Number", "DOB", "Age", "Status"};
        tableModel = UIHelper.createReadOnlyModel(columns);
        tblMembers = new JTable(tableModel);
        tblMembers.setRowHeight(22);
        tblMembers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(new JScrollPane(tblMembers), BorderLayout.CENTER);

        // Bottom Action buttons (Styled via UIHelper)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        JButton btnPay = UIHelper.createSolidButton("Pay / Subscribe Plan", new Color(230, 126, 34), new Color(211, 110, 26));
        JButton btnEdit = UIHelper.createSolidButton("Edit Member", new Color(52, 152, 219), new Color(41, 128, 185));
        JButton btnDelete = UIHelper.createSolidButton("Remove Member", new Color(231, 76, 60), new Color(217, 59, 43));

        btnPay.addActionListener(e -> processPaymentAction());
        btnEdit.addActionListener(e -> processEditAction());
        btnDelete.addActionListener(e -> processDeleteAction());

        actionPanel.add(btnPay);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        add(actionPanel, BorderLayout.SOUTH);

        refreshStat();
    }

    public void refreshStat() {
        tableModel.setRowCount(0);
        for (Member m : memberService.findAll()) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getFullName(), m.getGender(), m.getPhoneNumber(), m.getDob(), m.getAge(), m.getMemberStatus()
            });
        }
    }

    private void performSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            refreshStat();
            return;
        }
        tableModel.setRowCount(0);
        for (Member m : memberService.findAll()) {
            if (m.getFullName().toLowerCase().contains(query.toLowerCase()) || 
                m.getPhoneNumber().contains(query) || 
                m.getId().contains(query)) {
                tableModel.addRow(new Object[]{
                    m.getId(), m.getFullName(), m.getGender(), m.getPhoneNumber(), m.getDob(), m.getAge(), m.getMemberStatus()
                });
            }
        }
    }

    private void processPaymentAction() {
        int selectedRow = tblMembers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a member to process payment.");
            return;
        }
        String memberId = (String) tblMembers.getValueAt(selectedRow, 0);
        Member selectedMember = memberService.findById(memberId);
        if (selectedMember == null) {
            JOptionPane.showMessageDialog(this, "Member not found.");
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        new PayMemberDialog(owner, selectedMember, planService, membershipService, paymentService).setVisible(true);
        refreshStat();
    }

    private void processEditAction() {
        int selectedRow = tblMembers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a member to edit.");
            return;
        }
        String memberId = (String) tblMembers.getValueAt(selectedRow, 0);
        Member editMember = memberService.findById(memberId);

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        new MemberFormDialog(owner, editMember, memberService).setVisible(true);
        refreshStat();
    }

    private void processDeleteAction() {
        int selectedRow = tblMembers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a member to delete.");
            return;
        }
        String memberId = (String) tblMembers.getValueAt(selectedRow, 0);
        String name = (String) tblMembers.getValueAt(selectedRow, 1);

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
    }
}
