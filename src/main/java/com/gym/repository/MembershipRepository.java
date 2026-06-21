package com.gym.repository;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import com.gym.enums.MembershipStatus;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.MembershipPlan;
import kotlin.jvm.internal.PackageReference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipRepository {

    private  Connection connection;

    public MembershipRepository(Connection connection){
        this.connection=connection;
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

        String sql = "INSERT INTO memberships (id, member_id, plan_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membership.getId());
            stmt.setString(2, membership.getMember().getId());
            stmt.setString(3, membership.getPlan().getPlanID());
            stmt.setDate(4, membership.getStartDate());
            stmt.setDate(5, membership.getEndDate());
            stmt.setString(6, membership.getStatus() != null ? membership.getStatus().name() : null);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
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

        String sql = "UPDATE memberships SET member_id = ?, plan_id = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membership.getMember().getId());
            stmt.setString(2, membership.getPlan().getPlanID());
            stmt.setDate(3, membership.getStartDate());
            stmt.setDate(4, membership.getEndDate());
            stmt.setString(5, membership.getStatus() != null ? membership.getStatus().name() : null);
            stmt.setString(6, membership.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
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
        String sql = "DELETE FROM memberships WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

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
        return delete(membership.getId()); // Delegate to the delete(String id) method to keep code DRY
    }


    public List<Membership> findAll() {
        List<Membership> memberships = new ArrayList<>();
        String sql = """
            SELECT 
                ms.id AS ms_id, ms.start_date, ms.end_date, ms.status AS ms_status,
                m.id AS m_id, m.full_name, m.gender, m.phone_number, m.dob, m.status AS m_status,
                p.id AS p_id, p.price, p.duration
            FROM memberships ms 
            JOIN members m ON ms.member_id = m.id 
            JOIN membership_plans p ON ms.plan_id = p.id
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
        String sql = """
            SELECT 
                ms.id AS ms_id, ms.start_date, ms.end_date, ms.status AS ms_status,
                m.id AS m_id, m.full_name, m.gender, m.phone_number, m.dob, m.status AS m_status,
                p.id AS p_id, p.price, p.duration
            FROM memberships ms 
            JOIN members m ON ms.member_id = m.id 
            JOIN membership_plans p ON ms.plan_id = p.id
            WHERE ms.id = ?
        """;
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            try(ResultSet result  = statement.executeQuery()){
                if(result.next()){
                    return  mapRowToMembership(result);
                }
            }
        }catch (SQLException e){
            System.out.println("Error find membership By ID."+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * alternative way to search for membership by using memebr id.
     * @param memberID
     * @return membership if found
     */
    public Membership findMemberByID(String memberID){
        String sql = """
            SELECT 
                ms.id AS ms_id, ms.start_date, ms.end_date, ms.status AS ms_status,
                m.id AS m_id, m.full_name, m.gender, m.phone_number, m.dob, m.status AS m_status,
                p.id AS p_id, p.price, p.duration
            FROM memberships ms 
            JOIN members m ON ms.member_id = m.id 
            JOIN membership_plans p ON ms.plan_id = p.id
            WHERE m.id = ?
        """;
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,memberID);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                return mapRowToMembership(result);
                }
            }
        }catch (SQLException e ){
            System.out.println("Error Find membership By Member id. "+e.getMessage());
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
        String mId = rs.getString("m_id");
        String fullName = rs.getString("full_name");
        String genderStr = rs.getString("gender");
        String mPhone = rs.getString("phone_number");
        Date mDob = rs.getDate("dob");
        String mStatusStr = rs.getString("m_status");

        Gender gender = genderStr != null ? Gender.valueOf(genderStr.toUpperCase()) : Gender.MALE;
        MemberStatus memberStatus = mStatusStr != null ? MemberStatus.valueOf(mStatusStr.toUpperCase()) : MemberStatus.INACTIVE;

        Member member = new Member(mId, fullName, gender, mPhone, mDob, memberStatus);

        // 2. Reconstruct MembershipPlan
        String pId = rs.getString("p_id");
        double pPrice = rs.getDouble("price");
        int pDuration = rs.getInt("duration");

        MembershipPlan plan = new MembershipPlan(pId, pPrice, pDuration);

        // 3. Reconstruct Membership
        String msId = rs.getString("ms_id");
        Date startDate = rs.getDate("start_date");
        Date endDate = rs.getDate("end_date");
        String msStatusStr = rs.getString("ms_status");

        MembershipStatus membershipStatus = msStatusStr != null ? MembershipStatus.valueOf(msStatusStr.toUpperCase()) : MembershipStatus.PENDING;

        return new Membership(msId, member, plan, startDate, endDate, membershipStatus);
    }

}
