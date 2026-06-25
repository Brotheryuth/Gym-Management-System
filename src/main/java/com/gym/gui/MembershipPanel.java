package com.gym.gui;

import com.gym.service.MemberService;
import com.gym.service.MembershipPlanService;
import com.gym.service.MembershipService;

import javax.swing.*;

public class MembershipPanel extends JPanel {
    public MembershipPanel(MembershipService mss , MemberService ms , MembershipPlanService mps){
        add(new JLabel("Membership Screen"));
    }
    public  void refreshTable(){};
}
