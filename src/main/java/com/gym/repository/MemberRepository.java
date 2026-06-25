package com.gym.repository;

import com.gym.interfaces.Repository;
import com.gym.enums.Gender;

import com.gym.enums.MemberStatus;
import com.gym.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository implements Repository<Member, String> {
    private Connection connection;

    public MemberRepository(Connection getConnection){
        this.connection=getConnection;
    }

    private void setGeneratedId(Member member, int generatedId) {
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, String.valueOf(generatedId));
        } catch (Exception e) {
            System.out.println("Reflection error updating ID: " + e.getMessage());
        }
    }

    /**
     * Retrieve data from database and insert it to member class
     * @param member the member to insert
     * @return true if insert successful
     */
    @Override
    public boolean insert (Member member){
        if (member == null) return false;
        String script = "INSERT INTO member (fullName, gender, phoneNumber, dob, status) VALUES (?,?,?,?,?)";
        try(PreparedStatement statement = connection.prepareStatement(script, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, member.getFullName());
            statement.setString(2, member.getGender() != null ? member.getGender().name() : null);
            statement.setString(3, member.getPhoneNumber());
            statement.setDate(4, member.getDob());
            statement.setString(5, member.getMemberStatus() != null ? member.getMemberStatus().name() : null);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(member, generatedKeys.getInt(1));
                }
            }
            return true;
        }catch (SQLException e ){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing member values
     * @param member target member to update
     * @return true if successful
     */
    @Override
    public boolean update(Member member){
        if (member == null) return false;
        String query = "UPDATE member SET fullName = ?, gender = ?, phoneNumber = ?, dob = ?, status = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, member.getFullName());
            statement.setString(2, member.getGender() != null ? member.getGender().name() : null);
            statement.setString(3, member.getPhoneNumber());
            statement.setDate(4, member.getDob());
            statement.setString(5, member.getMemberStatus() != null ? member.getMemberStatus().name() : null);
            statement.setInt(6, Integer.parseInt(member.getId()));

            int rowAffect = statement.executeUpdate();
            return rowAffect > 0;

        }catch (SQLException | NumberFormatException e ){
            System.out.println("Error updating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find member by ID (standardized method name matching the Repository interface)
     * @param id the unique ID to search for
     * @return Member object or null
     */
    @Override
    public Member findById(String id ){
        if (id == null || id.isBlank()) return null;
        String queryScript = "SELECT * FROM member WHERE id = ?" ;
        try(PreparedStatement statement = connection.prepareStatement(queryScript)){
            try {
                statement.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return null;
            }

            try (ResultSet rs = statement.executeQuery()){
                if (rs.next()){
                    String dbID = String.valueOf(rs.getInt("id"));
                    String fullName = rs.getString("fullName");
                    String genderString = rs.getString("gender");
                    String phoneNumber = rs.getString("phoneNumber");
                    Date dob = rs.getDate("dob");
                    String statusStr = rs.getString("status");
                    
                    Gender gender = genderString != null ? Gender.valueOf(genderString.toUpperCase()) : Gender.MALE;
                    MemberStatus status = statusStr != null ? MemberStatus.valueOf(statusStr.toUpperCase()) : MemberStatus.INACTIVE;
                    return new Member(dbID, fullName, gender, phoneNumber, dob, status);
                }
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find member by Phone Number
     * @param phone phone number to search
     * @return Member object or null
     */
    public Member findByPhoneNumber(String phone){
        if (phone == null || phone.isBlank()) return null;
        String query = "SELECT * FROM member WHERE phoneNumber = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, phone.trim());
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    String id = String.valueOf(rs.getInt("id"));
                    String fullName = rs.getString("fullName");
                    String genderString = rs.getString("gender");
                    String phoneNumber = rs.getString("phoneNumber");
                    Date dob = rs.getDate("dob");
                    String statusStr = rs.getString("status");
                    
                    Gender gender = genderString != null ? Gender.valueOf(genderString.toUpperCase()) : Gender.MALE;
                    MemberStatus status = statusStr != null ? MemberStatus.valueOf(statusStr.toUpperCase()) : MemberStatus.INACTIVE;
                    return new Member(id, fullName, gender, phoneNumber, dob, status);
                }
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Query to get all members
     * @return list of members
     */
    @Override
    public List<Member> findAll(){
        String query = "SELECT * FROM member";
        List<Member> members = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(query)){
            try(ResultSet rs = statement.executeQuery()){
                while(rs.next()){
                    String dbID = String.valueOf(rs.getInt("id"));
                    String fullName = rs.getString("fullName");
                    String genderString = rs.getString("gender");
                    String phoneNumber = rs.getString("phoneNumber");
                    Date dob = rs.getDate("dob");
                    String statusStr = rs.getString("status");
                    
                    Gender gender = genderString != null ? Gender.valueOf(genderString.toUpperCase()) : Gender.MALE;
                    MemberStatus status = statusStr != null ? MemberStatus.valueOf(statusStr.toUpperCase()) : MemberStatus.INACTIVE;
                    members.add(new Member(dbID, fullName, gender, phoneNumber, dob, status));
                }
            }
        }catch (SQLException e ){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    /**
     * Delete member by ID (implementing Repository delete method)
     * @param id the target member ID
     * @return true if successful
     */
    @Override
    public boolean delete(String id){
        if (id == null || id.isBlank()) return false;
        String query = "DELETE FROM member WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            try {
                statement.setInt(1, Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                return false;
            }
            int rowAffect = statement.executeUpdate();
            return rowAffect > 0;
        }catch (SQLException e ){
            System.out.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper delete method for Member object
     */
    public boolean delete(Member member){
        if (member == null) return false;
        return delete(member.getId());
    }
}
