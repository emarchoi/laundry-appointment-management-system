package com.laundry.helper;

/*
    The helper package contains utility classes and methods 
    used throughout the system.
*/

import java.util.List;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.dao.PaymentDAO;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.systemclass.Payment;

import java.util.Scanner;

/*
    Helper class that handles appointment logics.
    Assembles full appointment records by fetching data from multiple DAO.
*/
public class AppointmentService {

    // Data Access Objects for interacting with the database tables
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    // Fetches a complete Appointment object including its attached laundry orders and payment info
    public Appointment getFullAppointmentDetails(String referenceNumber) {

        // First - Fetch base appointment data from database
        List<Appointment> appointments = appointmentDAO.getAppointmentsByReference(referenceNumber);
        if (appointments.isEmpty()) {
            return null; // If no appointments
        }
        Appointment appt = appointments.get(0); // Fetch the appointment from the list

        // Second - Fetch and attach all laundry orders from the appointment
        List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
        for (LaundryOrder order : orders) {
            appt.addOrder(order);
        }

        // Third - Fetch and attach all laundry orders from the appointment
        Payment payment = paymentDAO.getPaymentByAppointment(appt.getAppointmentId());
        if (payment != null) {

            // Set the amount to be paid based on the orders' total price calculation
            payment.setAmountToBePaid(appt.getAppointmentTotalPrice());
            appt.setPayment(payment);
        }

        // Return the complete appointment record
        return appt;
    }

    // Helper validation for Weighing and Assignment
    // Used in AttendantMenu
    public double validateWeightInput(Scanner scanner) {

        double weight = 0.0;
        boolean validWeight = false;

        while (!validWeight) {
            System.out.print("| Enter Weight in Kg: ");
            String weightInput = scanner.nextLine().trim();

            if (weightInput.isEmpty()) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| ERROR: Input cannot be empty. Try again.           |");
                System.out.println("+----------------------------------------------------+");
                continue;
            }

            try {
                weight = Double.parseDouble(weightInput);
                if (weight <= 0 || weight >= 21) {
                    System.out.println("\n+----------------------------------------------------+");
                    System.out.println("| ERROR: Weight must be between 0 and 20. Try again. |");
                    System.out.println("+----------------------------------------------------+");
                } else {
                    validWeight = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("\n+----------------------------------------------------+");
                System.out.println("| ERROR: Invalid input. Enter a valid number.        |");
                System.out.println("+----------------------------------------------------+");
            }
        }
        return weight;
    }
}
