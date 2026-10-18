package com.laundry.util;

import java.util.List;

import com.laundry.dao.AppointmentDAO;
import com.laundry.dao.LaundryOrderDAO;
import com.laundry.dao.PaymentDAO;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.LaundryOrder;
import com.laundry.systemclass.Payment;

public class AppointmentService {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private LaundryOrderDAO orderDAO = new LaundryOrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    // THIS IS THE METHOD THAT VIEWS THE ENTIRE APPOINTMENT
    public Appointment getFullAppointmentDetails(String referenceNumber) {
        
        // 1. Get the base appointment from the database
        List<Appointment> appointments = appointmentDAO.getAppointmentByReference(referenceNumber);
        if (appointments.isEmpty()) {
            return null; // Appointment not found
        }
        Appointment appt = appointments.get(0); // Assuming one active appointment per reference for now
        
        // 2. Fetch all orders for this appointment and attach them
        List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
        for (LaundryOrder order : orders) {
            appt.addOrder(order);
        }

        // 3. Fetch the payment details and attach them
        Payment payment = paymentDAO.getPaymentByAppointment(appt.getAppointmentId());
        if (payment != null) {
            // Set the amount to be paid based on the orders' total price calculation
            payment.setAmountToBePaid(appt.getAppointmentTotalPrice()); 
            appt.setPayment(payment);
        }

        // The Appointment object is now fully loaded with all its data!
        return appt;
    }
}