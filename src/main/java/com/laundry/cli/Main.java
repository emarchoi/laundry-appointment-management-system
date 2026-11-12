package com.laundry.cli;

/*
    The cli package contains the console-based UI classes
    used to interact with the laundry management system.
*/

import com.laundry.cli.menus.BookingMenu;
import com.laundry.cli.menus.StaffLoginMenu;

import java.util.Scanner;

// Main entry point of the Laundry Appointment System
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("|                     QUICK RINSE                    |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] BOOK APPOINTMENT                              |");
            System.out.println("|  [2] STAFF LOG IN                                  |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] EXIT                                          |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n-> Redirecting to Booking Menu...");
                    // Instantiation of the BookingMenu to use display()
                    BookingMenu bookingMenu = new BookingMenu(scanner);
                    bookingMenu.display();
                    break;
                case "2":
                    System.out.println("\n-> Redirecting to Staff Login...");
                    // Instantiation of the StaffLoginMenu to use display()
                    StaffLoginMenu loginMenu = new StaffLoginMenu(scanner);
                    loginMenu.display();
                    break;
                case "0":
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Thank you for using our laundry service!           |");
                    System.out.println("+----------------------------------------------------+\n");
                    isRunning = false;
                    break;
                default:
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Invalid option. Please try again.                  |");
                    System.out.println("+----------------------------------------------------+\n");
            }
        }

    }
}
