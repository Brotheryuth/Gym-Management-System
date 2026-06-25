package com.gym.gui;

import com.gym.service.MemberService;
import com.gym.service.MembershipPlanService;
import com.gym.service.MembershipService;
import com.gym.service.PaymentService;

import javax.swing.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel(MemberService ms , MembershipService mss , MembershipPlanService mps , PaymentService ps ){
        add(new JLabel("Dashboard"));
    }
    public  void refreshStat(){};
}
