package com.laundry.cli;

import java.sql.Connection;
import java.util.Scanner;

import com.laundry.config.DatabaseConnection;
import com.laundry.userclass.Customer;
import com.laundry.util.CustomerService;

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

        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter Reference Number: ");
        String referenceNumber = sc.nextLine();

        System.out.print("Enter First Name: ");
        String fullName = sc.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = sc.nextLine();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = sc.nextLine();

        System.out.print("Enter Barangay: ");
        String barangay = sc.nextLine();

        System.out.print("Enter Street: ");
        String street = sc.nextLine();

        System.out.print("Enter Block No: ");
        String blockNo = sc.nextLine();
        
        // Create a dummy customer matching your SQL schema
        Customer testCustomer = new Customer(
                referenceNumber,
                fullName,
                lastName,
                phoneNumber,
                barangay,
                street,
                blockNo
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