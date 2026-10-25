package com.laundry.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
   Manages and establishes connections from here and
   and to our MS SQL Server database.
*/

public class DatabaseConnection {

    private static Properties properties = new Properties();
    
    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Ensure config.properties exists in the root directory!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            properties.getProperty("DB_URL"),
            properties.getProperty("DB_USER"),
            properties.getProperty("DB_PASSWORD")
        );
    }
}