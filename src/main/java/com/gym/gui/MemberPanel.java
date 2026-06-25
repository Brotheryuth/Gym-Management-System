package com.gym.gui;

import com.gym.service.MemberService;
import com.gym.service.PaymentService;

import javax.swing.*;

public class MemberPanel extends JPanel {
    public MemberPanel(MemberService ms , PaymentService ps ){
        add(new JLabel("Member screen"));
    }
    public  void refreshStat(){}
}
