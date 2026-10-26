package com.laundry.cli.menus;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.CustomerDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.dao.MachineDAO;
import com.laundry.dao.MachineUsageDAO;
import com.laundry.dao.PaymentDAO;
import com.laundry.helper.AppointmentService;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.Cash;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.systemclass.MachineUsage;
import com.laundry.systemclass.Payment;
import com.laundry.userclass.Customer;
import com.laundry.userclass.Staff;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
    User interface menu for the Delivery Rider role.
    Handles viewing delivery orders, updating status to 'Out for Delivery', and processing cash payments on delivery.
*/
public class DeliveryRiderMenu {

    // Inputs and logged-in user session
    private Scanner scanner;
    private Staff loggedInRider;

    // Database access objects (DAOs) for database CRUD queries
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private MachineDAO machineDAO = new MachineDAO();
    private MachineUsageDAO machineUsageDAO = new MachineUsageDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    // Helper classes instantiation
    private AppointmentService appointmentService = new AppointmentService();

    // Constructor initializing the Scanner and the currently logged-in Attendant
    // Used in StaffLoginMenu.java
    public DeliveryRiderMenu(Scanner scanner, Staff loggedInRider) {
        this.scanner = scanner;
        this.loggedInRider = loggedInRider;
    }

    // Entry point, display loop showing UI for the Delivery Rider
    public void display() {
        boolean inRiderMenu = true;

        while (inRiderMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println(String.format("| %-50s |", "DELIVERY RIDER MENU - Welcome, " + loggedInRider.getFullName()));
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] VIEW DELIVERY APPOINTMENTS                    |");
            System.out.println("|  [2] UPDATE STATUS                                 |");
            System.out.println("|  [3] PROCESS CASH PAYMENT                          |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] LOGOUT                                        |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewDeliveryAppointments();
                    break;
                case "2":
                    updateDeliveryStatus();
                    break;
                case "3":
                    processCashPayment();
                    break;
                case "0":
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Logging out... Returning to Main Menu.             |");
                    System.out.println("+----------------------------------------------------+");
                    inRiderMenu = false;
                    break;
                default:
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Invalid option. Please try again.                  |");
                    System.out.println("+----------------------------------------------------+");
            }
        }
    }

    // Option [1]: Displays all delivery appointments along with customer contact and address details
    private void viewDeliveryAppointments() {
        System.out.println("\n+--------------------------------------------------------------+");
        System.out.println("|                 DELIVERY APPOINTMENTS LIST                   |");
        System.out.println("+--------------------------------------------------------------+");

        // Fetch all delivery type appointments from database
        List<Appointment> deliveryList = appointmentDAO.getDeliveryAppointment();

        if (deliveryList.isEmpty()) {
            System.out.println("\n+--------------------------------------------------------------+");
            System.out.println("| No delivery appointments found.                              |");
            System.out.println("+--------------------------------------------------------------+");
            return;
        }

        // Loop through delivery appointments and display details with customer contact info
        for (Appointment appt : deliveryList) {
            Customer customer = customerDAO.getCustomerDetails(appt.getReferenceNumber());

            String name = (customer != null) ? customer.getFullName() : "N/A";
            String phone = (customer != null) ? customer.getPhoneNumber() : "N/A";
            String barangay = (customer != null) ? customer.getBarangay() : "N/A";
            String street = (customer != null) ? customer.getStreet() : "N/A";
            String blkNo = (customer != null) ? customer.getBlockNo() : "N/A";
            String fullAddress = barangay + ", " + street + ", " + blkNo;

            String payStatus = appt.isPaid() ? "Paid" : "Pending";
            String priceStr = String.format("PHP%.2f", appt.getAppointmentTotalPrice());

            System.out.println("+--------------------------------------------------------------+");
            System.out.println(String.format("| %-6s | %-9s | %-16s | %-10s | %-7s |", "ID", "Ref Num", "Status", "Price", "Payment"));
            System.out.println("+--------------------------------------------------------------+");
            System.out.println(String.format("| %-6d | %-9s | %-16s | %-10s | %-7s |",
                    appt.getAppointmentId(), appt.getReferenceNumber(), appt.getAppointmentStatus(), priceStr, payStatus));

            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| CONTACT DETAILS:                                             |");
            System.out.println("+--------------------------------------------------------------+");
            System.out.println(String.format("| Name    : %-50s |", name));
            System.out.println(String.format("| Phone   : %-50s |", phone));
            System.out.println(String.format("| Address : %-50s |", fullAddress));
            System.out.println("+--------------------------------------------------------------+\n");
        }
    } // end of [1] : viewDeliveryAppointments

    // Option [2]: Updates delivery appointment status to 'Out for Delivery' or 'Completed'
    // 'Out for Delivery': releases assigned machines back to 'Available' and clears the ECF timer
    // 'Completed': only allowed once the appointment is already 'Out for Delivery'
    private void updateDeliveryStatus() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|       UPDATE STATUS : DELIVERY APPOINTMENT         |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine().trim();

        // Retrieve full appointment record of refnum
        // Only one appointment
        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);

        if (appt == null || !"Delivery".equalsIgnoreCase(appt.getAppointmentType())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Valid delivery appointment not found.       |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        if ("Pending".equalsIgnoreCase(appt.getAppointmentStatus())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Appointment is still Pending.                      |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Already finished, nothing left to update
        if ("Completed".equalsIgnoreCase(appt.getAppointmentStatus())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Appointment is already Completed.                  |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Choose the new status
        System.out.println("\n+----------------------------------------------------+");
        System.out.println(String.format("| Reference Number : %-31s |", appt.getReferenceNumber()));
        System.out.println(String.format("| Current Status   : %-31s |", appt.getAppointmentStatus()));
        System.out.println("+----------------------------------------------------+");
        System.out.println("| Set status to:                                     |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("|  [1] Out for Delivery                              |");
        System.out.println("|  [2] Completed                                     |");
        System.out.println("|  [0] Cancel                                        |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("SELECT OPTION: ");
        String choice = scanner.nextLine().trim();

        String newStatus;
        if ("1".equals(choice)) {
            newStatus = "Out for Delivery";
        } else if ("2".equals(choice)) {
            newStatus = "Completed";
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Operation cancelled. Returning to menu.            |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Completed can only follow Out for Delivery
        if ("Completed".equals(newStatus) && !"Out for Delivery".equalsIgnoreCase(appt.getAppointmentStatus())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Must be 'Out for Delivery' before Completed.|");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Already Out for Delivery, machines were released earlier
        if ("Out for Delivery".equals(newStatus) && "Out for Delivery".equalsIgnoreCase(appt.getAppointmentStatus())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Appointment is already Out for Delivery.           |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Double Confirmation
        System.out.println("\n+----------------------------------------------------+");
        System.out.println(String.format("| Confirm status change to '%s'?", newStatus));
        System.out.println("+----------------------------------------------------+");
        System.out.println("|  [1] Yes                                           |");
        System.out.println("|  [0] Cancel                                        |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("SELECT OPTION: ");
        String confirm = scanner.nextLine().trim();

        if ("1".equals(confirm)) {
            appt.setAppointmentStatus(newStatus);
            appt.setStaffId(loggedInRider.getStaffId());

            boolean machinesReleased = false;

            if ("Out for Delivery".equals(newStatus)) {
                // Release assigned machines back to 'Available', delete usage records, and reset ECF timer
                List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());

                for (LaundryOrder order : orders) {
                    List<MachineUsage> usages = machineUsageDAO.getUsageByOrder(order.getOrderId());
                    for (MachineUsage usage : usages) {
                        machineDAO.updateMachineStatus(usage.getMachineId(), "Available");
                    }
                    machineUsageDAO.deleteUsageByOrder(order.getOrderId());
                }
                appt.setECF(0);
                machinesReleased = true;
            }

            // Save status update to database
            if (appointmentDAO.updateAppointment(appt)) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("|            STATUS UPDATED SUCCESSFULLY!            |");
                System.out.println("+----------------------------------------------------+");
                System.out.println(String.format("| New Status : %-37s |", newStatus));
                System.out.println(String.format("| Updated By : %-37s |", loggedInRider.getFullName()));
                if (machinesReleased) {
                    System.out.println(String.format("| Machines   : %-37s |", "Released to 'Available'"));
                }
                System.out.println("+----------------------------------------------------+");
            } else {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| ERROR: Failed to update status in database.        |");
                System.out.println("+----------------------------------------------------+");
            }
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Operation cancelled. Returning to menu.            |");
            System.out.println("+----------------------------------------------------+");
        }
    } // end [2] : updateDeliveryStatus

    // Option [3]: Collects cash payment upon delivery, computes change, 
    // and automatically updates appointment status to 'Completed'
    // If customer wants to pay using QRCode, pay in the BookingMenu(viewAppointment - Pay Digital)
    private void processCashPayment() {

        
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 PROCESS CASH PAYMENT               |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine().trim();

        // Retrieve appointment details from database using reference number
        // Only one
        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);

        
        // Appointment - Paymetn valdiation
        if (appt == null) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Appointment not found.                      |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        if("In Progress".equalsIgnoreCase(appt.getAppointmentStatus()) || "Pending".equalsIgnoreCase(appt.getAppointmentStatus())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Cannot Process Payment yet.                        |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        if (appt.isPaid()) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Appointment is ALREADY PAID.                       |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        double totalAmount = appt.getAppointmentTotalPrice();
        if (totalAmount <= 0) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Total price is PHP 0.00. Weigh order first.  |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        System.out.println(String.format("| Total Amount Due: PHP %-28.2f |", totalAmount));

        boolean validAmount = false;
        double amountPaid = 0.0;

        // Input loop to ensure valid cash amount is entered
        while (!validAmount) {
            System.out.print("| Enter Amount Paid: ");
            String input = scanner.nextLine().trim();

            try {
                amountPaid = Double.parseDouble(input);
                if (amountPaid < totalAmount) {
                    System.out.println("| ERROR: Amount Paid is less than total due.    |");
                } else {
                    validAmount = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("| ERROR: Enter a valid numeric amount.              |");
            }
        }

        // Compute change and create Cash payment instance
        double change = amountPaid - totalAmount;
        int paymentId = new Random().nextInt(900000) + 100000;
        Payment payment = new Cash(paymentId, appt.getAppointmentId(), totalAmount);
        payment.setAmountPaid(amountPaid);
        payment.markPaid();

        // Save payment to database and update appointment status to Completed
        if (paymentDAO.createPayment(payment, "Cash")) {
            appt.setPayment(payment);
            appt.setAppointmentStatus("Completed");
            appt.setStaffId(loggedInRider.getStaffId());
            appointmentDAO.updateAppointment(appt);

            System.out.println("\n+----------------------------------------------------+");
            System.out.println("|             PAYMENT RECORDED SUCCESSFULLY          |");
            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| Amount Due      : PHP %.2f", totalAmount));
            System.out.println(String.format("| Amount Paid     : PHP %.2f", amountPaid));
            System.out.println(String.format("| Change          : PHP %.2f", change));
            System.out.println(String.format("| New Status      : %s", "Completed"));
            System.out.println("+----------------------------------------------------+");
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Could not record payment in database.       |");
            System.out.println("+----------------------------------------------------+");
        }
    } // end [3] : processCashPayment
}
