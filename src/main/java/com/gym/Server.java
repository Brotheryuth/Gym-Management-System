package com.gym;

import com.gym.controller.MemberController;
import com.gym.controller.MembershipPlanController;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.MembershipRepository;
import com.gym.service.MemberService;
import com.gym.service.MembershipPlanService;
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

            App app = new App(memberController, planController);

            app.start(7070);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
