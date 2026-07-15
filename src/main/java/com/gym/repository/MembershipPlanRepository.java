package com.gym.repository;

import com.gym.interfaces.Repository;
import com.gym.model.MembershipPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipPlanRepository implements Repository<MembershipPlan, String> {
    private final Connection connection;

    public MembershipPlanRepository(Connection connection) {
        this.connection = connection;
    }

    private void setGeneratedId(MembershipPlan plan, int generatedId) {
        plan.setPlanID(String.valueOf(generatedId));
    }

    /**
     * Inserts a new membership plan into the database.
     *
     * @param plan The membership plan to save
     * @return true if successful, false otherwise
     */
    @Override
    public boolean insert(MembershipPlan plan) {
        if (plan == null) return false;

        String sql = "INSERT INTO membershipPlan (planName, duration, planPrice) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, plan.getPlanName());
            stmt.setInt(2, plan.getDuration());
            stmt.setDouble(3, plan.getPlanPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(plan, generatedKeys.getInt(1));
                }
            }
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
    @Override
    public boolean update(MembershipPlan plan) {
        if (plan == null) return false;

        String sql = "UPDATE membershipPlan SET planName = ?, duration = ?, planPrice = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plan.getPlanName());
            stmt.setInt(2, plan.getDuration());
            stmt.setDouble(3, plan.getPlanPrice());
            stmt.setInt(4, Integer.parseInt(plan.getPlanID()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | NumberFormatException e) {
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
    @Override
    public MembershipPlan findById(String id) {
        if (id == null || id.isBlank()) return null;
        String sql = "SELECT * FROM membershipPlan WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return null;
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MembershipPlan(
                            String.valueOf(rs.getInt("id")),
                            rs.getString("planName"),
                            rs.getDouble("planPrice"),
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
    @Override
    public List<MembershipPlan> findAll() {
        List<MembershipPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM membershipPlan";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                plans.add(new MembershipPlan(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("planName"),
                        rs.getDouble("planPrice"),
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
    @Override
    public boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        String sql = "DELETE FROM membershipPlan WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return false;
            }
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
