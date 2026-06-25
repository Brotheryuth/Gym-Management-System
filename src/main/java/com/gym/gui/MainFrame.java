package com.gym.gui;

import com.gym.service.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(MemberService memberService , MembershipService membershipService ,
                     MembershipPlanService membershipPlanService,
                     PaymentService paymentService,
                     StaffService staffService
    ){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception e ){
            e.printStackTrace();
        }
        setTitle("Gym Management System");
        setSize(1000,650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

    }//end constructor
}
