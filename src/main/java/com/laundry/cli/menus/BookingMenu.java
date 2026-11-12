package com.laundry.cli.menus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.CustomerDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.dao.PaymentDAO;
import com.laundry.helper.AppointmentService;
import com.laundry.helper.CustomerService;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.DigitalPayment;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.systemclass.Payment;
import com.laundry.userclass.Customer;

/*
    User interface menu for Laundry Appointments
    Handles appointment creation, customer registration, viewing appointment bills, 
    digital pre-payments, and appointment cancellation
*/
public class BookingMenu {

    // Inputs and shared Scanner instance
    private Scanner scanner;

    // Database access objects (DAOs) for database CRUD queries 
    private CustomerDAO customerDAO = new CustomerDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    // Helper classes instantiation
    private CustomerService customerService = new CustomerService();
    private AppointmentService appointmentService = new AppointmentService();

    // Constructor initializing the Scanner instance for console input
    public BookingMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    // Entry point, display loop showing UI for the Customer Booking Menu
    public void display() {
        boolean inBookingMenu = true;

        while (inBookingMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("|      QUICK RINSE | LAUNDRY APPOINTMENT SYSTEM      |");
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

    // Option [1]: Registers a new customer selects appointment type 
    // (Walk-In or Delivery), attaches laundry orders, and displays a summary 
    private void createAppointment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                ENTER PERSONAL DETAILS              |");
        System.out.println("+----------------------------------------------------+");

        // Input Validations for Entering Personal Details
        String fName = "";
        while (true) {
            System.out.print("| Enter First Name: ");
            fName = scanner.nextLine().trim();
            if (fName.isEmpty()) {
                System.out.println("| ERROR: First Name cannot be empty.                 |");
            } else {
                break;
            }
        }
        String lName = "";
        while (true) {
            System.out.print("| Enter Last Name: ");
            lName = scanner.nextLine().trim();
            if (lName.isEmpty()) {
                System.out.println("| ERROR: Last Name cannot be empty.                  |");
            } else if (!lName.matches("[a-zA-Z\\s\\.-]+")) {
                System.out.println("| ERROR: Last Name must contain letters only.       |");
            } else {
                break;
            }
        }
        String phoneNum = "";
        while (true) {
            System.out.print("| Enter Phone Number (11 digits): ");
            phoneNum = scanner.nextLine().trim();
            if (phoneNum.isEmpty()) {
                System.out.println("| ERROR: Phone Number cannot be empty.              |");
            } else if (!phoneNum.matches("\\d{11}")) {
                System.out.println("| ERROR: Phone Number must not contain letters and must be exactly 11 digits.    |");
            } else {
                break;
            }
        }
        String brgy = "";
        while (true) {
            System.out.print("| Enter Barangay: ");
            brgy = scanner.nextLine().trim();
            if (brgy.isEmpty()) {
                System.out.println("| ERROR: Barangay cannot be empty.                   |");
            } else {
                break;
            }
        }
        String street = "";
        while (true) {
            System.out.print("| Enter Street: ");
            street = scanner.nextLine().trim();
            if (street.isEmpty()) {
                System.out.println("| ERROR: Street cannot be empty.                     |");
            } else {
                break;
            }
        }
        String blockNo = "";
        while (true) {
            System.out.print("| Enter Block No: ");
            blockNo = scanner.nextLine().trim();
            if (blockNo.isEmpty()) {
                System.out.println("| ERROR: Block No cannot be empty.                   |");
            } else {
                break;
            }
        }

        // Generate a random unique reference number and save customer to database
        // Will be used later by both Staff and Customer
        String refNum = "REF-" + (System.currentTimeMillis() % 100000);
        Customer customer = new Customer(fName, lName, refNum, phoneNum, brgy, street, blockNo);

        try {
            if (!customerService.registerCustomer(customer)) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| ERROR: Could not register customer in DB.          |");
                System.out.println("+----------------------------------------------------+");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: " + e.getMessage());
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Customer registration feedback
        System.out.println("+------------------------------------------------------+");
        System.out.println("|               -> DETAILS REGISTERED!                 |");
        System.out.println("+------------------------------------------------------+");
        System.out.println("+------------------------------------------------------+");
        System.out.println("| [!] PLEASE SAVE THIS REFERENCE NUMBER FOR FUTURE USE |");
        System.out.println("+------------------------------------------------------+");
        System.out.println("| Reference Number: " + refNum);
        System.out.println("+------------------------------------------------------+");
        
        // Appointment Type Selection Loop
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|              SELECT APPOINTMENT TYPE               |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("| [1] Pickup                                         |");
        System.out.println("| [2] Delivery                                       |");
        System.out.println("+----------------------------------------------------+");
        String appointmentType = "";
        while (true) {
            System.out.print("SELECT TYPE: ");
            String typeChoice = scanner.nextLine().trim();
            if ("1".equals(typeChoice)) {
                appointmentType = "Pickup";
                break;
            } else if ("2".equals(typeChoice)) {
                appointmentType = "Delivery";
                break;
            } else {
                System.out.println("| ERROR: Enter 1 for Pickup or 2 for Delivery.      |");
            }
        }

        // Save initial appointment record in database
        int appointmentId = new Random().nextInt(900000) + 100000;

        // Instantiate an Appointment object to be saved in the database
        Appointment appointment = new Appointment(appointmentId, refNum, appointmentType, LocalDate.now());

        if (!appointmentDAO.createAppointment(appointment)) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Could not create appointment.               |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Initial Laundry Add loop
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 ADD LAUNDRY ORDERS                 |");
        boolean addingOrders = true;
        int orderCounter = 1;

        // List For Order Summary Later
        List<String> orderSummaries = new ArrayList<>();
        
        // Order Service Selection Loop
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
                case "1":
                    serviceName = "Wash";
                    break;
                case "2":
                    serviceName = "Dry";
                    break;
                case "3":
                    serviceName = "Wash and Dry";
                    break;
                default:
                    System.out.println("+----------------------------------------------------+");
                    System.out.println("| ERROR: Invalid choice. Select 1, 2, 3, or 0.       |");
                    System.out.println("+----------------------------------------------------+");
                    continue;
            }

            // Save LaundryOrder record to database
            int orderId = new Random().nextInt(900000) + 100000;

            // Instantiate an Order object to be saved in the database
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

        // APPOINTMENT AND ORDERS SUMMARY
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

        // Loop through the tracked orders linked to the appointment
        for (int i = 0; i < orderSummaries.size(); i++) {
            System.out.println(String.format("| %-50s |", "Order #" + (i + 1) + "         : " + orderSummaries.get(i)));
        }

        System.out.println("+----------------------------------------------------+\n");

        // Feedback
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

    } // end [1] : createAppointment

    // Option [2]: Fetches full appointment details by reference number, lists orders, 
    // and provides an option to generate an E-Payment QR code for digital pre-payment
    private void viewAppointment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                  VIEW APPOINTMENT                  |");
        System.out.println("+----------------------------------------------------+");

        // Refnum enter validation loop
        String refNum = "";
        while (true) {
            System.out.print("| Enter Reference Number: ");
            refNum = scanner.nextLine().trim();
            if (refNum.isEmpty()) {
                System.out.println("| ERROR: Reference Number cannot be empty.          |");
            } else {
                break;
            }
        }

        // Fetch full appointment details
        // only one appointment at a time
        Appointment appt = appointmentService.getFullAppointmentDetails(refNum.toUpperCase());

        if (appt == null) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| ERROR: No appointment found for: " + refNum);
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Display the summary of the apponitment and its corresponding orders details
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
            System.out.println("| Est. Duration      : 0 minutes");
        }

        System.out.println("| ");
        System.out.println("| Total Price        : PHP " + appt.getAppointmentTotalPrice());
        System.out.println("| Payment Status     : " + (appt.isPaid() ? "Paid" : "Pending"));

        System.out.println("+----------------------------------------------------+");
        System.out.println("| ORDERS SUMMARY:                                    |");
        System.out.println("+----------------------------------------------------+");

        List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
        if (orders.isEmpty()) {
            System.out.println("|   No orders created by this appointment.          |");
        } else {
            for (int i = 0; i < orders.size(); i++) {
                LaundryOrder order = orders.get(i);
                String weightStr = order.getLaundryTotalKg() > 0 ? String.format("%.1f kg", order.getLaundryTotalKg()) : "Pending weigh-in";
                System.out.println(String.format("|   %d. %-13s | Weight: %-21s |",
                        (i + 1), order.getLaundryService(), weightStr));
            }
        }
        System.out.println("+----------------------------------------------------+");

        // Digital pre-payment prompt if appointment is weighed and unpaid
        // This will only show after an appointment is weighed
        // Customer need to viewAppointment first to reach this
        if (!appt.isPaid() && appt.getAppointmentTotalPrice() > 0) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| [1] Generate Digital Payment QR                    |");
            System.out.println("| [0] Back to Menu                                   |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");
            String payChoice = scanner.nextLine();

            // Instantiate a digital payment to be saved to the database
            if ("1".equals(payChoice)) {
                int paymentId = new Random().nextInt(900000) + 100000;
                DigitalPayment digitalPayment = new DigitalPayment(paymentId, appt.getAppointmentId(), appt.getAppointmentTotalPrice());
                digitalPayment.generateQr();

                // Confirmation logic
                System.out.println("+-------------------------+");
                System.out.println("| Confirm Digital Payment |");

                String confirm;

                while (true) {
                    System.out.print("| [1] Yes  [0] No: ");
                    confirm = scanner.nextLine().trim();
                    
                    if (confirm.equals("1") || confirm.equals("0")) {
                        break;
                    }
                    System.out.println("| Invalid input! Please enter 1 or 0.");
                }

                // Save the payment record to the database
                // Show summary
                if ("1".equals(confirm)) {
                    digitalPayment.setAmountPaid(appt.getAppointmentTotalPrice());
                    digitalPayment.markPaid();

                    if (paymentDAO.createPayment(digitalPayment, "E-Payment")) {
                        appt.setPayment(digitalPayment);
                        System.out.println("\n+-----------------------------------------------+");
                        System.out.println("|               PAYMENT SUCCESSFULL             |");
                        System.out.println("+-----------------------------------------------+");
                        System.out.println(String.format("| Payment ID        : %-25d |", paymentId));
                        System.out.println(String.format("| Customer Ref No.  : %-25s |", appt.getReferenceNumber()));
                        System.out.println(String.format("| Amount Paid       : PHP %-21.2f |", appt.getAppointmentTotalPrice()));
                        System.out.println(String.format("| Payment           : %-25s |", "Paid (E-Payment)"));
                        System.out.println("+-----------------------------------------------+");
                        System.out.println("|   Please keep this receipt for your records   |");
                        System.out.println("+-----------------------------------------------+");
                    } else {
                        System.out.println("\n+-----------------------------------------------+");
                        System.out.println("| ERROR: Failed to record payment in database.  |");
                        System.out.println("+-----------------------------------------------+");
                    }
                } else {
                    System.out.println("\n+-----------------------------------------------+");
                    System.out.println("| Pre-payment cancelled.                             |");
                    System.out.println("+-----------------------------------------------+");
                }
            }
        }
    } // end [2] : viewAppointment

    // Option [3]: Cancels an appointment if its status is 'Pending'
    // Cannot proceed if appointment is already in progress
    private void cancelAppointment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 CANCEL APPOINTMENT                 |");
        System.out.println("+----------------------------------------------------+");

        // validation loop
        String refNum = "";
        while (true) {
            System.out.print("| Enter Reference Number: ");
            refNum = scanner.nextLine().trim();
            if (refNum.isEmpty()) {
                System.out.println("| ERROR: Reference Number cannot be empty.          |");
            } else {
                break;
            }
        }
        
        // Fetch full appointment details
        // can only cancel one appointment at a time
        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);

        if (appt == null) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| ERROR: No appointment found.                       |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Only Pending appointments can be cancelled
        if (appt.canBeCancelled()) {
            System.out.print("| Are you sure you want to cancel? (Y/N): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y")) {
                appt.setAppointmentStatus("Cancelled");
                if (appointmentDAO.updateAppointment(appt)) {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| -> Appointment successfully cancelled.             |");
                } else {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| ERROR: Could not cancel appointment in database.   |");
                }
            }
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Cannot cancel. Status is: " + appt.getAppointmentStatus());
        }
        System.out.println("+----------------------------------------------------+");
    }
} // end [3] : cancelAppointment
