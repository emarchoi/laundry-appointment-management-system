package com.laundry.cli.menus;

import com.laundry.dao.StaffDAO;
import com.laundry.userclass.Staff;
import com.laundry.userclass.Attendant;
import com.laundry.userclass.DeliveryRider;
import java.util.Scanner;

public class StaffLoginMenu {
    private Scanner scanner;
    private StaffDAO staffDAO = new StaffDAO(); // Added DAO

    public StaffLoginMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        boolean inLoginMenu = true;

        while (inLoginMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("|                    STAFF LOGIN                     |");
            System.out.println("+----------------------------------------------------+");
            
            System.out.print("| Enter Staff ID: ");
            String staffIdInput = scanner.nextLine();
            
            System.out.print("| Enter Full Name: ");
            String fullName = scanner.nextLine();
            
            System.out.println("+----------------------------------------------------+");
            System.out.println("| [1] LOGIN                                          |");
            System.out.println("| [0] BACK TO MAIN MENU                              |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");
            
            String loginChoice = scanner.nextLine();

            if (loginChoice.equals("1")) {
                try {
                    int staffId = Integer.parseInt(staffIdInput);
                    
                    // Call the database to verify the login
                    Staff loggedInStaff = staffDAO.loginStaff(staffId, fullName);

                    if (loggedInStaff != null) {
                        System.out.println("\n+----------------------------------------------------+");
                        System.out.println("| SUCCESS: Logged in as " + loggedInStaff.getStaffType() + String.format("%-11s |", ""));
                        System.out.println("+----------------------------------------------------+");
                        
                        // Polymorphism: Route to the correct menu based on role
                        if (loggedInStaff instanceof Attendant) {
                            AttendantMenu attendantMenu = new AttendantMenu(scanner, loggedInStaff);
                            attendantMenu.display();
                        } else if (loggedInStaff instanceof DeliveryRider) {
                            System.out.println("-> Redirecting to Delivery Rider Menu (To be implemented)...");
                            // new DeliveryRiderMenu(scanner, loggedInStaff).display();
                        }
                        
                        inLoginMenu = false; // Exit login loop after successful session ends
                    } else {
                        System.out.println("\n| ERROR: Invalid Staff ID or Name. Please try again. |");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n| ERROR: Staff ID must be a number.                  |");
                }
            } else if (loginChoice.equals("0")) {
                inLoginMenu = false; 
            } else {
                System.out.println("| Invalid option. Please try again.                  |");
            }
        }
    }
}