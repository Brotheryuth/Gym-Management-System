package com.gym;

import com.gym.controller.*;
import com.gym.repository.*;
import com.gym.service.*;
import com.gym.util.DatabaseConnection;
import com.gym.util.DatabaseInitializer;

import java.sql.Connection;

public class Server {

    public static void main(String[] args) {
        try {
            Connection db = DatabaseConnection.getInstance().getConnection();
            if (db != null && !db.isClosed()) {
                System.out.println("Connection Successful");
                DatabaseInitializer.initializeDatabase(db);
            } else {
                System.out.println("Connection failed");
            }

            MemberRepository memRepo = new MemberRepository(db);
            MemberService memberService = new MemberService(memRepo);
            MemberController memberController = new MemberController(memberService);

            MembershipPlanRepository planRepo = new MembershipPlanRepository(db);
            MembershipRepository membershipRepo = new MembershipRepository(db);
            MembershipPlanService planService = new MembershipPlanService(planRepo, membershipRepo);
            MembershipPlanController planController = new MembershipPlanController(planService);

            PaymentRepository paymentRepo = new PaymentRepository(db);
            PaymentService paymentService = new PaymentService(paymentRepo, membershipRepo, memRepo);
            PaymentController paymentController = new PaymentController(paymentService);

            MembershipService membershipService = new MembershipService(membershipRepo, memRepo, paymentRepo, planRepo);
            MembershipController membershipController = new MembershipController(membershipService, memberService, planService);

            StaffRepository staffRepo = new StaffRepository(db);
            StaffService staffService = new StaffService(staffRepo);
            StaffController staffController = new StaffController(staffService);
            AuthController authController = new AuthController(staffService);

            App app = new App(memberController, planController, membershipController, paymentController, staffController, authController);

            app.start(7070);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
