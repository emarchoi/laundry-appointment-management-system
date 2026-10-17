package com.laundry.systemclass;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Appointment{

    private int appointmentId;
    private String referenceNumber;
    private int staffId;
    private String appointmentType;
    private String appointmentStatus;
    private double appointmentTotalPrice;
    private LocalDate appointmentDate;
    private LocalTime estimatedCompletionFinished;

    private List<LaundryOrder> orders = new ArrayList<>();
    private Payment payment;

    public Appointment(int appointmentId, String referenceNumber, String appointmentType, LocalDate appointmentDate){
            this.appointmentId = appointmentId;
            this.referenceNumber = referenceNumber;
            this.appointmentType = appointmentType;
            this.appointmentTotalPrice = 0.00;
            this.appointmentStatus = "Pending";
            this.appointmentDate = appointmentDate;
        }
    

    // Getters
    public int getAppointmentId() { return appointmentId; }
    public String getReferenceNumber() { return referenceNumber; }
    public int getStaffId() { return staffId; }
    public String getAppointmentType() { return appointmentType; }
    public String getAppointmentStatus() { return appointmentStatus; }
    public double getAppointmentTotalPrice() { return appointmentTotalPrice; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getECF() { return estimatedCompletionFinished; }

    // Setters
    public void setAppointmentId(int appointmentId ) { this.appointmentId = appointmentId; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    public void setAppointmentType(String appointmentType ) { this.appointmentType = appointmentType; }
    public void setAppointmentStatus(String appointmentStatus ) { this.appointmentStatus = appointmentStatus; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setECF(LocalTime time) { estimatedCompletionFinished = time; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public void setAppointmentTotalPrice() {
        appointmentTotalPrice =  orders.stream().mapToDouble(LaundryOrder::getOrderTotalPrice).sum();
    }

    public boolean isPaid() {
        return payment != null && "Paid".equalsIgnoreCase(payment.getPaymentStatus());
    }

    public boolean canBeCancelled() {
        return "Pending".equalsIgnoreCase(appointmentStatus);
    }
}