package com.laundry.cli.menus;

import com.laundry.dao.StaffDAO;
import com.laundry.userclass.Staff;
import com.laundry.userclass.LaundryAttendant;
import com.laundry.userclass.DeliveryRider;
import java.util.Scanner;

/*
    User interface menu for staff login and authentication.
    Verifies staff credentials with the database and routes logged-in staff 
    to their respective role menus (Attendant or Delivery Rider).
*/
public class StaffLoginMenu {

    // Inputs and DAO for database CRUD queries 
    private Scanner scanner;
    private StaffDAO staffDAO = new StaffDAO(); 

    public StaffLoginMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    // Entry point, display loop showing UI for the Staff Login Menu
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
                    
                    // Convert string ot int
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