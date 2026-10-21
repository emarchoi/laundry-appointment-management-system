package com.laundry.cli.menus;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.LaundryOrderDAO;

import com.laundry.util.AppointmentService;
import com.laundry.util.MachineService;

import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.LaundryOrder;

import com.laundry.userclass.Staff;

import java.util.Scanner;
import java.util.List;

public class AttendantMenu {

    private Scanner scanner;
    private Staff loggedInStaff;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private AppointmentService appointmentService = new AppointmentService();
    private MachineService machineService = new MachineService();

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
                    viewPendingAppointments();
                    break;
                // case "2": viewAllAppointments();
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
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Invalid option. Please try again.                  |");
                    System.out.println("+----------------------------------------------------+");
            }
        }
    }

    private void viewPendingAppointments() {
        System.out.println("+---------------------------------------------------------+");
        System.out.println("|                PENDING APPOINTMENTS LIST                |");
        System.out.println("+---------------------------------------------------------+");

        List<Appointment> pendingList = appointmentDAO.getAppointmentByStatus("Pending");

        if (pendingList.isEmpty()) {
            System.out.println("| No pending appointments found.                      |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        for (Appointment appt : pendingList) {
            // Fetch the order count from LaundryOrderDAO
            int orderCount = orderDAO.getOrdersByAppointment(appt.getAppointmentId()).size();
        
            System.out.println(String.format("| Appt ID: %-3d | Ref: %-6s | %-7s | Orders: %-2d ", 
                appt.getAppointmentId(), 
                appt.getReferenceNumber(), 
                appt.getAppointmentType(), 
                orderCount));
        }
        System.out.println("+---------------------------------------------------------+");
        System.out.print("Enter Reference Number to Process (or 0 to exit): ");
        String refNum = scanner.nextLine();

        if (refNum.equals("0")) return;

        // Full Appointment Record
        Appointment selectedAppt = appointmentService.getFullAppointmentDetails(refNum);
        if (selectedAppt == null || !selectedAppt.canBeCancelled()) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Invalid or non-pending appointment reference.|");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                 WEIGHING & ASSIGNMENT              |");

        for (LaundryOrder order : selectedAppt.getOrders()) {
            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| Processing Order #%-5d", order.getOrderId()));
            System.out.println(String.format("| Service: %s", order.getLaundryService()));
            System.out.println("+----------------------------------------------------+");
            System.out.print("| Enter Weight in Kg: ");
            double weight = Double.parseDouble(scanner.nextLine());
            order.setLaundryTotalKg(weight);

            // Apply CEILING(Weight / 7)
            int machinesNeeded = machineService.calculateMachinesRequired(weight);
            order.setMachinesUsed(machinesNeeded);

            // Determine Base Price based on Service
            double basePrice = order.getLaundryService().equalsIgnoreCase("Dry") ? 65.0 : 50.0;
            if (order.getLaundryService().equalsIgnoreCase("Wash and Dry")) {
                basePrice = 115.0; // 50 (Wash) + 65 (Dry)
            }
            
            // Compute Order Price
            double orderPrice = machinesNeeded * basePrice;
            order.setOrderTotalPrice(orderPrice);

            // Update LaundryOrder table
            orderDAO.updateOrderDetails(order);

            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| -> Machines Needed : %-29d |", machinesNeeded));
            System.out.println(String.format("| -> Order Subtotal  : PHP %-25.2f |", orderPrice));
            System.out.println("+----------------------------------------------------+\n");
        }

        // Update Appointment status and calculated total price
        selectedAppt.computeAppointmentTotalPrice();
        selectedAppt.setStaffId(loggedInStaff.getStaffId());
        selectedAppt.setAppointmentStatus("In Progress");
        selectedAppt.setECF(selectedAppt.getOrders().size() * 60); // 60 mins per cycle

        if (appointmentDAO.updateAppointment(selectedAppt)) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| APPOINTMENT PROCESSED AND SET TO 'IN PROGRESS'!    |");
            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| Final Total Price : PHP %-26.2f |", selectedAppt.getAppointmentTotalPrice()));
            System.out.println(String.format("| Estimated Duration: %-30s |", selectedAppt.getECF() + " minutes"));
            System.out.println("+----------------------------------------------------+\n");
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Failed to update appointment status.       |");
            System.out.println("+----------------------------------------------------+");
        }
    }

    private void viewLaundryMachines() {
        System.out.println("\n-> Entering View Machines Flow (To be implemented)...");
        // We will fetch all Washers and Dryers from MachineDAO here later
    }
}
