package com.gym.repository;

import com.gym.model.*;
import com.gym.enums.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository implements Repository<Payment, String> {
    private final Connection connection;

    public PaymentRepository(Connection connection) {
        this.connection = connection;
    }

    private void setGeneratedId(Payment payment, int generatedId) {
        try {
            java.lang.reflect.Field idField = Payment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(payment, String.valueOf(generatedId));
        } catch (Exception e) {
            System.out.println("Reflection error updating ID: " + e.getMessage());
        }
    }

    /**
     * Helper method to map a single database row to a Payment object.
     */
    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        // 1. Reconstruct Member
        Member member = new Member(
                String.valueOf(rs.getInt("m_id")),
                rs.getString("fullName"),
                Gender.valueOf(rs.getString("gender").toUpperCase()),
                rs.getString("phoneNumber"),
                rs.getDate("dob"),
                MemberStatus.valueOf(rs.getString("m_status").toUpperCase())
        );

        // 2. Reconstruct MembershipPlan
        MembershipPlan plan = new MembershipPlan(
                String.valueOf(rs.getInt("plan_id")),
                rs.getString("plan_name"),
                rs.getDouble("planPrice"),
                rs.getInt("duration")
        );

        // 3. Reconstruct Membership
        Membership membership = new Membership(
                String.valueOf(rs.getInt("ms_id")),
                member,
                plan,
                rs.getDate("startDate"),
                rs.getDate("endDate"),
                MembershipStatus.valueOf(rs.getString("ms_status").toUpperCase())
        );

        // 4. Reconstruct Payment using getObject for LocalDateTime mapping
        double dbDiscount = rs.getDouble("discount");
        int discountPercentage = (int) Math.round(dbDiscount * 100.0);

        return new Payment(
                String.valueOf(rs.getInt("p_id")),
                membership,
                rs.getDouble("baseAmount"),
                discountPercentage,
                PaymentMethod.valueOf(rs.getString("method").toUpperCase()),
                PaymentStatus.valueOf(rs.getString("p_status").toUpperCase()),
                rs.getObject("createAt", LocalDateTime.class),
                rs.getObject("paymentDate", LocalDateTime.class)
        );
    }

    /**
     * Inserts a new payment record into the database.
     */
    @Override
    public boolean insert(Payment payment) {
        if (payment == null || payment.getMembership() == null) {
            System.out.println("Cannot insert payment: missing membership details.");
            return false;
        }

        String sql = "INSERT INTO payment (membershipID, baseAmount, finalAmount, discount, method, status, createAt, paymentDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, Integer.parseInt(payment.getMembership().getId()));
            stmt.setDouble(2, payment.getBaseAmount());
            stmt.setDouble(3, payment.getFinalAmount());
            stmt.setDouble(4, (double) payment.getDiscount() / 100.0);
            stmt.setString(5, payment.getMethod() != null ? payment.getMethod().name() : null);
            stmt.setString(6, payment.getStatus() != null ? payment.getStatus().name() : null);

            // Good Practice: Use setObject to insert Java 8 LocalDate/LocalDateTime
            stmt.setObject(7, payment.getCreateAt());
            stmt.setObject(8, payment.getPaymentDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(payment, generatedKeys.getInt(1));
                }
            }
            return true;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing payment's status and execution date.
     */
    @Override
    public boolean update(Payment payment) {
        if (payment == null) return false;

        String sql = "UPDATE payment SET status = ?, paymentDate = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getStatus() != null ? payment.getStatus().name() : null);
            stmt.setObject(2, payment.getPaymentDate()); // Good Practice: Use setObject for LocalDateTime
            stmt.setInt(3, Integer.parseInt(payment.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a payment by its unique ID.
     */
    @Override
    public Payment findById(String id) {
        if (id == null || id.isBlank()) return null;
        String sql = """
            SELECT 
                p.id AS p_id, p.baseAmount, p.finalAmount, p.discount, p.method, p.status AS p_status, p.createAt, p.paymentDate,
                ms.id AS ms_id, ms.startDate, ms.endDate, ms.status AS ms_status,
                m.id AS m_id, m.fullName, m.gender, m.phoneNumber, m.dob, m.status AS m_status,
                plan.id AS plan_id, plan.planName AS plan_name, plan.planPrice, plan.duration
            FROM payment p
            JOIN memberships ms ON p.membershipID = ms.id
            JOIN member m ON ms.memberID = m.id
            JOIN membershipPlan plan ON ms.planID = plan.id
            WHERE p.id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return null;
            }

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
    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT 
                p.id AS p_id, p.baseAmount, p.finalAmount, p.discount, p.method, p.status AS p_status, p.createAt, p.paymentDate,
                ms.id AS ms_id, ms.startDate, ms.endDate, ms.status AS ms_status,
                m.id AS m_id, m.fullName, m.gender, m.phoneNumber, m.dob, m.status AS m_status,
                plan.id AS plan_id, plan.planName AS plan_name, plan.planPrice, plan.duration
            FROM payment p
            JOIN memberships ms ON p.membershipID = ms.id
            JOIN member m ON ms.memberID = m.id
            JOIN membershipPlan plan ON ms.planID = plan.id
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
    @Override
    public boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        String sql = "DELETE FROM payment WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return false;
            }
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
