package com.laundry.systemclass;

import java.time.LocalTime;

public class MachineUsage {
    private int orderId;
    private int machineId;
    private double laundryLoad;
    private double priceCharge;
    private LocalTime laundryStartTime;
    private int laundryDuration;

    public MachineUsage(int orderId, int machineId, double laundryLoad, double priceCharge, LocalTime laundryStartTime, int laundryDuration) {
        this.orderId = orderId;
        this.machineId = machineId;
        this.laundryLoad = laundryLoad;
        this.priceCharge = priceCharge;
        this.laundryStartTime = laundryStartTime;
        this.laundryDuration = laundryDuration;
    }

    public int getOrderId() { return orderId; }
    public int getMachineId() { return machineId; }
    public double getLaundryLoad() { return laundryLoad; }
    public double getPriceCharge() { return priceCharge; }
    public LocalTime getLaundryStartTime() { return laundryStartTime; }
    public int getLaundryDuration() { return laundryDuration; }

    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setMachineId(int machineId) { this.machineId = machineId; }
    public void setLaundryLoad(double laundryLoad) { this.laundryLoad = laundryLoad; }
    public void setPriceCharge(double priceCharge) { this.priceCharge = priceCharge; }
    public void setLaundryStartTime(LocalTime laundryStartTime) { this.laundryStartTime = laundryStartTime; }
    public void setLaundryDuration(int laundryDuration) { this.laundryDuration = laundryDuration; }
}