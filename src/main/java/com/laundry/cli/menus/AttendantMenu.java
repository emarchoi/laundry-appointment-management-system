package com.laundry.cli.menus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.CustomerDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.dao.MachineDAO;
import com.laundry.dao.MachineUsageDAO;
import com.laundry.dao.PaymentDAO;
import com.laundry.helper.AppointmentService;
import com.laundry.helper.MachineService;
import com.laundry.machineclass.Machine;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.Cash;
import com.laundry.systemclass.DigitalPayment;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.systemclass.MachineUsage;
import com.laundry.systemclass.Payment;
import com.laundry.userclass.Customer;
import com.laundry.userclass.Staff;

/*
    User interface menu for the Laundry Attendant role.
    Handles weighing, machine assignments, monitoring, payments, and status updates.
 */
public class AttendantMenu {

    // Inputs and logged-in user session
    private Scanner scanner;
    private Staff loggedInStaff;

    // Database access objects (DAOs) for database CRUD queries
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private MachineDAO machineDAO = new MachineDAO();
    private MachineUsageDAO machineUsageDAO = new MachineUsageDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    // Helper classes instantiation
    private AppointmentService appointmentService = new AppointmentService();
    private MachineService machineService = new MachineService();

    // Constructor initializing the Scanner and the currently logged-in Attendant
    // Used in StaffLoginMenu.java
    public AttendantMenu(Scanner scanner, Staff loggedInStaff) {
        this.scanner = scanner;
        this.loggedInStaff = loggedInStaff;
    }

    // Entry point, display loop showing UI for the Laundry Attendant
    public void display() {
        boolean inAttendantMenu = true;

        while (inAttendantMenu) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println(String.format("| %-50s |", "ATTENDANT MENU - Welcome, " + loggedInStaff.getFullName()));
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] VIEW PENDING APPOINTMENTS                     |");
            System.out.println("|  [2] VIEW ALL APPOINTMENTS                         |");
            System.out.println("|  [3] VIEW LAUNDRY MACHINES                         |");
            System.out.println("|  [4] CLEAR CANCELLED APPOINTMENTS                  |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [5] UPDATE APPOINTMENT STATUS                     |");
            System.out.println("|  [6] PROCESS PAYMENT                               |");
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
                case "4":
                    clearCancelledAppointments();
                    break;
                case "5":
                    updateAppointmentStatus();
                    break;
                case "6":
                    processPayment();
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

    // Option [1]: Displays pending appointments, inputs laundry weight.
    // Where calculation of required machines using and assigning machines happen.
    private void viewPendingAppointments() {
        System.out.println("\n+---------------------------------------------------------+");
        System.out.println("|               PENDING APPOINTMENTS LIST                 |");
        System.out.println("+---------------------------------------------------------+");

        // Fetch pending appointments from database
        List<Appointment> pendingList = appointmentDAO.getPendingAppointments();

        if (pendingList.isEmpty()) {
            System.out.println("| No pending appointments found.                          |");
            System.out.println("+---------------------------------------------------------+");
            return;
        }

        // List each pending appointment along with its order count
        for (Appointment appt : pendingList) {
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

        if (refNum.equals("0")) {
            return;
        }

        // Retrieve full appointment record
        Appointment selectedAppt = appointmentService.getFullAppointmentDetails(refNum);
        if (selectedAppt == null || !selectedAppt.canBeCancelled()) {
            System.out.println("\n+-----------------------------------------------------+");
            System.out.println("| ERROR: Invalid or non-pending appointment reference.|");
            System.out.println("+-----------------------------------------------------+");
            return;
        }

        // Loop through each attached laundry order to input weight and assign machines
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                WEIGHING & ASSIGNMENT               |");
        System.out.println("+----------------------------------------------------+");

        List<MachineUsage> allUsagesForAppt = new ArrayList<>();

        for (LaundryOrder order : selectedAppt.getOrders()) {
            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| Processing Order #%-5d | Service: %-14s |", order.getOrderId(), order.getLaundryService()));

            // Weigh validation from AppointmentService
            double weight = appointmentService.validateWeightInput(scanner);

            order.setLaundryTotalKg(weight);

            // Calculate number of machines needed from MachineService
            int machinesRequired = machineService.calculateMachinesRequired(weight);
            order.setMachinesUsed(machinesRequired);

            // Determine base price per machine according to laundry service
            double basePrice = order.getLaundryService().equalsIgnoreCase("Dry") ? 65.0 : 50.0;
            if (order.getLaundryService().equalsIgnoreCase("Wash and Dry")) {
                basePrice = 115.0;
            }

            // Calculation of order price from MachineService
            double orderPrice = machineService.calculateOrderPrice(machinesRequired, basePrice);
            order.setOrderTotalPrice(orderPrice);
            orderDAO.updateOrderDetails(order);

            // Identify required machine types based on selected service
            String service = order.getLaundryService();
            List<String> requiredTypes = new ArrayList<>();
            if (service.equalsIgnoreCase("Wash")) {
                requiredTypes.add("Washer");
            } else if (service.equalsIgnoreCase("Dry")) {
                requiredTypes.add("Dryer");
            } else if (service.equalsIgnoreCase("Wash and Dry")) {
                requiredTypes.add("Washer");
                requiredTypes.add("Dryer");
            }

            // Calculation of laundryLoad per machine from MachineService
            double loadPerMachine = machineService.calculateLaundryLoad(weight, machinesRequired);

            // Track machines chosen in this session for assignment validation
            List<Integer> assignedInThisSession = new ArrayList<>();

            // Prompt attendant to select available physical machines
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
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| ERROR: No available " + mType + "s in database!       |");
                    System.out.println("+----------------------------------------------------+");
                    return;
                }

                for (Machine m : availableMachines) {
                    System.out.println(String.format("| Machine ID: %-6d | Status: %-21s |", m.getMachineId(), m.getMachineStatus()));
                }
                System.out.println("+----------------------------------------------------+");

                for (int i = 1; i <= machinesRequired; i++) {
                    int chosenMachineId = -1;
                    boolean validSelection = false;

                    // Re-prompt loop for assignment validation
                    while (!validSelection) {
                        System.out.print("| Select " + mType + " ID #" + " to assign: ");
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
                            System.out.println("| ERROR: Machine #" + chosenMachineId + " does not exist.                 |");
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

                    // Record machine usage in database
                    MachineUsage usage = new MachineUsage(order.getOrderId(), chosenMachineId, loadPerMachine, LocalTime.now());
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

        // L146
        // Recalculate appointment total price, assign staff ID, set status to 'In Progress', and save ECF timer
        selectedAppt.computeAppointmentTotalPrice();
        selectedAppt.setStaffId(loggedInStaff.getStaffId());
        selectedAppt.setAppointmentStatus("In Progress");
        selectedAppt.setECF(selectedAppt.getOrders().size() * 60);

        if (appointmentDAO.updateAppointment(selectedAppt)) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| APPOINTMENT PROCESSED AND SET TO 'IN PROGRESS'!    |");
            System.out.println("+----------------------------------------------------+");
            System.out.println(String.format("| Final Total Price : PHP %-26.2f |", selectedAppt.getAppointmentTotalPrice()));
            System.out.println(String.format("| Estimated Duration: %-30s |", selectedAppt.getECF() + " minutes"));
            System.out.println("+----------------------------------------------------+");
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Failed to update appointment status.       |");
            System.out.println("+----------------------------------------------------+");
        }
    } // end of [1] viewPendingAppointment

    // Option [2]: Displays a list of all system appointments (with complete details) and their associated orders
    private void viewAllAppointments() {
        System.out.println("\n+---------------------------------------------------------------+");
        System.out.println("|                     VIEW ALL APPOINTMENTS                     |");
        System.out.println("+---------------------------------------------------------------+");

        // Retrieve all appointments in the database and store it in a list
        List<Appointment> allAppts = appointmentDAO.getAllAppointments();

        if (allAppts.isEmpty()) {
            System.out.println("\n+---------------------------------------------------------------+");
            System.out.println("| No appointments found in database.                            |");
            System.out.println("+---------------------------------------------------------------+");
        }

        // Loop through each appointment and list its summary details
        for (Appointment appt : allAppts) {
            Customer customer = customerDAO.getCustomerDetails(appt.getReferenceNumber());
            String customerName = (customer != null) ? customer.getFullName() : "N/A";

            System.out.println("+---------------------------------------------------------------+");
            System.out.println(String.format("| Customer : %s", customerName));
            System.out.println("+---------------------------------------------------------------+");
            System.out.println(String.format("| Appt ID: %-6d | Ref: %-9s | Status: %-12s ",
                    appt.getAppointmentId(), appt.getReferenceNumber(), appt.getAppointmentStatus()));
            System.out.println(String.format("| Type: %-9s | ECF: %-9s | Price: PHP%-5.2f | %s",
                    appt.getAppointmentType(), (appt.getECF() > 0 ? appt.getECF() + " mins" : "0 mins"),
                    appt.getAppointmentTotalPrice(), (appt.isPaid() ? "Paid" : "Pending")));

            // Fetch and summary list of orders corresponding to the now looped appointment
            List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
            System.out.println("+---------------------------------------------------------------+");
            System.out.println("| Orders Summary:                                               |");
            System.out.println("+---------------------------------------------------------------+");
            for (int i = 0; i < orders.size(); i++) {
                LaundryOrder o = orders.get(i);
                System.out.println(String.format("| %d. Order #%-6d | %-12s | Weight: %-4.1fkg ",
                        (i + 1), o.getOrderId(), o.getLaundryService(), o.getLaundryTotalKg()));
            }
            System.out.println("+---------------------------------------------------------------+\n");
        }
    } // end of [2] viewAllAppointments()

    // Option [3]: Displays a table of physical machines (Washers/Dryers) with load, assigned order, status, and duration
    // Also prompts to update machine status
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

        // Fetch machine status list with full usage details
        List<MachineDAO.MachineUsageDetail> machines = machineDAO.getMachineAssignmentDetails(machineType);

        // UI Headers
        System.out.println("\n+-----------------------------------------------------------------------------------+");
        System.out.println(String.format("| %-81s |", machineType.toUpperCase() + " STATUS LIST"));
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.println(String.format("| %-10s | %-12s | %-15s | %-20s | %-12s |",
                "Machine ID", "Laundry Load", "Assigned To", "Status", "Duration"));
        System.out.println("+-----------------------------------------------------------------------------------+");

        if (machines.isEmpty()) {
            System.out.println("\n+-----------------------------------------------------------------------------------+");
            System.out.println("| No " + machineType.toLowerCase() + "s found in database.                                             |");
            System.out.println("+-----------------------------------------------------------------------------------+");
            return;
        }

        // List of Machines
        for (MachineDAO.MachineUsageDetail m : machines) {
            String loadStr = m.getLaundryLoad() > 0 ? String.format("%.1f kg", m.getLaundryLoad()) : "0.0 kg";
            String durationStr = m.getDuration() > 0 ? m.getDuration() + " mins" : "0 mins";

            System.out.println(String.format("| %-10d | %-12s | %-15s | %-20s | %-12s |",
                    m.getMachineId(), loadStr, m.getAssignedTo(), m.getMachineStatus(), durationStr));
        }
        System.out.println("+-----------------------------------------------------------------------------------+");

        // Prompt for updating the machine status
        System.out.print("Enter Machine ID to Update Status (or 0 to exit): ");
        int machineId;
        try {
            machineId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return;
        }

        if (machineId == 0) {
            return;
        }

        // Display new status selection menu
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
            case "1":
                newStatus = "Available";
                break;
            case "2":
                newStatus = "In Use";
                break;
            case "3":
                newStatus = "Under Maintenance";
                break;
            default:
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| Invalid selection. Returning to menu.              |");
                System.out.println("+----------------------------------------------------+");
                return;
        }

        if (machineDAO.updateMachineStatus(machineId, newStatus)) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println(String.format("| -> Machine #%-2d status updated to %-15s |", machineId, newStatus));
            System.out.println("+----------------------------------------------------+");
        } else {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Failed to update machine status in DB.      |");
            System.out.println("+----------------------------------------------------+");
        }
    } // end of [3] viewLaundryMachines

    // Option [4]: Prompts for confirmation and bulk-deletes all cancelled appointments 
    // along with their associated order and payment records from the database.
    private void clearCancelledAppointments() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|        BULK DELETE ALL CANCELLED APPOINTMENTS      |");
        System.out.println("+----------------------------------------------------+");
        System.out.println("| WARNING: This action will permanently remove all   |");
        System.out.println("| appointments with status 'Cancelled' and their     |");
        System.out.println("| associated order & payment records from DB.        |");
        System.out.println("+----------------------------------------------------+");

        // Double Confirmation Loop
        while (true) {
            System.out.print("| Confirm bulk deletion of ALL cancelled records? (Y/N): ");
            String confirmInput = scanner.nextLine().trim();

            // Perform bulk deletion if confirmed
            if ("Y".equalsIgnoreCase(confirmInput)) {
                boolean success = appointmentDAO.clearCancelledAppointments();
                System.out.println("\n+----------------------------------------------------+");
                if (success) {
                    System.out.println(String.format("| %-50s |", "SUCCESS: All cancelled appointments cleared."));
                } else {
                    System.out.println(String.format("| %-50s |", "NOTICE: No cancelled appointments found to clear."));
                }
                System.out.println("+----------------------------------------------------+");
                break;
                // Abort deletion if user chooses 'N'
            } else if ("N".equalsIgnoreCase(confirmInput)) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println(String.format("| %-50s |", "Bulk clearing aborted."));
                System.out.println("+----------------------------------------------------+");
                break;
            } else {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| ERROR: Please enter 'Y' for Yes or 'N' for No.     |");
                System.out.println("+----------------------------------------------------+");
            }
        }
    } // end of [4] clearCancelledAppointments

    // Option [5]: Updates appointment status to 'In Progress' or 'Ready for Pickup'
    // Automatically releases assigned machines back to 'Available' when setting 'Ready for Pickup'
    private void updateAppointmentStatus() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|              UPDATE APPOINTMENT STATUS             |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine().trim();

        // Retrieve an appointment based on the reference number
        // Only update one appointment
        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);
        Customer customer = customerDAO.getCustomerDetails(refNum);
        Payment payment = paymentDAO.getPaymentByAppointment(appt.getAppointmentId());

        if (appt == null) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| ERROR: Appointment not found.                      |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Display Details
        System.out.println("\n+----------------------------------------------------+");
        System.out.println(String.format("| Customer Name    : %-31s |", customer.getFullName()));
        System.out.println("+----------------------------------------------------+");
        System.out.println(String.format("| Reference Number : %-31s |", appt.getReferenceNumber()));
        System.out.println(String.format("| Current Status   : %-31s |", appt.getAppointmentStatus()));
        System.out.println(String.format("| Appointment Type : %-31s |", appt.getAppointmentType()));
        System.out.println("+----------------------------------------------------+");

        // Return automatically if appointment is completed
        if (appt.getAppointmentStatus().equalsIgnoreCase("Completed")) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Appointment is already completed.                  |");
            System.out.println("+----------------------------------------------------+");
            return;
        }

        // Prompt of new Status
        boolean validSelection = false;

        while (!validSelection) {
            System.out.println("+----------------------------------------------------+");
            System.out.println("| Select New Status:                                 |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] Completed                                     |");
            System.out.println("|  [2] In Progress                                   |");
            System.out.println("|  [3] Ready for Pickup                              |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] Cancel                                        |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");
            String choice = scanner.nextLine().trim();

            String newStatus = null;

            switch (choice) {
                case "1":
                    if (!payment.getPaymentStatus().equalsIgnoreCase("Paid")) {
                        System.out.println("\n+----------------------------------------------------+");
                        System.out.println("| Appointment must be Paid first.                    |");
                        System.out.println("+----------------------------------------------------+");
                        return;
                    }
                    newStatus = "Completed";
                    validSelection = true;
                    break;
                case "2":
                    newStatus = "In Progress";
                    validSelection = true;
                    break;
                case "0":
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Operation cancelled. Returning to menu.            |");
                    System.out.println("+----------------------------------------------------+");
                    return;
                default:
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| Invalid option. Please try again.                  |");
                    System.out.println("+----------------------------------------------------+");
            }

            if (newStatus != null) {
                appt.setAppointmentStatus(newStatus);
                appt.setStaffId(loggedInStaff.getStaffId());

                // If 'Ready for Pickup', assigned machines back to 'Available' and reset ECF to 0
                if ("Ready for Pickup".equalsIgnoreCase(newStatus)) {
                    List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
                    for (LaundryOrder order : orders) {
                        List<MachineUsage> usages = machineUsageDAO.getUsageByOrder(order.getOrderId());
                        for (MachineUsage usage : usages) {
                            machineDAO.updateMachineStatus(usage.getMachineId(), "Available");
                        }
                        machineUsageDAO.deleteUsageByOrder(order.getOrderId()); // Resets AssignedTo to 'None'
                    }
                    appt.setECF(0);
                }

                if (appointmentDAO.updateAppointment(appt)) {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("|            STATUS UPDATED SUCCESSFULLY!            |");
                    System.out.println("+----------------------------------------------------+");
                    System.out.println(String.format("| New Status : %-37s |", newStatus));
                    System.out.println(String.format("| Updated By : %-37s |", loggedInStaff.getFullName()));

                    if ("Ready for Pickup".equalsIgnoreCase(newStatus)) {
                        System.out.println(String.format("| Machines   : %-37s |", "Released to 'Available'"));
                    }
                    System.out.println("+----------------------------------------------------+");
                } else {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| ERROR: Failed to update status in database.        |");
                    System.out.println("+----------------------------------------------------+");
                }
            }
        }
    } // end of Option [5]: updateAppointmentStatus

    // Option [6]: Processes cash or digital payments e.g. at the Counter
    // Marks appointment as 'Completed' upon payment success
    private void processPayment() {
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|                   PROCESS PAYMENT                  |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("| Enter Reference Number: ");
        String refNum = scanner.nextLine().trim();

        // Retrieve an appointment based on the reference number
        // Process only one appointment
        Appointment appt = appointmentService.getFullAppointmentDetails(refNum);

        // Validations
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

        // Display Amount Due
        System.out.println("+----------------------------------------------------+");
        System.out.println(String.format("| Total Amount Due: PHP %-28.2f |", totalAmount));
        System.out.println("+----------------------------------------------------+");

        boolean validSelection = false;

        // Loop until a valid payment choice or cancellation is selected
        while (!validSelection) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("| Select Payment Method:                             |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [1] Cash                                          |");
            System.out.println("|  [2] E-Payment (Digital)                           |");
            System.out.println("+----------------------------------------------------+");
            System.out.println("|  [0] Cancel / Back to Attendant Menu               |");
            System.out.println("+----------------------------------------------------+");
            System.out.print("SELECT OPTION: ");
            String payChoice = scanner.nextLine().trim();

            int paymentId = new Random().nextInt(900000) + 100000;

            // Payment validation for cash
            if ("1".equals(payChoice)) {
                validSelection = true;
                boolean validAmount = false;
                double amountPd = 0.0;

                while (!validAmount) {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.print("| Enter Amount Paid: ");
                    String input = scanner.nextLine().trim();

                    try {
                        amountPd = Double.parseDouble(input);
                        if (amountPd < totalAmount) {
                            System.out.println("\n+-----------------------------------------------+");
                            System.out.println("| ERROR: Amount paid is less than total due.    |");
                            System.out.println("+-----------------------------------------------+");
                        } else {
                            validAmount = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\n+-----------------------------------------------+");
                        System.out.println("| ERROR: Enter a valid numeric amount.          |");
                        System.out.println("+-----------------------------------------------+");
                    }
                }

                // Create Payment/ Cash Payment Instantiation (Polymorphism) and update the Database
                double change = amountPd - totalAmount;
                Payment payment = new Cash(paymentId, appt.getAppointmentId(), totalAmount);
                payment.setAmountPaid(amountPd);
                payment.markPaid();

                if (paymentDAO.createPayment(payment, "Cash")) {
                    appt.setPayment(payment);

                    if (!"In Progress".equalsIgnoreCase(appt.getAppointmentStatus())) {
                        appt.setAppointmentStatus("Completed");
                    }
                    appointmentDAO.updateAppointment(appt);

                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("|             PAYMENT RECORDED SUCCESSFULLY          |");
                    System.out.println("+----------------------------------------------------+");
                    System.out.println(String.format("| Amount Due      : PHP %-28.2f |", totalAmount));
                    System.out.println(String.format("| Amount Paid     : PHP %-28.2f |", amountPd));
                    System.out.println(String.format("| Change          : PHP %-28.2f |", change));
                    System.out.println("+----------------------------------------------------+");
                } else {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| ERROR: Could not record payment in database.       |");
                    System.out.println("+----------------------------------------------------+");
                }

                // Create Payment/ Digital Payment Instantiation (Polymorphism) and update the Database
            } else if ("2".equals(payChoice)) {
                validSelection = true;
                Payment payment = new DigitalPayment(paymentId, appt.getAppointmentId(), totalAmount);
                payment.setAmountPaid(totalAmount);
                payment.markPaid();

                if (paymentDAO.createPayment(payment, "E-Payment")) {
                    appt.setPayment(payment);

                    if (!"In Progress".equalsIgnoreCase(appt.getAppointmentStatus())) {
                        appt.setAppointmentStatus("Completed");
                    }
                    appointmentDAO.updateAppointment(appt);

                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("|          E-PAYMENT CONFIRMED SUCCESSFULLY!         |");
                    System.out.println("+----------------------------------------------------+");
                    System.out.println(String.format("| Total Paid      : PHP %-28.2f |", totalAmount));
                    System.out.println("+----------------------------------------------------+");
                } else {
                    System.out.println("| ERROR: Could not record payment in database.       |");
                    System.out.println("+----------------------------------------------------+");
                }

            } else if ("0".equals(payChoice)) {
                validSelection = true;
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| Payment selection cancelled. Returning to menu.    |");
                System.out.println("+----------------------------------------------------+");
            } else {
                System.out.println("+----------------------------------------------------+");
                System.out.println("| Invalid payment method selected. Please try again. |");
                System.out.println("+----------------------------------------------------+");
            }
        }
    } // end of Option [6] : processPayment()
}
