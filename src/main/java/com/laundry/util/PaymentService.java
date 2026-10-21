package com.laundry.util;

import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.Payment;

public class PaymentService {
    
    public double processCashPayment(Payment payment, double amount) {
        double balance = payment.getAmountToBePaid();
        if (amount >= balance) {
            payment.setAmountPaid(amount);
            payment.markPaid();
            return amount - balance; // Returns Change
        }
        throw new IllegalArgumentException("Insufficient amount tendered.");
    }

    public String generateDigitalPaymentQR(Appointment appointment) {
        return "+----------------------------------------------------+\n" +
               "|             [SCAN THIS QR CODE TO PAY]             |\n" +
               "|                                                    |\n" +
               "|  APPT ID : " + String.format("%-39s", appointment.getAppointmentId()) + " |\n" +
               "|  AMOUNT  : PHP " + String.format("%-35.2f", appointment.getAppointmentTotalPrice()) + " |\n" +
               "+----------------------------------------------------+";
    }
}