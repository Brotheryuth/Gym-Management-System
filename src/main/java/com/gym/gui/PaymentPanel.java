package com.gym.gui;

import com.gym.service.MembershipService;

import javax.swing.*;

public class PaymentPanel extends JPanel {
    public PaymentPanel(MembershipService ms  ){
        add(new JLabel("Payment screen"));
    }
    public void refreshTable(){};
}
