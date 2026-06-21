package com.gym.repository;

import com.gym.model.Staff;
import com.gym.enums.Gender;
import com.gym.enums.StaffRole;
import com.gym.enums.StaffShift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    private final Connection connection;

    public StaffRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new staff member into the database.
     *
     * @param staff The staff object to save
     * @return true if successful, false otherwise
     */
    public boolean insert(Staff staff) {
        if (staff == null) return false;

        String sql = """
            INSERT INTO staff (id, name, gender, dob, salary, phoneNumber, password, role, shift, hire_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, staff.getId());
            stmt.setString(2, staff.getName());
            stmt.setString(3, staff.getGender() != null ? staff.getGender().name() : null);
            stmt.setDate(4, staff.getDob());
            stmt.setDouble(5, staff.getSalary());
            stmt.setString(6, staff.getPhoneNumber());
            stmt.setString(7, staff.getPassword());
            stmt.setString(8, staff.getRole() != null ? staff.getRole().name() : null);
            stmt.setString(9, staff.getShift() != null ? staff.getShift().name() : null);
            stmt.setDate(10, staff.getHireDate());

            stmt.executeUpdate();
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
    public boolean update(Staff staff) {
        if (staff == null) return false;

        String sql = """
            UPDATE staff 
            SET name = ?, gender = ?, dob = ?, salary = ?, phoneNumber = ?, password = ?, role = ?, shift = ?, hire_date = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getGender() != null ? staff.getGender().name() : null);
            stmt.setDate(3, staff.getDob());
            stmt.setDouble(4, staff.getSalary());
            stmt.setString(5, staff.getPhoneNumber());
            stmt.setString(6, staff.getPassword());
            stmt.setString(7, staff.getRole() != null ? staff.getRole().name() : null);
            stmt.setString(8, staff.getShift() != null ? staff.getShift().name() : null);
            stmt.setDate(9, staff.getHireDate());
            stmt.setString(10, staff.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
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
    public Staff findById(String id) {
        String sql = "SELECT * FROM staff WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbId = rs.getString("id");
                    String name = rs.getString("name");
                    String genderStr = rs.getString("gender");
                    Date dob = rs.getDate("dob");
                    double salary = rs.getDouble("salary");
                    String phone = rs.getString("phoneNumber");
                    String password = rs.getString("password");
                    String roleStr = rs.getString("role");
                    String shiftStr = rs.getString("shift");
                    Date hireDate = rs.getDate("hire_date");

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
    public List<Staff> findAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dbId = rs.getString("id");
                String name = rs.getString("name");
                String genderStr = rs.getString("gender");
                Date dob = rs.getDate("dob");
                double salary = rs.getDouble("salary");
                String phone = rs.getString("phoneNumber");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                String shiftStr = rs.getString("shift");
                Date hireDate = rs.getDate("hire_date");

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
    public boolean delete(String id) {
        String sql = "DELETE FROM staff WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
