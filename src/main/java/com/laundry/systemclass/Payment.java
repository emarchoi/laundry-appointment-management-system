package com.laundry.systemclass;

public abstract class Payment {
    private int paymentId;
    private int appointmentId;
    private double amountToBePaid;
    private double amountPaid;
    private String paymentStatus;

    public Payment(int paymentId, int appointmentId, double amountToBePaid) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.amountToBePaid = amountToBePaid;
        this.amountPaid = 0.0;
        this.paymentStatus = "Pending";
    }

        // Getters
    public int getPaymentId() { return paymentId; }
    public int getAppointmentId() { return appointmentId; }
    public double getAmountToBePaid() { return amountToBePaid; }
    public double getAmountPaid() { return amountPaid; }
    public String getPaymentStatus() { return paymentStatus; }


    // Setters
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public void setAmountToBePaid(double amountToBePaid) { this.amountToBePaid = amountToBePaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        
    public void markPaid() {
        this.paymentStatus = "Paid";
    }

    public abstract String getPaymentMethod();
}