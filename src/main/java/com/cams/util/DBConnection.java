package com.cams.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cams";
    private static final String USER = "root";
    private static final String PASSWORD = "Monunayan@27"; // Update this based on local MySQL setup

    private DBConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console.");
            e.printStackTrace();
        }
        return null;
    }
}
