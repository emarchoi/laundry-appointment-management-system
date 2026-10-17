package com.laundry.cli;

import com.laundry.config.DatabaseConnection;
import com.laundry.models.Customer;
import com.laundry.services.CustomerService;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Phase 1: Testing Database Connection ---");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("SUCCESS: Connected to MS SQL Server!");
            }
        } catch (Exception e) {
            System.err.println("FAILED: Could not connect to the database.");
            e.printStackTrace();
            return; 
        }

        System.out.println("\n--- Phase 2: Testing Customer Insertion ---");
        CustomerService service = new CustomerService();
        
        // Create a dummy customer matching your SQL schema
        Customer testCustomer = new Customer(
            "12350", 
            "Juan", 
            "Dela Cruz", 
            "09123456789", 
            "San Jose", 
            "Mabini St", 
            "Block 4"
        );

        try {
            boolean isRegistered = service.registerCustomer(testCustomer);
            if (isRegistered) {
                System.out.println("SUCCESS: Customer successfully saved to the database!");
            } else {
                System.out.println("FAILED: Customer was not saved.");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Validation or Database issue -> " + e.getMessage());
        }
    }
}