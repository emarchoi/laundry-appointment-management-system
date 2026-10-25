package com.laundry.systemclass;

/*
    The systemclass package contains the main system-related classes.
*/

import java.time.*;
import java.util.ArrayList;
import java.util.List;

/*
    Represents a customer appointment holding attached orders and payment info.
*/
public class Appointment {

    private int appointmentId;
    private String referenceNumber;
    private Integer staffId; // Can be null because Integer is a wrapper class (use of Unboxing later)
    private String appointmentType;
    private String appointmentStatus;
    private double appointmentTotalPrice;
    private LocalDate appointmentDate;
    private int estimatedCompletionFinished; 

    // Aggregation: Contains a list of orders and a payment instance
    private List<LaundryOrder> orders = new ArrayList<>();
    private Payment payment;

    // Constructors for object creation, DAO, and initial values 
    public Appointment(int appointmentId, String referenceNumber, String appointmentType, LocalDate appointmentDate) {
        this.appointmentId = appointmentId;
        this.referenceNumber = referenceNumber;
        this.appointmentType = appointmentType;
        this.appointmentTotalPrice = 0.00;
        this.appointmentStatus = "Pending";
        this.appointmentDate = appointmentDate;
        this.estimatedCompletionFinished = 0;
    }

    // Getters and Setters for DAO encapsulated retrieval and insertion
    public int getAppointmentId() { return appointmentId; }
    public String getReferenceNumber() { return referenceNumber; }
    public Integer getStaffId() { return staffId; }
    public String getAppointmentType() { return appointmentType; }
    public String getAppointmentStatus() { return appointmentStatus; }
    public double getAppointmentTotalPrice() { return appointmentTotalPrice; }
    public LocalDate getAppointmentDate() { return appointmentDate; } 
    public int getECF() { return estimatedCompletionFinished; }

    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setAppointmentTotalPrice(double totalPrice) { this.appointmentTotalPrice = totalPrice; }
    public void setECF(int time) { this.estimatedCompletionFinished = time; }

    // Order Management
    public List<LaundryOrder> getOrders() {
        return orders;
    }

    public void addOrder(LaundryOrder order) {
        this.orders.add(order);
        computeAppointmentTotalPrice();
    }

    // Payment Management
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // Loops through all orders to compute the total appointment cost.
    public void computeAppointmentTotalPrice() {

        double total = 0.0;

        for (LaundryOrder order : orders) {

            total += order.getOrderTotalPrice();
        }
        this.appointmentTotalPrice = total;
    }

    // Checks if the appointment is paid or pending.
    public boolean isPaid() {
        return payment != null && "Paid".equalsIgnoreCase(payment.getPaymentStatus());
    }

    // Checks if the appointment is allowed to be cancelled.
    public boolean canBeCancelled() {
        return "Pending".equalsIgnoreCase(appointmentStatus);
    }
}
