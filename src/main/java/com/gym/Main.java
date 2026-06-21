package com.gym;

import com.gym.util.DatabaseConnection;
import com.gym.util.DatabaseInitializer;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        try {
            Connection db = DatabaseConnection.getInstance().getConnection();
            if (db != null && !db.isClosed()) {
                System.out.println("Connection Successful");
                DatabaseInitializer.initializeDatabase(db);
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

