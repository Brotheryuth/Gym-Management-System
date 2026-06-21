package com.gym;

import com.gym.controller.MemberController;
import com.gym.repository.MemberRepository;
import com.gym.service.MemberService;
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
            App app = new App(memberController);
            app.start(8080);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

