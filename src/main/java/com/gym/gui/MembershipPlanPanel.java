package com.gym.gui;

import com.gym.service.MembershipPlanService;

import javax.swing.*;

public class MembershipPlanPanel extends JPanel {
    public MembershipPlanPanel(MembershipPlanService msp ){
        add(new JLabel("Plan screen"));
    }
    public  void refreshTable(){};
}
