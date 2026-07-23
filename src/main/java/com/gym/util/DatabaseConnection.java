package com.gym.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        connect();
    }

    private synchronized void connect() {
        try {
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // Fallback to db.properties if env vars are not set
            if (url == null || url.isBlank()) {
                try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
                    if (input != null) {
                        Properties prop = new Properties();
                        prop.load(input);
                        url = prop.getProperty("db.url");
                        user = prop.getProperty("db.user");
                        password = prop.getProperty("db.password");
                    }
                }
            }

            if (url == null || url.isBlank()) {
                System.out.println("Error: Unable to find database URL configuration.");
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);

            // Auto-create gym_db and select it so all SQL tables are grouped cleanly
            try (java.sql.Statement stmt = this.connection.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS gym_db;");
                stmt.execute("USE gym_db;");
            } catch (SQLException ex) {
                System.out.println("Note: Could not run USE gym_db: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns an active database connection.
     * Automatically reconnects if the connection was dropped or timed out by the server.
     */
    public synchronized Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed() || !this.connection.isValid(3)) {
                System.out.println("Database connection lost or idle timeout. Reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            System.out.println("Error validating database connection, attempting reconnect: " + e.getMessage());
            connect();
        }
        return this.connection;
    }
}
