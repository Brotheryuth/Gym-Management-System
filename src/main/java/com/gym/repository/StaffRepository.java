package com.gym.repository;

import com.gym.interfaces.Repository;
import com.gym.model.Staff;
import com.gym.enums.Gender;
import com.gym.enums.StaffRole;
import com.gym.enums.StaffShift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository implements Repository<Staff, String> {
    private final Connection connection;

    public StaffRepository(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        try {
            Connection active = DatabaseConnection.getInstance().getConnection();
            if (active != null && !active.isClosed()) {
                return active;
            }
        } catch (Exception ignored) {}
        return this.connection;
    }

    private void setGeneratedId(Staff staff, int generatedId) {
        staff.setId(String.valueOf(generatedId));
    }

    /**
     * Inserts a new staff member into the database.
     *
     * @param staff The staff object to save
     * @return true if successful, false otherwise
     */
    @Override
    public boolean insert(Staff staff) {
        if (staff == null) return false;

        String sql = """
            INSERT INTO staff (name, gender, dob, salary, phoneNumber, password, role, shift, hireDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getGender() != null ? staff.getGender().name() : null);
            stmt.setDate(3, staff.getDob());
            stmt.setDouble(4, staff.getSalary());
            stmt.setString(5, staff.getPhoneNumber());
            stmt.setString(6, staff.getPassword());
            stmt.setString(7, staff.getRole() != null ? staff.getRole().name() : null);
            stmt.setString(8, staff.getShift() != null ? staff.getShift().name() : null);
            stmt.setDate(9, staff.getHireDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(staff, generatedKeys.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing staff member's record in the database.
     *
     * @param staff The staff object with updated values
     * @return true if successful, false otherwise
     */
    @Override
    public boolean update(Staff staff) {
        if (staff == null) return false;

        String sql = """
            UPDATE staff 
            SET name = ?, gender = ?, dob = ?, salary = ?, phoneNumber = ?, password = ?, role = ?, shift = ?, hireDate = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getGender() != null ? staff.getGender().name() : null);
            stmt.setDate(3, staff.getDob());
            stmt.setDouble(4, staff.getSalary());
            stmt.setString(5, staff.getPhoneNumber());
            stmt.setString(6, staff.getPassword());
            stmt.setString(7, staff.getRole() != null ? staff.getRole().name() : null);
            stmt.setString(8, staff.getShift() != null ? staff.getShift().name() : null);
            stmt.setDate(9, staff.getHireDate());
            stmt.setInt(10, Integer.parseInt(staff.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error updating staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a staff member by their unique ID.
     *
     * @param id The staff ID
     * @return The Staff object if found, or null
     */
    @Override
    public Staff findById(String id) {
        if (id == null || id.isBlank()) return null;
        String sql = "SELECT * FROM staff WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return null;
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbId = String.valueOf(rs.getInt("id"));
                    String name = rs.getString("name");
                    String genderStr = rs.getString("gender");
                    Date dob = rs.getDate("dob");
                    double salary = rs.getDouble("salary");
                    String phone = rs.getString("phoneNumber");
                    String password = rs.getString("password");
                    String roleStr = rs.getString("role");
                    String shiftStr = rs.getString("shift");
                    Date hireDate = rs.getDate("hireDate");

                    Gender gender = genderStr != null ? Gender.valueOf(genderStr.toUpperCase()) : Gender.MALE;
                    StaffRole role = roleStr != null ? StaffRole.valueOf(roleStr.toUpperCase()) : StaffRole.CASHIER;
                    StaffShift shift = shiftStr != null ? StaffShift.valueOf(shiftStr.toUpperCase()) : StaffShift.MORNING;

                    return new Staff(dbId, name, gender, dob, salary, phone, password, role, shift, hireDate);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding staff by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all staff members from the database.
     *
     * @return A list of all staff
     */
    @Override
    public List<Staff> findAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dbId = String.valueOf(rs.getInt("id"));
                String name = rs.getString("name");
                String genderStr = rs.getString("gender");
                Date dob = rs.getDate("dob");
                double salary = rs.getDouble("salary");
                String phone = rs.getString("phoneNumber");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                String shiftStr = rs.getString("shift");
                Date hireDate = rs.getDate("hireDate");

                Gender gender = genderStr != null ? Gender.valueOf(genderStr.toUpperCase()) : Gender.MALE;
                StaffRole role = roleStr != null ? StaffRole.valueOf(roleStr.toUpperCase()) : StaffRole.CASHIER;
                StaffShift shift = shiftStr != null ? StaffShift.valueOf(shiftStr.toUpperCase()) : StaffShift.MORNING;

                staffList.add(new Staff(dbId, name, gender, dob, salary, phone, password, role, shift, hireDate));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all staff: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Deletes a staff member from the database.
     *
     * @param id The ID of the staff member to delete
     * @return true if successful, false otherwise
     */
    @Override
    public boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        String sql = "DELETE FROM staff WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return false;
            }
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
