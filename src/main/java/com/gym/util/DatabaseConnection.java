package com.gym.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private  static  DatabaseConnection instance;
    private Connection connection;
    private  DatabaseConnection(){
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")){
            Properties prop = new Properties();
            // check input
            if(input==null){
                System.out.println("Error, unable to find db properties.");
                return;
            }
            prop.load(input);
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url,user,password);
        }
        catch (Exception e ){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }//end constructor
    public static  DatabaseConnection getInstance() throws SQLException{
        if(instance==null || instance.connection.isClosed() || instance.getConnection()==null){
            instance = new DatabaseConnection();
        }
        return  instance; // if no return instance
    }
    //getter
    public Connection getConnection(){
        return this.connection;
    }
}
