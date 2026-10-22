package com.laundry.cli.menus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.dao.MachineDAO;
import com.laundry.dao.MachineUsageDAO;
import com.laundry.machineclass.Machine;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.systemclass.MachineUsage;
import com.laundry.userclass.Staff;
import com.laundry.util.AppointmentService;
import com.laundry.util.MachineService;

public class AttendantMenu {

    private Scanner scanner;
    private Staff loggedInStaff;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private MachineDAO machineDAO = new MachineDAO();
    private MachineUsageDAO machineUsageDAO = new MachineUsageDAO();

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
            System.out.println("|  [1] VIEW PENDING APPOINTMENTS (Assign Machines)   |");
            System.out.println("|  [2] VIEW ALL APPOINTMENTS                         |");
            System.out.println("|  [3] VIEW LAUNDRY MACHINES                         |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] LOGOUT                                        |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewPendingAppointments();
                    break;
                case "2": 
                    viewAllAppointments();
                    break;
                case "3":
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
        System.out.println("\n+---------------------------------------------------------+");
        System.out.println("|               PENDING APPOINTMENTS LIST                 |");
        System.out.println("+---------------------------------------------------------+");

        List<Appointment> pendingList = appointmentDAO.getAppointmentByStatus("Pending");

        if (pendingList.isEmpty()) {
            System.out.println("| No pending appointments found.                          |");
            System.out.println("+---------------------------------------------------------+");
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
        System.out.println("|                WEIGHING & ASSIGNMENT               |");
        System.out.println("+----------------------------------------------------+");

        List<MachineUsage> allUsagesForAppt = new ArrayList<>();

        for (LaundryOrder order : selectedAppt.getOrders()) {
            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| Processing Order #%-5d | Service: %-14s |", order.getOrderId(), order.getLaundryService()));
            
            double weight = appointmentService.validateWeightInput(scanner);

            order.setLaundryTotalKg(weight);

            // Calculate number of machines needed: CEILING(Weight / 7)
            int machinesNeeded = machineService.calculateMachinesRequired(weight);
            order.setMachinesUsed(machinesNeeded);

            // Determine machine base price
            double basePrice = order.getLaundryService().equalsIgnoreCase("Dry") ? 65.0 : 50.0;
            if (order.getLaundryService().equalsIgnoreCase("Wash and Dry")) {
                basePrice = 115.0;
            }
            
            double orderPrice = machinesNeeded * basePrice;
            order.setOrderTotalPrice(orderPrice);
            orderDAO.updateOrderDetails(order);

            // Select physical machines to assign based on service type
            String service = order.getLaundryService();
            List<String> requiredTypes = new ArrayList<>();
            if (service.equalsIgnoreCase("Wash")) requiredTypes.add("Washer");
            else if (service.equalsIgnoreCase("Dry")) requiredTypes.add("Dryer");
            else if (service.equalsIgnoreCase("Wash and Dry")) {
                requiredTypes.add("Washer");
                requiredTypes.add("Dryer");
            }

            double loadPerMachine = weight / machinesNeeded;

            // Track machines chosen in this session to prevent double-assigning in the same prompt
            List<Integer> assignedInThisSession = new ArrayList<>();

            for (String mType : requiredTypes) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println(String.format("| AVAILABLE %-40s |", mType.toUpperCase() + "S"));
                System.out.println("+----------------------------------------------------+");
                
                List<Machine> allMachines = machineDAO.getMachinesByType(mType);
                List<Machine> availableMachines = new ArrayList<>();

                for (Machine m : allMachines) {
                    if (m.isAvailable() && !assignedInThisSession.contains(m.getMachineId())) {
                        availableMachines.add(m);
                    }
                }

                if (availableMachines.isEmpty()) {
                    System.out.println("| ERROR: No available " + mType + "s in database!       |");
                    System.out.println("+----------------------------------------------------+");
                    return;
                }

                for (Machine m : availableMachines) {
                    System.out.println(String.format("| Machine ID: %-6d | Status: %-21s |", m.getMachineId(), m.getMachineStatus()));
                }
                System.out.println("+----------------------------------------------------+");

                for (int i = 1; i <= machinesNeeded; i++) {
                    int chosenMachineId = -1;
                    boolean validSelection = false;

                    // Re-prompt loop for validation
                    while (!validSelection) {
                        System.out.print("| Select " + mType + " ID #" + i + " to assign: ");
                        try {
                            chosenMachineId = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("\n+----------------------------------------------------+");
                            System.out.println("| ERROR: Input must be a valid number. Try again.     |");
                            System.out.println("+----------------------------------------------------+");
                            continue;
                        }

                        Machine selectedMachine = machineDAO.getMachineById(chosenMachineId);

                        if (selectedMachine == null) {
                            System.out.println("\n+----------------------------------------------------+");
                            System.out.println("| ERROR: Machine ID #" + chosenMachineId + " does not exist.              |");
                            System.out.println("+----------------------------------------------------+\n");
                        } else if (!selectedMachine.getMachineType().equalsIgnoreCase(mType)) {
                            System.out.println("\n+----------------------------------------------------+");
                            System.out.println("| ERROR: Machine #" + chosenMachineId + " is a " + selectedMachine.getMachineType() + ", not a " + mType + "!       |");
                            System.out.println("+----------------------------------------------------+\n");
                        } else if (!selectedMachine.isAvailable()) {
                            System.out.println("\n+----------------------------------------------------+");
                            System.out.println(String.format("| ERROR: Machine #%d is '%s'. Select another.     |", 
                                chosenMachineId, selectedMachine.getMachineStatus()));
                            System.out.println("+----------------------------------------------------+\n");
                        } else if (assignedInThisSession.contains(chosenMachineId)) {
                            System.out.println("\n+----------------------------------------------------+");
                            System.out.println("| ERROR: Machine #" + chosenMachineId + " was already selected above!   |");
                            System.out.println("+----------------------------------------------------+\n");
                        } else {
                            validSelection = true;
                        }
                    }

                    // Record assignment in this session
                    assignedInThisSession.add(chosenMachineId);

                    // Create MachineUsage entry
                    MachineUsage usage = new MachineUsage(
                        order.getOrderId(), 
                        chosenMachineId, 
                        loadPerMachine, 
                        (mType.equalsIgnoreCase("Washer") ? 50.0 : 65.0) * machinesNeeded, 
                        LocalTime.now()
                    );
                    machineUsageDAO.addMachineUsage(usage);
                    allUsagesForAppt.add(usage);

                    // Automatically update machine status to 'In Use' in DB
                    machineDAO.updateMachineStatus(chosenMachineId, "In Use");
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println(String.format("| -> %s #%d successfully set to 'In Use'.", mType, chosenMachineId));
                    System.out.println("+----------------------------------------------------+\n");
                }
            }
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

    private void viewAllAppointments() {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                   VIEW ALL APPOINTMENTS                     |");
        System.out.println("+-------------------------------------------------------------+");

        List<Appointment> allAppts = appointmentDAO.getAllAppointments();

        if (allAppts.isEmpty()) {
            System.out.println("+-------------------------------------------------------------+");
            System.out.println("| No appointments found in database.                          |");
            System.out.println("+-------------------------------------------------------------+");
            return;
        }

        for (Appointment appt : allAppts) {
            System.out.println("+-------------------------------------------------------------+");
            System.out.println(String.format("| Appt ID: %-6d | Ref: %-9s | Status: %-12s ", 
                appt.getAppointmentId(), appt.getReferenceNumber(), appt.getAppointmentStatus()));
            System.out.println(String.format("| Type: %-9s | ECF: %-9s | Price: PHP %-7.2f ", 
                appt.getAppointmentType(), (appt.getECF() > 0 ? appt.getECF() + " mins" : "Pending"), appt.getAppointmentTotalPrice()));
            
            // Fetch and summary list of corresponding orders
            List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
            System.out.println("+-------------------------------------------------------------+");
            System.out.println("| Orders Summary:                                             |");
            System.out.println("+-------------------------------------------------------------+");
            for (int i = 0; i < orders.size(); i++) {
                LaundryOrder o = orders.get(i);
                System.out.println(String.format("| %-2d. Order #%-6d | %-12s | Weight: %-4.1fkg ", 
                    (i + 1), o.getOrderId(), o.getLaundryService(), o.getLaundryTotalKg()));
            }
            System.out.println("+-------------------------------------------------------------+\n");
        }
    }

    private void viewLaundryMachines() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                VIEW LAUNDRY MACHINES               |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("|  [1] View Washers                                  |");
        System.out.println("|  [2] View Dryers                                   |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("|  [0] Back to Attendant Menu                        |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("SELECT OPTION: ");

        String choice = scanner.nextLine();
        String machineType;

        if (choice.equals("1")) {
            machineType = "Washer";
        } else if (choice.equals("2")) {
            machineType = "Dryer";
        } else {
            return;
        }

        List<MachineDAO.MachineUsageDetail> machines = machineDAO.getMachineAssignmentDetails(machineType);

        System.out.println("\n+-----------------------------------------------------------------------------------+");
        System.out.println(String.format("| %-81s |", machineType.toUpperCase() + " STATUS LIST"));
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.println(String.format("| %-10s | %-12s | %-15s | %-20s | %-12s |", 
            "Machine ID", "LaundryLoad", "AssignedTo", "Status", "Duration"));
        System.out.println("+-----------------------------------------------------------------------------------+");

        if (machines.isEmpty()) {
            System.out.println("\n+-----------------------------------------------------------------------------------+");
            System.out.println("| No " + machineType.toLowerCase() + "s found in database.                                             |");
            System.out.println("+-----------------------------------------------------------------------------------+");
            return;
        }

        for (MachineDAO.MachineUsageDetail m : machines) {
            String loadStr = m.getLaundryLoad() > 0 ? String.format("%.1f kg", m.getLaundryLoad()) : "0.0 kg";
            String durationStr = m.getDuration() > 0 ? m.getDuration() + " mins" : "0 mins";

            System.out.println(String.format("| %-10d | %-12s | %-15s | %-20s | %-12s |", 
                m.getMachineId(), 
                loadStr, 
                m.getAssignedTo(), 
                m.getMachineStatus(), 
                durationStr));
        }
        System.out.println("+-----------------------------------------------------------------------------------+");

        System.out.print("Enter Machine ID to Update Status (or 0 to exit): ");
        int machineId;
        try {
            machineId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return;
        }

        if (machineId == 0) return;

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                  SELECT NEW STATUS                 |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("|  [1] Available                                     |");
        System.out.println("|  [2] In Use                                        |");
        System.out.println("|  [3] Under Maintenance                             |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("SELECT OPTION: ");
        
        String statusChoice = scanner.nextLine();
        String newStatus;

        switch (statusChoice) {
            case "1": newStatus = "Available"; break;
            case "2": newStatus = "In Use"; break;
            case "3": newStatus = "Under Maintenance"; break;
            default:
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| Invalid selection. Returning to menu.              |");
                System.out.println("+----------------------------------------------------+");
                return;
        }

        if (machineDAO.updateMachineStatus(machineId, newStatus)) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println(String.format("| -> Machine #%-3d status updated to %-15s |", machineId, newStatus));
            System.out.println("+----------------------------------------------------+");
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Failed to update machine status in DB.      |");
            System.out.println("+----------------------------------------------------+");
        }
    }
}
