package com.gym.gui;

import com.gym.model.Payment;
import com.gym.enums.PaymentMethod;
import com.gym.service.PaymentService;
import java.awt.*;
import javax.swing.*;

public class PaymentDialog extends JDialog {
    private final Payment payment;
    private final PaymentService paymentService;
    private final JComboBox<PaymentMethod> cbMethod = new JComboBox<>(PaymentMethod.values());

    public PaymentDialog(Frame owner, Payment payment, PaymentService service) {
        super(owner, "Process Payment", true);
        this.payment = payment;
        this.paymentService = service;

        setSize(360, 240);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        Font valFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font lblFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel pnl = new JPanel(new GridLayout(4, 2, 10, 15));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        double discountVal = payment.getBaseAmount() * (payment.getDiscount() / 100.0);
        double finalAmount = payment.getBaseAmount() - discountVal;

        pnl.add(new JLabel("Base Amount:", SwingConstants.CENTER));
        JLabel lblBase = new JLabel("$" + payment.getBaseAmount(), SwingConstants.CENTER);
        lblBase.setFont(valFont);
        pnl.add(lblBase);

        pnl.add(new JLabel("Discount Applied:", SwingConstants.CENTER));
        JLabel lblDisc = new JLabel(payment.getDiscount() + "%", SwingConstants.CENTER);
        lblDisc.setFont(valFont);
        pnl.add(lblDisc);

        pnl.add(new JLabel("Final Amount:", SwingConstants.CENTER));
        JLabel lblFinal = new JLabel("$" + String.format("%.2f", finalAmount), SwingConstants.CENTER);
        lblFinal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFinal.setForeground(new Color(46, 204, 113)); // Highlight total in green
        pnl.add(lblFinal);

        pnl.add(new JLabel("Payment Method:", SwingConstants.CENTER));
        cbMethod.setFont(valFont);
        ((JLabel)cbMethod.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnl.add(cbMethod);

        add(pnl, BorderLayout.CENTER);

        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancel = new JButton("Cancel");
        JButton btnPay = new JButton("Confirm Payment");
        
        btnPay.setFocusPainted(false);
        btnPay.setContentAreaFilled(false);
        btnPay.setOpaque(true);
        btnPay.setBackground(new Color(46, 204, 113));
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
            boolean success = paymentService.processPayment(payment.getId(), (PaymentMethod) cbMethod.getSelectedItem());
            if (success) {
                JOptionPane.showMessageDialog(this, "Payment successful!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Payment processing failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPnl.add(btnCancel);
        btnPnl.add(btnPay);
        add(btnPnl, BorderLayout.SOUTH);
    }
}
