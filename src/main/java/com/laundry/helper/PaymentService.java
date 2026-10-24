package com.laundry.helper;

import com.laundry.systemclass.Payment;

public class PaymentService {
    
    public double processCashPayment(Payment payment, double amount) {
        double balance = payment.getAmountToBePaid();
        if (amount >= balance) {
            payment.setAmountPaid(amount);
            payment.markPaid();
            return amount - balance; // Returns Change
        }
        throw new IllegalArgumentException("Insufficient amount.");
    }
}