package com.laundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.laundry.config.DatabaseConnection;
import com.laundry.userclass.Customer;

public class CustomerDAO {
    // Example CRUD Operation
    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO Customer (ReferenceNumber, FirstName, LastName, PhoneNumber, Barangay, Street, BlockNo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(query)) {
             
            s.setString(1, customer.getReferenceNumber());
            s.setString(2, customer.getFirstName());
            s.setString(3, customer.getLastName());
            s.setString(4, customer.getPhoneNumber());
            s.setString(5, customer.getBarangay());
            s.setString(6, customer.getStreet());
            s.setString(7, customer.getBlockNo());
            // ... set remaining parameters
            
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            return false;
        }
    }
}