package com.gym.repository;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import com.gym.enums.MembershipStatus;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.MembershipPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipRepository {
    private Connection connection;

    public MembershipRepository(Connection connection){
        this.connection=connection;
    }

    private void setGeneratedId(Membership membership, int generatedId) {
        try {
            java.lang.reflect.Field idField = Membership.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(membership, String.valueOf(generatedId));
        } catch (Exception e) {
            System.out.println("Reflection error updating ID: " + e.getMessage());
        }
    }

    /**
     * Inserts a new membership record into the database.
     *
     * @param membership The membership object to insert
     * @return true if insert was successful, false otherwise
     */
    public boolean insert(Membership membership) {
        if (membership == null || membership.getMember() == null || membership.getPlan() == null) {
            System.out.println("Cannot insert membership: missing member or plan details.");
            return false;
        }

        String sql = "INSERT INTO memberships (planID, memberID, startDate, endDate, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, Integer.parseInt(membership.getPlan().getPlanID()));
            stmt.setInt(2, Integer.parseInt(membership.getMember().getId()));
            stmt.setDate(3, membership.getStartDate());
            stmt.setDate(4, membership.getEndDate());
            stmt.setString(5, membership.getStatus() != null ? membership.getStatus().name() : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(membership, generatedKeys.getInt(1));
                }
            }
            return true;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error inserting membership: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing membership record in the database.
     *
     * @param membership The membership with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean update(Membership membership) {
        if (membership == null || membership.getMember() == null || membership.getPlan() == null) {
            System.out.println("Cannot update membership: missing member or plan details.");
            return false;
        }

        String sql = "UPDATE memberships SET planID = ?, memberID = ?, startDate = ?, endDate = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(membership.getPlan().getPlanID()));
            stmt.setInt(2, Integer.parseInt(membership.getMember().getId()));
            stmt.setDate(3, membership.getStartDate());
            stmt.setDate(4, membership.getEndDate());
            stmt.setString(5, membership.getStatus() != null ? membership.getStatus().name() : null);
            stmt.setInt(6, Integer.parseInt(membership.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error updating membership: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a membership record from the database by its unique ID.
     *
     * @param id The ID of the membership to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        String sql = "DELETE FROM memberships WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return false;
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting membership: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a membership record from the database using the membership object.
     *
     * @param membership The membership object to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(Membership membership) {
        if (membership == null) {
            return false;
        }
        return delete(membership.getId());
    }

    public List<Membership> findAll() {
        List<Membership> memberships = new ArrayList<>();
        String sql = """
            SELECT 
                ms.id AS ms_id, ms.startDate, ms.endDate, ms.status AS ms_status,
                m.id AS m_id, m.fullName, m.gender, m.phoneNumber, m.dob, m.status AS m_status,
                p.id AS p_id, p.planPrice, p.duration
            FROM memberships ms 
            JOIN member m ON ms.memberID = m.id 
            JOIN membershipPlan p ON ms.planID = p.id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                memberships.add(mapRowToMembership(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching memberships: " + e.getMessage());
            e.printStackTrace();
        }
        return memberships;
    }

    public Membership findByID(String id){
        if (id == null || id.isBlank()) return null;
        String sql = """
            SELECT 
                ms.id AS ms_id, ms.startDate, ms.endDate, ms.status AS ms_status,
                m.id AS m_id, m.fullName, m.gender, m.phoneNumber, m.dob, m.status AS m_status,
                p.id AS p_id, p.planPrice, p.duration
            FROM memberships ms 
            JOIN member m ON ms.memberID = m.id 
            JOIN membershipPlan p ON ms.planID = p.id
            WHERE ms.id = ?
        """;
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            try {
                statement.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return null;
            }

            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    return mapRowToMembership(result);
                }
            }
        }catch (SQLException e){
            System.out.println("Error finding membership By ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Alternative way to search for membership by using member id.
     * @param memberID ID of member
     * @return membership if found
     */
    public Membership findMemberByID(String memberID){
        if (memberID == null || memberID.isBlank()) return null;
        String sql = """
            SELECT 
                ms.id AS ms_id, ms.startDate, ms.endDate, ms.status AS ms_status,
                m.id AS m_id, m.fullName, m.gender, m.phoneNumber, m.dob, m.status AS m_status,
                p.id AS p_id, p.planPrice, p.duration
            FROM memberships ms 
            JOIN member m ON ms.memberID = m.id 
            JOIN membershipPlan p ON ms.planID = p.id
            WHERE m.id = ?
        """;
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            try {
                statement.setInt(1, Integer.parseInt(memberID.trim()));
            } catch (NumberFormatException e) {
                return null;
            }

            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    return mapRowToMembership(result);
                }
            }
        }catch (SQLException e ){
            System.out.println("Error Finding membership By Member ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method to map a single database row to a Membership object.
     *
     * @param rs The active ResultSet
     * @return Fully loaded Membership object
     * @throws SQLException If database mapping fails
     */
    private Membership mapRowToMembership(ResultSet rs) throws SQLException {
        // 1. Reconstruct Member
        String mId = String.valueOf(rs.getInt("m_id"));
        String fullName = rs.getString("fullName");
        String genderStr = rs.getString("gender");
        String mPhone = rs.getString("phoneNumber");
        Date mDob = rs.getDate("dob");
        String mStatusStr = rs.getString("m_status");

        Gender gender = genderStr != null ? Gender.valueOf(genderStr.toUpperCase()) : Gender.MALE;
        MemberStatus memberStatus = mStatusStr != null ? MemberStatus.valueOf(mStatusStr.toUpperCase()) : MemberStatus.INACTIVE;

        Member member = new Member(mId, fullName, gender, mPhone, mDob, memberStatus);

        // 2. Reconstruct MembershipPlan
        String pId = String.valueOf(rs.getInt("p_id"));
        double pPrice = rs.getDouble("planPrice");
        int pDuration = rs.getInt("duration");

        MembershipPlan plan = new MembershipPlan(pId, pPrice, pDuration);

        // 3. Reconstruct Membership
        String msId = String.valueOf(rs.getInt("ms_id"));
        Date startDate = rs.getDate("startDate");
        Date endDate = rs.getDate("endDate");
        String msStatusStr = rs.getString("ms_status");

        MembershipStatus membershipStatus = msStatusStr != null ? MembershipStatus.valueOf(msStatusStr.toUpperCase()) : MembershipStatus.PENDING;

        return new Membership(msId, member, plan, startDate, endDate, membershipStatus);
    }
}
