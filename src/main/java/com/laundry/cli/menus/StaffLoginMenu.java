package com.laundry.cli.menus;

import java.util.Scanner;

public class StaffLoginMenu {
    private Scanner scanner;

    public StaffLoginMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        boolean inLoginMenu = true;

        while (inLoginMenu) {
            System.out.println("\n+----------------------------+");
            System.out.println("|        STAFF LOGIN         |");
            System.out.println("+----------------------------+");
            
            System.out.print("| Enter Staff ID: ");
            String staffIdInput = scanner.nextLine();
            
            System.out.print("| Enter Full Name: ");
            String fullName = scanner.nextLine();
            
            System.out.println("+----------------------------+");
            System.out.println("| [1] LOGIN                  |");
            System.out.println("| [0] BACK                   |");
            System.out.println("+----------------------------+");
            System.out.print("SELECT OPTION: ");
            
            String loginChoice = scanner.nextLine();

            if (loginChoice.equals("1")) {
                // TODO: Database Authentication will go here
                System.out.println("\n-> Verifying credentials for ID: " + staffIdInput);
                System.out.println("-> Redirecting to Attendant/Driver MENU (To be implemented)...\n");
                inLoginMenu = false; // Exits the loop, returning to the Main Menu for now
            } else if (loginChoice.equals("0")) {
                System.out.println("\n-> Returning to Main Menu...\n");
                inLoginMenu = false; // Exits the loop, returning to the Main Menu
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }
}