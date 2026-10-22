package com.laundry.systemclass;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Appointment {

    private int appointmentId;
    private String referenceNumber;
    private Integer staffId;
    private String appointmentType;
    private String appointmentStatus;
    private double appointmentTotalPrice;
    private LocalDate appointmentDate;
    private int estimatedCompletionFinished; // in minutes

    private List<LaundryOrder> orders = new ArrayList<>();
    private Payment payment;

    public Appointment(int appointmentId, String referenceNumber, String appointmentType, LocalDate appointmentDate) {
        this.appointmentId = appointmentId;
        this.referenceNumber = referenceNumber;
        this.appointmentType = appointmentType;
        this.appointmentTotalPrice = 0.00;
        this.appointmentStatus = "Pending";
        this.appointmentDate = appointmentDate;
        this.estimatedCompletionFinished = 0;
    }

    // Getters
    public int getAppointmentId() {
        return appointmentId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public double getAppointmentTotalPrice() {
        return appointmentTotalPrice;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public int getECF() {
        return estimatedCompletionFinished;
    }

    // Setters
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTotalPrice(double totalPrice) {
        this.appointmentTotalPrice = totalPrice;
    }

    public void setECF(int time) {
        this.estimatedCompletionFinished = time;
    }

    // Order Management
    public List<LaundryOrder> getOrders() {
        return orders;
    }

    public void addOrder(LaundryOrder order) {
        this.orders.add(order);
        computeAppointmentTotalPrice(); // Automatically updates the price when an order is added
    }

    public void computeAppointmentTotalPrice() {

        double total = 0.0;

        for (LaundryOrder order : orders) {

            total += order.getOrderTotalPrice();
        }
        this.appointmentTotalPrice = total;
    }

    // Payment Management
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public boolean isPaid() {
        return payment != null && "Paid".equalsIgnoreCase(payment.getPaymentStatus());
    }

    public boolean canBeCancelled() {
        return "Pending".equalsIgnoreCase(appointmentStatus);
    }
}
