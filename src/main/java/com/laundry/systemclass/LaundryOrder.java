package com.laundry.systemclass;

public class LaundryOrder {
    private int orderId;
    private int appointmentId;
    private String laundryService;
    private double laundryTotalKg; 
    private double orderTotalPrice; 
    private int machinesUsed;

    public LaundryOrder(int orderId, int appointmentId, String laundryService) {
        this.orderId = orderId;
        this.appointmentId = appointmentId;
        this.laundryService = laundryService;
        this.laundryTotalKg = 0.0;
        this.orderTotalPrice = 0.00;
        this.machinesUsed = 0;
    }

    // Getters
    public int getOrderId() { return orderId; }
    public int getAppointmentId() { return appointmentId; }
    public String getLaundryService() { return laundryService; }
    public double getLaundryTotalKg() { return laundryTotalKg; }
    public double getOrderTotalPrice() { return orderTotalPrice; }
    public int getMachinesUsed() { return machinesUsed; }

    // Setters
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public void setLaundryService(String laundryService) { this.laundryService = laundryService; }
    public void setLaundryTotalKg(double laundryTotalKg) { this.laundryTotalKg = laundryTotalKg; }
    public void setOrderTotalPrice(double orderTotalPrice) { this.orderTotalPrice = orderTotalPrice; }
    public void setMachinesUsed(int machinesUsed) { this.machinesUsed = machinesUsed; }


}