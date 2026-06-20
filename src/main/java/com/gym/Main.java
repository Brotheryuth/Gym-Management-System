package com.gym;

import com.gym.util.DatabaseConnection;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        try{
            Connection db = DatabaseConnection.getInstance().getConnection();
            if(db !=null || !db.isClosed()){
                System.out.println("Connection Successful");
            }
            else{
                System.out.println("Connection failed");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    }

