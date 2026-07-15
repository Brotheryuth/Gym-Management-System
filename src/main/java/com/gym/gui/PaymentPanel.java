package com.gym.gui;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.service.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PaymentPanel extends JPanel {
    private final PaymentService paymentService;

    private final JTable tblPayments;
    private final DefaultTableModel model;

    private final JLabel lblTotalPayments;
    private final JLabel lblPaidRevenue;

    public PaymentPanel(PaymentService paymentService) {
        this.paymentService = paymentService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- TOP STATS PANEL ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel cardTotal = UIHelper.createMetricCard("Total Transactions", "0", new Color(155, 89, 182)); // Purple
        lblTotalPayments = (JLabel) cardTotal.getClientProperty("valLabel");

        JPanel cardRevenue = UIHelper.createMetricCard("Paid Revenue", "$0.00", new Color(46, 204, 113)); // Green
        lblPaidRevenue = (JLabel) cardRevenue.getClientProperty("valLabel");

        statsPanel.add(cardTotal);
        statsPanel.add(cardRevenue);
        add(statsPanel, BorderLayout.NORTH);

        // --- CENTER TABLE LOG ---
        String[] columns = {
            "Transaction ID", "Member Name", "Plan Name", "Method", 
            "Base Amount", "Discount", "Final Amount", "Status", "Date"
        };
        model = UIHelper.createReadOnlyModel(columns);
        tblPayments = new JTable(model);
        tblPayments.setRowHeight(22);
        tblPayments.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(new JScrollPane(tblPayments), BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        model.setRowCount(0);
        List<Payment> allPayments = paymentService.findAll();

        long totalCount = allPayments.size();
        double paidRevenue = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(p -> p.getBaseAmount() * (1.0 - (p.getDiscount() / 100.0)))
                .sum();

        lblTotalPayments.setText(String.valueOf(totalCount));
        lblPaidRevenue.setText(String.format("$%.2f", paidRevenue));

        for (Payment p : allPayments) {
            String memberName = "N/A";
            String planName = "N/A";
            if (p.getMembership() != null) {
                if (p.getMembership().getMember() != null) {
                    memberName = p.getMembership().getMember().getFullName();
                }
                if (p.getMembership().getPlan() != null) {
                    planName = p.getMembership().getPlan().getPlanName();
                }
            }

            double finalAmt = p.getBaseAmount() * (1.0 - (p.getDiscount() / 100.0));

            model.addRow(new Object[]{
                p.getId(),
                memberName,
                planName,
                p.getMethod(),
                String.format("$%.2f", p.getBaseAmount()),
                p.getDiscount() + "%",
                String.format("$%.2f", finalAmt),
                p.getStatus(),
                p.getPaymentDate() != null ? p.getPaymentDate().toString() : "PENDING"
            });
        }
    }
}
