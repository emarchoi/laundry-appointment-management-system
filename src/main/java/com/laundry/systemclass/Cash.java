package com.laundry.systemclass;

/*
    Handles cash transactions and change calculation.
    Inherits from Payment abstract class.
*/

public class Cash extends Payment {

    public Cash(int paymentId, int appointmentId, double amountToBePaid) {
        super(paymentId, appointmentId, amountToBePaid);
    }

    // Method overriding: Polymorphism
    // Provides the implementation of the abstract method in Cash.
    @Override
    public String getPaymentMethod() {
        return "Cash";
    }
}
