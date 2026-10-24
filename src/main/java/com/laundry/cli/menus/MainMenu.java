package com.laundry.cli.menus;

import java.util.Scanner;

public class MainMenu {
    private Scanner scanner;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("|            LAUNDRY APPOINTMENT SYSTEM              |");
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
                    BookingMenu bookingMenu = new BookingMenu(scanner);
                    bookingMenu.display();
                    break;
                case "2":
                    System.out.println("\n-> Redirecting to Staff Login...");
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
                    System.out.println("Invalid option. Please try again.\n");
            }
        }
    }
}