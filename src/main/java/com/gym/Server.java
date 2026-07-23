package com.gym;

import com.gym.controller.AuthController;
import com.gym.controller.MemberController;
import com.gym.controller.MembershipController;
import com.gym.controller.MembershipPlanController;
import com.gym.controller.PaymentController;
import com.gym.controller.StaffController;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.MembershipRepository;
import com.gym.repository.PaymentRepository;
import com.gym.repository.StaffRepository;
import com.gym.service.MemberService;
import com.gym.service.MembershipPlanService;
import com.gym.service.MembershipService;
import com.gym.service.PaymentService;
import com.gym.service.StaffService;
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
            MemberController memberController = new MemberController(
                memberService
            );

            MembershipPlanRepository planRepo = new MembershipPlanRepository(
                db
            );
            MembershipRepository membershipRepo = new MembershipRepository(db);
            MembershipPlanService planService = new MembershipPlanService(
                planRepo,
                membershipRepo
            );
            MembershipPlanController planController =
                new MembershipPlanController(planService);

            PaymentRepository paymentRepo = new PaymentRepository(db);
            PaymentService paymentService = new PaymentService(
                paymentRepo,
                membershipRepo,
                memRepo
            );
            PaymentController paymentController = new PaymentController(
                paymentService
            );

            MembershipService membershipService = new MembershipService(
                membershipRepo,
                memRepo,
                paymentRepo
            );
            MembershipController membershipController =
                new MembershipController(
                    membershipService,
                    memberService,
                    planService
                );

            StaffRepository staffRepo = new StaffRepository(db);
            StaffService staffService = new StaffService(staffRepo);
            StaffController staffController = new StaffController(staffService);
            AuthController authController = new AuthController(staffService);

            App app = new App(
                memberController,
                planController,
                membershipController,
                paymentController,
                staffController,
                authController
            );

            int port = 7070;
            String envPort = System.getenv("PORT");
            if (envPort != null && !envPort.isBlank()) {
                try {
                    port = Integer.parseInt(envPort);
                } catch (NumberFormatException ignored) {}
            }
            app.start(port);

            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    com.gym.gui.LoginFrame loginFrame = new com.gym.gui.LoginFrame(
                        memberService,
                        membershipService,
                        planService,
                        paymentService,
                        staffService
                    );
                    loginFrame.setVisible(true);
                });
            } else {
                System.out.println("Headless environment detected. Running backend web services only.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
