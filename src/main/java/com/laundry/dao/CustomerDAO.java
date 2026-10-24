package com.laundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.laundry.config.DatabaseConnection;
import com.laundry.userclass.Customer;

import java.util.List;
import java.util.ArrayList;
public class CustomerDAO {
    // Example CRUD Operation
    public boolean addCustomer(Customer customer) {
        String q = "INSERT INTO Customer (ReferenceNumber, FirstName, LastName, PhoneNumber, Barangay, Street, BlockNo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setString(1, customer.getReferenceNumber());
            s.setString(2, customer.getFirstName());
            s.setString(3, customer.getLastName());
            s.setString(4, customer.getPhoneNumber());
            s.setString(5, customer.getBarangay());
            s.setString(6, customer.getStreet());
            s.setString(7, customer.getBlockNo());
            
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Create Customer): " + e.getMessage());
            return false;
        }
    }

    public Customer getCustomerDetails(String referenceNumber) {

        String q = "SELECT * FROM Customer "
                    + "WHERE ReferenceNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, referenceNumber);

            try (ResultSet rs = s.executeQuery()) {

                if (rs.next()) {

                    String refNum = rs.getString("ReferenceNumber");
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String phoneNum = rs.getString("PhoneNumber");
                    String barangay =  rs.getString("Barangay");
                    String street = rs.getString("Street");
                    String blkNo = rs.getString("BlockNo");

                    Customer customer = new Customer(firstName, lastName, refNum, phoneNum, barangay, street, blkNo);
                    
                    return customer;
                }
            }


        } catch (SQLException e) {
            System.err.println("Database Error (Get Customer Details): " + e.getMessage());
        }
        return null;
    }
}