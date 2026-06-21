package com.gym.repository;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import com.gym.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A repo class that perform CRUD operation
 */
public class MemberRepository {
    private Connection connection;

    public MemberRepository(Connection getConnection){
        this.connection=getConnection;
    }

    /**
     * @brief retrive data from database and insert  it to member class
     * @param member
     * @return true if insert successful
     */
    public  boolean insert (Member member){
        String script = "INSERT INTO members (id, fullName, gender, phoneNumber, dob, status) values (?,?,?,?,?,?)";
        try(PreparedStatement statement = connection.prepareStatement(script)){
            statement.setString(1,member.getId());
            statement.setString(2, member.getFullName());
            // since gender we are using enum so we can access it using .name() with ternery
            statement.setString(3 , member.getGender() != null ? member.getGender().name() : null);
            statement.setString(4, member.getPhoneNumber());
            statement.setDate(5, member.getDob());
            statement.setString(6, member.getMemberStatus().name());

            statement.executeUpdate();
            return true;
        }catch (SQLException e ){
            e.printStackTrace();
        return false;
        }
    }// close insert

    /**
     *
     * @param id to search for that id
     * @return
     */
    public  Member findByID(String id ){
        String queryScript = " select * from member where id =? " ;
        try(PreparedStatement statement = connection.prepareStatement(queryScript)){
            statement.setString(1 , id);

            try (ResultSet rs = statement.executeQuery()){
                if (rs.next()){
                    String dbID = rs.getString("id");
                    String fullName = rs.getString("fullName");
                    String genderString = rs.getString("gender");
                    String phoneNumber = rs.getString("phoneNumber");
                    Date dob = rs.getDate("dob");
                    String statusStr = rs.getString("status");
                    //parse
                    Gender gender = genderString !=null? Gender.valueOf(genderString.toUpperCase()) : Gender.MALE;
                    MemberStatus status = statusStr !=null ? MemberStatus.valueOf(statusStr.toUpperCase()) : MemberStatus.INACTIVE;
                    return  new Member(dbID , fullName,gender,phoneNumber,dob,status);
                }
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return null;
    }

    public Member findByPhoneNumber(String phone){
        String query  ="select * from member where phoneNumber = ? ";
        try(PreparedStatement statement  = connection.prepareStatement(query)){
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    String id  = rs.getString("id");
                    String fullName = rs.getString("fullname");
                    String genderString = rs.getString("gender");
                    String phoneNumber = rs.getString("phoneNumber");
                    Date dob = rs.getDate("dob");
                    String statusStr = rs.getString("status");
                    //parse
                    Gender gender = genderString !=null? Gender.valueOf(genderString.toUpperCase()) : Gender.MALE;
                    MemberStatus status = statusStr !=null ? MemberStatus.valueOf(statusStr.toUpperCase()) : MemberStatus.INACTIVE;
                    return  new Member(id , fullName,gender,phoneNumber,dob,status);
                }
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * query to get all member
     * @return
     */
    public List<Member> findAll(){
        String query = "select * from member";
        List<Member> members = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(query)){
            try(ResultSet rs = statement.executeQuery()){
                while(rs.next()){
                    String dbID = rs.getString("id");
                    String fullName = rs.getString("fullName");
                    String genderString = rs.getString("gender");
                    String phoneNumber = rs.getString("phoneNumber");
                    Date dob = rs.getDate("dob");
                    String statusStr = rs.getString("status");
                    //parse
                    Gender gender = genderString !=null? Gender.valueOf(genderString.toUpperCase()) : Gender.MALE;
                    MemberStatus status = statusStr !=null ? MemberStatus.valueOf(statusStr.toUpperCase()) : MemberStatus.INACTIVE;
                    members.add(new Member(dbID,fullName,gender,phoneNumber,dob,status));
                }
            }
        }catch (SQLException e ){
            System.out.println("Erorr:"+e.getMessage());
            e.printStackTrace();
        }
        return  members;
    }

    /**
     * @brief Update exiting value
     * @param member target member to update
     *
     */
    public  boolean update(Member member ){
        String query = "Update member set fullName =? , gender=? , phoneNumber=? , dob = ? , status =? where id=?";
       try(PreparedStatement statement = connection.prepareStatement(query)){
           statement.setString(1, member.getFullName());
           statement.setString(2,member.getGender() !=null ? member.getGender().name() : null);
           statement.setString(3,member.getPhoneNumber());
           statement.setDate(4,member.getDob());
           statement.setString(5,member.getMemberStatus() !=null ? member.getMemberStatus().name() : null);
           statement.setString(6, member.getId());

           int rowAffect = statement.executeUpdate();
           return rowAffect>0;

       }catch (SQLException e ){
           System.out.println("Error: Update member"+e.getMessage());
           e.printStackTrace();
           return false;
       }
    }

    /**
     * Delete member by accept the whole member as argument
     * @param member the target member
     * @return
     */
    public  boolean delete(Member member){
        String query = "delete from member where id = ? ";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,member.getId());
            int rowAffect = statement.executeUpdate();
            return rowAffect>0;
        }catch (SQLException e ){
            System.out.println("Error: Delete Column "+e.getMessage());
            return false;
        }
    }

    /**
     * Delete member by accept id as argument
     * @param id
     * @return
     */
    public boolean delete(String id ){
        String query = "delete from member where id =? ";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,id);
            int rowAffect = statement.executeUpdate();
            return rowAffect> 0 ;
        }catch (SQLException e ){
            System.out.println("Error Delete Member "+e.getMessage());
            return false;
        }

    }
}
