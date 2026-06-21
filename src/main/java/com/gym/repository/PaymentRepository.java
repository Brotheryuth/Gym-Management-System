package com.gym.repository;

import com.gym.model.*;
import com.gym.enums.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private final Connection connection;

    public PaymentRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Helper method to map a single database row to a Payment object.
     */
    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        // 1. Reconstruct Member
        Member member = new Member(
                rs.getString("m_id"),
                rs.getString("full_name"),
                Gender.valueOf(rs.getString("gender").toUpperCase()),
                rs.getString("phone_number"),
                rs.getDate("dob"),
                MemberStatus.valueOf(rs.getString("m_status").toUpperCase())
        );

        // 2. Reconstruct MembershipPlan
        MembershipPlan plan = new MembershipPlan(
                rs.getString("plan_id"),
                rs.getDouble("price"),
                rs.getInt("duration")
        );

        // 3. Reconstruct Membership
        Membership membership = new Membership(
                rs.getString("ms_id"),
                member,
                plan,
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                MembershipStatus.valueOf(rs.getString("ms_status").toUpperCase())
        );

        // 4. Reconstruct Payment using getObject for LocalDateTime mapping
        return new Payment(
                rs.getString("p_id"),
                membership,
                rs.getDouble("base_amount"),
                rs.getInt("discount"),
                PaymentMethod.valueOf(rs.getString("payment_method").toUpperCase()),
                PaymentStatus.valueOf(rs.getString("payment_status").toUpperCase()),
                rs.getObject("create_at", LocalDateTime.class),
                rs.getObject("payment_date", LocalDateTime.class)
        );
    }

    /**
     * Inserts a new payment record into the database.
     */
    public boolean insert(Payment payment) {
        if (payment == null || payment.getMembership() == null) {
            System.out.println("Cannot insert payment: missing membership details.");
            return false;
        }

        String sql = "INSERT INTO payments (id, membership_id, base_amount, discount, payment_method, payment_status, create_at, payment_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getId());
            stmt.setString(2, payment.getMembership().getId());
            stmt.setDouble(3, payment.getBaseAmount());
            stmt.setInt(4, payment.getDiscount());
            stmt.setString(5, payment.getMethod() != null ? payment.getMethod().name() : null);
            stmt.setString(6, payment.getStatus() != null ? payment.getStatus().name() : null);

            // Good Practice: Use setObject to insert Java 8 LocalDate/LocalDateTime
            stmt.setObject(7, payment.getCreateAt());
            stmt.setObject(8, payment.getPaymentDate());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing payment's status and execution date.
     */
    public boolean update(Payment payment) {
        if (payment == null) return false;

        String sql = "UPDATE payments SET payment_status = ?, payment_date = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getStatus() != null ? payment.getStatus().name() : null);
            stmt.setObject(2, payment.getPaymentDate()); // Good Practice: Use setObject for LocalDateTime
            stmt.setString(3, payment.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a payment by its unique ID.
     */
    public Payment findById(String id) {
        String sql = """
            SELECT 
                p.id AS p_id, p.base_amount, p.discount, p.payment_method, p.payment_status, p.create_at, p.payment_date,
                ms.id AS ms_id, ms.start_date, ms.end_date, ms.status AS ms_status,
                m.id AS m_id, m.full_name, m.gender, m.phone_number, m.dob, m.status AS m_status,
                plan.id AS plan_id, plan.price, plan.duration
            FROM payments p
            JOIN memberships ms ON p.membership_id = ms.id
            JOIN members m ON ms.member_id = m.id
            JOIN membership_plans plan ON ms.plan_id = plan.id
            WHERE p.id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPayment(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding payment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all payments.
     */
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT 
                p.id AS p_id, p.base_amount, p.discount, p.payment_method, p.payment_status, p.create_at, p.payment_date,
                ms.id AS ms_id, ms.start_date, ms.end_date, ms.status AS ms_status,
                m.id AS m_id, m.full_name, m.gender, m.phone_number, m.dob, m.status AS m_status,
                plan.id AS plan_id, plan.price, plan.duration
            FROM payments p
            JOIN memberships ms ON p.membership_id = ms.id
            JOIN members m ON ms.member_id = m.id
            JOIN membership_plans plan ON ms.plan_id = plan.id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                payments.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    /**
     * Deletes a payment by ID.
     */
    public boolean delete(String id) {
        String sql = "DELETE FROM payments WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
