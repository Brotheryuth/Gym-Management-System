package com.gym.repository;

import com.gym.model.MembershipPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MembershipPlanRepository {
    private final Connection connection;

    public MembershipPlanRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new membership plan into the database.
     *
     * @param plan The membership plan to save
     * @return true if successful, false otherwise
     */
    public boolean insert(MembershipPlan plan) {
        if (plan == null) return false;

        String sql = "INSERT INTO membership_plans (id, price, duration) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plan.getPlanID());
            stmt.setDouble(2, plan.getPlanPrice());
            stmt.setInt(3, plan.getDuration());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting membership plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing plan's price and duration in the database.
     *
     * @param plan The updated membership plan
     * @return true if successful, false otherwise
     */
    public boolean update(MembershipPlan plan) {
        if (plan == null) return false;

        String sql = "UPDATE membership_plans SET price = ?, duration = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, plan.getPlanPrice());
            stmt.setInt(2, plan.getDuration());
            stmt.setString(3, plan.getPlanID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating membership plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a membership plan by its ID.
     *
     * @param id The plan ID
     * @return The MembershipPlan if found, or null
     */
    public MembershipPlan findById(String id) {
        String sql = "SELECT * FROM membership_plans WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MembershipPlan(
                            rs.getString("id"),
                            rs.getDouble("price"),
                            rs.getInt("duration")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding membership plan by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all membership plans in the database.
     *
     * @return A list of all plans
     */
    public List<MembershipPlan> findAll() {
        List<MembershipPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM membership_plans";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                plans.add(new MembershipPlan(
                        rs.getString("id"),
                        rs.getDouble("price"),
                        rs.getInt("duration")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all plans: " + e.getMessage());
            e.printStackTrace();
        }
        return plans;
    }

    /**
     * Deletes a plan by ID.
     *
     * @param id The ID of the plan to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(String id) {
        String sql = "DELETE FROM membership_plans WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
