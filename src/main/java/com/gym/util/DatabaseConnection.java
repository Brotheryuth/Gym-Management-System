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

            // Auto-create gym_db and select it so that all SQL tables are grouped cleanly
            try (java.sql.Statement stmt = this.connection.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS gym_db;");
                stmt.execute("USE gym_db;");
            } catch (SQLException ex) {
                System.out.println("Note: Could not run USE gym_db, using default catalog: " + ex.getMessage());
            }
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
