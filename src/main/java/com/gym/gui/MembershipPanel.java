package com.gym.gui;

import com.gym.model.Membership;
import com.gym.service.MemberService;
import com.gym.service.MembershipPlanService;
import com.gym.service.MembershipService;
import com.gym.service.PaymentService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MembershipPanel extends JPanel {
    private final MembershipService membershipService;
    private final MembershipPlanService membershipPlanService;
    private final PaymentService paymentService;
    private final MemberService memberService;

    private JTable tblMemberships;
    private DefaultTableModel model;

    public MembershipPanel(MembershipService mss , MemberService ms , MembershipPlanService mps , PaymentService pm){
        this.membershipService = mss;
        this.membershipPlanService = mps;
        this.memberService=ms;
        this.paymentService = pm;
        //#region set Laout 
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        //#endregion
        String[] columns ={"Sub ID", "Member Name","Plan Name" , "Start Date","End Date","Status"};
        //#region Style read only table 
        model=UIHelper.createReadOnlyModel(columns);
        tblMemberships = new JTable(model);
        tblMemberships.setRowHeight(22);
        tblMemberships.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(new JScrollPane(tblMemberships) , BorderLayout.CENTER);
        //#endregion
        //#region Button panel 
        JPanel actioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        JButton btnRenew = UIHelper.createSolidButton("Renew Subscription", new Color(155,89,182), new Color(142,68,43));
        JButton btnCancel= UIHelper.createOutlineButton("Cancel Subscription",new Color(217,59,43));

        btnRenew.addActionListener(e->{
            int selectedRow = tblMemberships.getSelectedRow();
            if(selectedRow ==-1) {
                JOptionPane.showMessageDialog(this, "Select a subscription row to renew");
                return;
            }
            String subID  =(String) tblMemberships.getValueAt(selectedRow, 0);
            Membership memship = membershipService.findById(subID);
            if(memship !=null && memship.getMember()!=null){
                Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                new PayMemberDialog(topFrame, memship.getMember(), membershipPlanService, membershipService, paymentService).setVisible(true);
                refreshTable();
            } else{
                JOptionPane.showMessageDialog(this, "Subscription detail not found");
            }
        }); // end btn renew

        btnCancel.addActionListener(e->{
            int selectedRow = tblMemberships.getSelectedRow();
            if(selectedRow ==-1){
                JOptionPane.showMessageDialog(this, "Select a subscription to cancel");
                return;
            }
            String getID = (String) tblMemberships.getValueAt(selectedRow,0);
            String getName = (String ) tblMemberships.getValueAt(selectedRow,1);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel subscription for "+getName+"\n This will also deactive the member's status ", "Confirm cancellation", JOptionPane.YES_NO_OPTION);
            if(confirm ==JOptionPane.YES_OPTION){
                try{
                    boolean success = membershipService.cancelMembership(getID);
                    if(success){
                        JOptionPane.showMessageDialog(this, "Subscription cancelled successfully");
                        
                    }else{
                        JOptionPane.showMessageDialog(this,"Failed to Cancel subscription");
                    }
                    refreshTable();
                }
                catch(Exception ex ){
                    JOptionPane.showMessageDialog(this,"Error "+ex.getMessage());
                }

            }
        });
        //#endregion
        actioPanel.add(btnRenew);
        actioPanel.add(btnCancel);
        add(actioPanel,BorderLayout.SOUTH);
    }
    public  void refreshTable(){
        membershipService.checkAndExpireMemberships();
        model.setRowCount(0);
        for(Membership ms : membershipService.findAll() ){
            model.addRow(new Object[]{
                ms.getId(),
                ms.getMember() !=null? ms.getMember().getFullName() : "Unknown",
                ms.getPlan() !=null ? ms.getPlan().getPlanName() : "N/A",
                ms.getStartDate(),
                ms.getEndDate(),
                ms.getStatus()
            });
        }
    };
}
