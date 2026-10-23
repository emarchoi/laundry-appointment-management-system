package com.laundry.cli.menus;

import com.laundry.dao.StaffDAO;
import com.laundry.userclass.Staff;
import com.laundry.userclass.LaundryAttendant;
import com.laundry.userclass.DeliveryRider;
import java.util.Scanner;

public class StaffLoginMenu {
    private Scanner scanner;
    private StaffDAO staffDAO = new StaffDAO(); 

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
            String staffIdInput = scanner.nextLine().trim();
            
            System.out.print("| Enter Full Name: ");
            String fullName = scanner.nextLine().trim();
            
            System.out.println("+----------------------------------------------------+");
            System.out.println("| [1] LOGIN                                          |");
            System.out.println("| [0] BACK TO MAIN MENU                              |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");
            
            String loginChoice = scanner.nextLine().trim();

            if (loginChoice.equals("1")) {
                try {
                    int staffId = Integer.parseInt(staffIdInput);
                    
                    // Call the database to verify the login
                    Staff loggedInStaff = staffDAO.loginStaff(staffId, fullName);

                    if (loggedInStaff != null) {
                        System.out.println("\n+----------------------------------------------------+");
                        System.out.println("| SUCCESS: Logged in as " + loggedInStaff.getStaffType());
                        System.out.println("+----------------------------------------------------+");
                        
                        // Polymorphism: Route to the correct menu based on role
                        if (loggedInStaff instanceof LaundryAttendant) {
                            AttendantMenu attendantMenu = new AttendantMenu(scanner, loggedInStaff);
                            attendantMenu.display();
                        } else if (loggedInStaff instanceof DeliveryRider) {
                            DeliveryRiderMenu deliveryRiderMenu = new DeliveryRiderMenu(scanner, loggedInStaff);
                            deliveryRiderMenu.display();
                        }
                        
                        inLoginMenu = false; // Exit login loop after successful session ends
                    } else {
                        System.out.println("\n+----------------------------------------------------+");
                        System.out.println("| ERROR: Invalid Staff ID or Name. Please try again. |");
                        System.out.println("+----------------------------------------------------+");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| ERROR: Staff ID must be a number.                  |");
                    System.out.println("+----------------------------------------------------+");
                }
            } else if (loginChoice.equals("0")) {
                inLoginMenu = false; 
            } else {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| Invalid option. Please try again.                  |");
                System.out.println("+----------------------------------------------------+");
            }
        }
    }
}