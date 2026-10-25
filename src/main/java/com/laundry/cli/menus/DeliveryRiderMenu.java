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

public class DeliveryRiderMenu {

    private Scanner scanner;
    private Staff loggedInRider;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private MachineDAO machineDAO = new MachineDAO();
    private MachineUsageDAO machineUsageDAO = new MachineUsageDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    private AppointmentService appointmentService = new AppointmentService();

    public DeliveryRiderMenu(Scanner scanner, Staff loggedInRider) {
        this.scanner = scanner;
        this.loggedInRider = loggedInRider;
    }

    public void display() {
        boolean inRiderMenu = true;

        while (inRiderMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println(String.format("| %-50s |", "DELIVERY RIDER MENU - Welcome, " + loggedInRider.getFullName()));
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] VIEW DELIVERY APPOINTMENTS                    |");
            System.out.println("|  [2] UPDATE STATUS : OUT FOR DELIVERY              |");
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
                    updateOutForDeliveryStatus();
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

    private void viewDeliveryAppointments() {
        System.out.println("\n+--------------------------------------------------------------+");
        System.out.println("|                 DELIVERY APPOINTMENTS LIST                   |");
        System.out.println("+--------------------------------------------------------------+");

        List<Appointment> deliveryList = appointmentDAO.getDeliveryAppointment();

        if (deliveryList.isEmpty()) {
            System.out.println("\n+--------------------------------------------------------------+");
            System.out.println("| No delivery appointments found.                              |");
            System.out.println("+--------------------------------------------------------------+");
            return;
        }

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
    }

    private void updateOutForDeliveryStatus() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|       UPDATE STATUS : OUT FOR DELIVERY             |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine().trim();

        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);

        if (appt == null || !"Delivery".equalsIgnoreCase(appt.getAppointmentType())) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Valid delivery appointment not found.       |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        System.out.println("\n+----------------------------------------------------+");
        System.out.println(String.format("| Reference Number : %-31s |", appt.getReferenceNumber()));
        System.out.println(String.format("| Current Status   : %-31s |", appt.getAppointmentStatus()));
        System.out.println("+----------------------------------------------------+");
        System.out.println("| Set status to 'Out for Delivery'?                  |");
        System.out.println("|  [1] Yes                                           |");
        System.out.println("|  [0] Cancel                                        |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("SELECT OPTION: ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            appt.setAppointmentStatus("Out for Delivery");
            appt.setStaffId(loggedInRider.getStaffId());

            // Release assigned machines back to 'Available' & clear ECF timer
            List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
            
            for (LaundryOrder order : orders) {
                List<MachineUsage> usages = machineUsageDAO.getUsageByOrder(order.getOrderId());
                for (MachineUsage usage : usages) {
                    machineDAO.updateMachineStatus(usage.getMachineId(), "Available");
                }
                machineUsageDAO.deleteUsageByOrder(order.getOrderId()); // Resets AssignedTo to 'None'
            }
            appt.setECF(0);

            if (appointmentDAO.updateAppointment(appt)) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("|            STATUS UPDATED SUCCESSFULLY!            |");
                System.out.println("+----------------------------------------------------+");
                System.out.println(String.format("| New Status : %-37s |", "Out for Delivery"));
                System.out.println(String.format("| Updated By : %-37s |", loggedInRider.getFullName()));
                System.out.println(String.format("| Machines   : %-37s |", "Released to 'Available'"));
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
    }

    private void processCashPayment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 PROCESS CASH PAYMENT               |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine().trim();

        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);

        if (appt == null) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Appointment not found.                      |");
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

        double change = amountPaid - totalAmount;
        int paymentId = new Random().nextInt(900000) + 100000;
        Payment payment = new Cash(paymentId, appt.getAppointmentId(), totalAmount);
        payment.setAmountPaid(amountPaid);
        payment.markPaid();

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
    }
}
