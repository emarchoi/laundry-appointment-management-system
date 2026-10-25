package com.laundry.systemclass;

/*
    ABSRACT CLASS for transaction payments.
    Inherited by Cash and Digital payment.
*/

public abstract class Payment {
    private int paymentId;
    private int appointmentId;
    private double amountToBePaid;
    private double amountPaid;
    private String paymentStatus;

    // Constructors for object creation, DAO, and initial values 
    public Payment(int paymentId, int appointmentId, double amountToBePaid) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.amountToBePaid = amountToBePaid;
        this.amountPaid = 0.0;
        this.paymentStatus = "Pending";
    }

    // Getters and Setters for DAO encapsulated retrieval and insertion
    public int getPaymentId() { return paymentId; }
    public int getAppointmentId() { return appointmentId; }
    public double getAmountToBePaid() { return amountToBePaid; }
    public double getAmountPaid() { return amountPaid; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public void setAmountToBePaid(double amountToBePaid) { this.amountToBePaid = amountToBePaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    // Called to change the paymentStatus if Paid
    public void markPaid() {
        this.paymentStatus = "Paid";
    }

    // Abstract method
    // Implemented by child classes
    public abstract String getPaymentMethod();
}