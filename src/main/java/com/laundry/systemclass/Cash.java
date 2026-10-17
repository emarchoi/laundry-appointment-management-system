package com.laundry.systemclass;

public class Cash extends Payment {

    public Cash(int paymentId, int appointmentId, double amountToBePaid) {
        super(paymentId, appointmentId, amountToBePaid);
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }
}
