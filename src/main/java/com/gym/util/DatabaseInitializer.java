package com.gym.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase(Connection connection) {
        if (connection == null) {
            System.out.println("Cannot initialize database: connection is null.");
            return;
        }

        System.out.println("Initializing database tables...");

        try (Statement stmt = connection.createStatement()) {
            // Clean up accidentally created tables from previous run
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS membership_plans");
                stmt.executeUpdate("DROP TABLE IF EXISTS members");
            } catch (SQLException e) {
                // Ignore if they cannot be dropped or fail due to FKs
            }

            // 1. Create member table
            String createMemberTable = """
                CREATE TABLE IF NOT EXISTS member (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    fullName VARCHAR(50) NOT NULL,
                    gender VARCHAR(10) NOT NULL,
                    phoneNumber VARCHAR(15) DEFAULT 'N/A' NULL,
                    dob DATE NULL,
                    status VARCHAR(10) DEFAULT 'INACTIVE' NOT NULL,
                    CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
                    CHECK (status IN ('ACTIVE', 'INACTIVE'))
                );
                """;
            stmt.executeUpdate(createMemberTable);

            // 2. Create membershipPlan table
            String createMembershipPlanTable = """
                CREATE TABLE IF NOT EXISTS membershipPlan (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    planName VARCHAR(50) NOT NULL,
                    duration INT NOT NULL,
                    planPrice DECIMAL(10, 2) NOT NULL,
                    CHECK (duration >= 1),
                    CHECK (planPrice >= 0)
                );
                """;
            stmt.executeUpdate(createMembershipPlanTable);

            // 3. Create memberships table
            String createMembershipsTable = """
                CREATE TABLE IF NOT EXISTS memberships (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    planID INT NOT NULL,
                    memberID INT NOT NULL,
                    startDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                    endDate TIMESTAMP NOT NULL,
                    status VARCHAR(15) DEFAULT 'PENDING' NOT NULL,
                    CONSTRAINT fk_membership_member
                        FOREIGN KEY (memberID) REFERENCES member (id)
                        ON DELETE CASCADE,
                    CONSTRAINT fk_membership_plan
                        FOREIGN KEY (planID) REFERENCES membershipPlan (id),
                    CHECK (status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'PENDING', 'CANCELLED'))
                );
                """;
            stmt.executeUpdate(createMembershipsTable);

            // 4. Create payment table
            String createPaymentTable = """
                CREATE TABLE IF NOT EXISTS payment (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    membershipID INT NOT NULL,
                    baseAmount DECIMAL(10, 2) NOT NULL,
                    finalAmount DECIMAL(10, 2) NOT NULL,
                    discount DECIMAL(3, 2) DEFAULT 0.00 NULL,
                    method VARCHAR(20) DEFAULT 'BYCASH' NULL,
                    status VARCHAR(20) DEFAULT 'PENDING' NULL,
                    createAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
                    paymentDate TIMESTAMP NULL,
                    CONSTRAINT fk_payment_membership
                        FOREIGN KEY (membershipID) REFERENCES memberships (id)
                        ON DELETE CASCADE,
                    CHECK (baseAmount >= 0),
                    CHECK (finalAmount >= 0),
                    CHECK (discount >= 0.0 AND discount <= 1.0),
                    CHECK (method IN ('BYCASH', 'KHQR', 'CREDITCARD')),
                    CHECK (status IN ('PAID', 'PENDING', 'FAILED'))
                );
                """;
            stmt.executeUpdate(createPaymentTable);

            // 5. Create staff table
            String createStaffTable = """
                CREATE TABLE IF NOT EXISTS staff (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    gender VARCHAR(10) NOT NULL,
                    dob DATE NULL,
                    salary DECIMAL(10, 2) NOT NULL,
                    phoneNumber VARCHAR(15) DEFAULT 'N/A' NULL,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    shift VARCHAR(15) NOT NULL,
                    hireDate DATE NULL,
                    CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
                    CHECK (salary >= 0),
                    CHECK (role IN ('ADMIN', 'CASHIER')),
                    CHECK (shift IN ('MORNING', 'AFTERNOON', 'NIGHT', 'FULLTIME'))
                );
                """;
            stmt.executeUpdate(createStaffTable);

            // Seed default admin if staff table is empty
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM staff")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Seeding default admin staff...");
                    String seedAdmin = """
                        INSERT INTO staff (name, gender, dob, salary, phoneNumber, password, role, shift, hireDate)
                        VALUES ('admin', 'MALE', '1990-01-01', 0.0, '012345678', 'admin123', 'ADMIN', 'FULLTIME', CURRENT_DATE)
                        """;
                    stmt.executeUpdate(seedAdmin);
                }
            }

            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
