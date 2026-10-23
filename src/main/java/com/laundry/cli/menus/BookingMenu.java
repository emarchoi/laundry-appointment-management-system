package com.laundry.cli.menus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.CustomerDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.helper.AppointmentService;
import com.laundry.helper.PaymentService;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.userclass.Customer;

public class BookingMenu {
    private Scanner scanner;
    private CustomerDAO customerDAO = new CustomerDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    
    private AppointmentService appointmentService = new AppointmentService();
    private PaymentService paymentService = new PaymentService();

    public BookingMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    // DISPLAY MENU
    public void display() {
        boolean inBookingMenu = true;

        while (inBookingMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("|            LAUNDRY APPOINTMENT SYSTEM              |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] CREATE APPOINTMENT                            |");
            System.out.println("|  [2] VIEW APPOINTMENT                              |");
            System.out.println("|  [3] CANCEL APPOINTMENT                            |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] EXIT TO MAIN MENU                             |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createAppointment();
                    break;
                case "2":
                    viewAppointment();
                    break;
                case "3":
                    cancelAppointment();
                    break;
                case "0":
                    inBookingMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // CREATE APPOINTMENT
    private void createAppointment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                ENTER PERSONAL DETAILS              |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| First Name: ");
        String fName = scanner.nextLine();
        System.out.print("| Last Name: ");
        String lName = scanner.nextLine();
        System.out.print("| Phone Number (11 digits): ");
        String phone = scanner.nextLine();
        System.out.print("| Barangay: ");
        String brgy = scanner.nextLine();
        System.out.print("| Street: ");
        String street = scanner.nextLine();
        System.out.print("| Block No: ");
        String block = scanner.nextLine();

        String refNum = "REF-" + (System.currentTimeMillis() % 100000);
        Customer customer = new Customer(fName, lName, refNum, phone, brgy, street, block);

        if (!customerDAO.addCustomer(customer)) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Could not register customer.                |");
            System.out.println("+----------------------------------------------------+");
            return;
        }
        System.out.println("+------------------------------------------------------+");
        System.out.println("|               -> DETAILS REGISTERED!                 |");
        System.out.println("+------------------------------------------------------+");
        System.out.println("+------------------------------------------------------+");
        System.out.println("| [!] PLEASE SAVE THIS REFERENCE NUMBER FOR FUTURE USE |");
        System.out.println("+------------------------------------------------------+");
        System.out.println("| Reference Number: " + refNum);
        System.out.println("+------------------------------------------------------+");

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|              SELECT APPOINTMENT TYPE               |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("| [1] Walk-In                                        |");
        System.out.println("| [2] Delivery                                       |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("SELECT TYPE: ");
        String typeChoice = scanner.nextLine();
        String appointmentType = typeChoice.equals("2") ? "Delivery" : "Walk-In";

        int appointmentId = new Random().nextInt(900000) + 100000;
        Appointment appointment = new Appointment(appointmentId, refNum, appointmentType, LocalDate.now());

        if (!appointmentDAO.createAppointment(appointment)) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Could not create appointment.               |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 ADD LAUNDRY ORDERS                 |");
        boolean addingOrders = true;
        int orderCounter = 1;

        // List For Order Summary Later
        List<String> orderSummaries = new ArrayList<>();
        while (addingOrders) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| Select Service for Order #" + orderCounter + ":");
            System.out.println("+----------------------------------------------------+");
            System.out.println("| [1] Wash                                           |");
            System.out.println("| [2] Dry                                            |");
            System.out.println("| [3] Wash and Dry                                   |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("| [0] Finish Adding Orders                           |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT SERVICE: ");
            String serviceChoice = scanner.nextLine();

            if (serviceChoice.equals("0")) {
                if (orderCounter == 1) {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| You must add at least one order!                   |");
                    System.out.println("+----------------------------------------------------+");
                    continue;
                }
                addingOrders = false;
                break;
            }

            String serviceName;
            switch (serviceChoice) {
                case "1": serviceName = "Wash"; break;
                case "2": serviceName = "Dry"; break;
                case "3": serviceName = "Wash and Dry"; break;
                default: 
                System.out.println("+----------------------------------------------------+");
                    System.out.println("| Invalid service. Try again.                        |");
                    System.out.println("+----------------------------------------------------+"); 
                    continue;
            }

            int orderId = new Random().nextInt(900000) + 100000;
            LaundryOrder order = new LaundryOrder(orderId, appointmentId, serviceName);
            
            if (orderDAO.addOrder(order)) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| -> " + serviceName + " order added to appointment.");
                System.out.println("+----------------------------------------------------+\n");
                orderSummaries.add(serviceName);
                orderCounter++;
            } else {
                System.out.println("+----------------------------------------------------+");
                System.out.println("| ERROR: Could not save order.                       |");
                System.out.println("+----------------------------------------------------+");
            }
        }

        // ADD APPOINTMENT AND ORDER SUMMARY
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|         APPOINTMENT CREATED SUCCESSFULLY!          |");
        System.out.println("+----------------------------------------------------+");

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                APPOINTMENT SUMMARY                 |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("| Reference Number : " + refNum);
        System.out.println("| Appointment ID   : " + appointmentId);
        System.out.println("| Type             : " + appointmentType);
        System.out.println("| Status           : Pending (Awaiting Attendant)    |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("+----------------------------------------------------+");
        System.out.println("|                   ORDER SUMMARY                    |");
        System.out.println("+----------------------------------------------------+");
        
        // Loop through the tracked orders and print them perfectly aligned
        for (int i = 0; i < orderSummaries.size(); i++) {
            System.out.println(String.format("| %-50s |", "Order #" + (i + 1) + "         : " + orderSummaries.get(i)));
        }
        
        System.out.println("+----------------------------------------------------+\n");

        if (appointmentType.equalsIgnoreCase("Walk-In")) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| Please bring your laundry to the shop              |");
            System.out.println("| at your convenience. Thank you!                    |");
            System.out.println("+----------------------------------------------------+");
        } else if (appointmentType.equalsIgnoreCase("Delivery")) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| Please wait while our delivery rider arrives       |");
            System.out.println("| to collect your laundry. Thank you!                |");
            System.out.println("+----------------------------------------------------+");
        }
        
    }

    // VIEW APPOINTMENT
    private void viewAppointment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                  VIEW APPOINTMENT                  |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine();

        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);
        
        if (appt == null) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| ERROR: No appointment found for: " + refNum);
            System.out.println("+----------------------------------------------------+");
            return;
        }

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                APPOINTMENT DETAILS                 |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("| Reference Number   : " + appt.getReferenceNumber());
        System.out.println("| Appointment ID     : " + appt.getAppointmentId());
        System.out.println("| Appointment Date   : " + appt.getAppointmentDate());
        System.out.println("| ");
        System.out.println("| Type               : " + appt.getAppointmentType());
        System.out.println("| Status             : " + appt.getAppointmentStatus());

        if (appt.getECF() > 0) {
            System.out.println("| Est. Duration      : " + appt.getECF() + " minutes");
        } else {
            System.out.println("| Est. Duration      : Pending Assignment");
        }

        System.out.println("| ");
        System.out.println("| Total Price        : PHP " + appt.getAppointmentTotalPrice());
        System.out.println("| Payment Status     : " + (appt.isPaid() ? "" : "Pending"));
        System.out.println("+----------------------------------------------------+");

        if (!appt.isPaid() && appt.getAppointmentTotalPrice() > 0) {
            System.out.println("| [1] Generate Digital Payment QR                    |");
            System.out.println("| [0] Back to Menu                                   |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");
            String payChoice = scanner.nextLine();

            if (payChoice.equals("1")) {
                System.out.println("\n" + paymentService.generateDigitalPaymentQR(appt));
                System.out.println("-> Please show this QR code to the attendant.");
            }
        }
    }

    private void cancelAppointment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 CANCEL APPOINTMENT                 |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine();

        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);
        
        if (appt == null) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| ERROR: No appointment found.                       |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        if (appt.canBeCancelled()) {
            System.out.print("| Are you sure you want to cancel? (Y/N): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("Y")) {
                appt.setAppointmentStatus("Cancelled");
                if (appointmentDAO.updateAppointment(appt)) {
                    System.out.println("+----------------------------------------------------+");
                    System.out.println("| -> Appointment successfully cancelled.             |");
                } else {
                    System.out.println("+----------------------------------------------------+");
                    System.out.println("| ERROR: Could not cancel appointment in database.   |");
                }
            }
        } else {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| ERROR: Cannot cancel. Status is: " + appt.getAppointmentStatus());
        }
        System.out.println("+----------------------------------------------------+");
    }
}