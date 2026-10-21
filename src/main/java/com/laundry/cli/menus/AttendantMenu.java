package com.laundry.cli.menus;

import com.laundry.userclass.Staff;
import java.util.Scanner;

public class AttendantMenu {
    private Scanner scanner;
    private Staff loggedInStaff;

    public AttendantMenu(Scanner scanner, Staff loggedInStaff) {
        this.scanner = scanner;
        this.loggedInStaff = loggedInStaff;
    }

    public void display() {
        boolean inAttendantMenu = true;

        while (inAttendantMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println(String.format("| %-50s |", "ATTENDANT MENU - Welcome, " + loggedInStaff.getFullName()));
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] VIEW APPOINTMENTS (Assign Machines)           |");
            System.out.println("|  [2] VIEW LAUNDRY MACHINES                         |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] LOGOUT                                        |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAppointments();
                    break;
                case "2":
                    viewLaundryMachines();
                    break;
                case "0":
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("|  Logging out... Returning to Main Menu.            |");
                    System.out.println("+----------------------------------------------------+");
                    inAttendantMenu = false;
                    break;
                default:
                    System.out.println("| Invalid option. Please try again.                  |");
            }
        }
    }

    private void viewAppointments() {
        System.out.println("\n-> Entering View Appointments Flow (To be implemented)...");
        // We will fetch pending appointments, enter weight, and do CEILING(Weight/7) here next
    }

    private void viewLaundryMachines() {
        System.out.println("\n-> Entering View Machines Flow (To be implemented)...");
        // We will fetch all Washers and Dryers from MachineDAO here later
    }
}