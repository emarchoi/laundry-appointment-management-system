package com.laundry.machineclass;

// Abstract: Inherit(Washer, Dryer)
public abstract class Machine {
    
    private static final double MAX_CAPACITY = 7.0;
    private int machineId;
    private double basePrice;
    private String machineStatus;

    public Machine(int machineId, double basePrice, String machineStatus){
        this.machineId = machineId;
        this.basePrice = basePrice;
        this.machineStatus = machineStatus;
    }

    public int getMachineId() { return machineId; }
    public static double getMaxCapacity() { return MAX_CAPACITY; }
    public double getBasePrice() { return basePrice; }
    public String getMachineStatus() { return machineStatus; }

    public void updateMachineStatus(String machineStatus) { this.machineStatus = machineStatus; }

    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(machineStatus);
    }

    public abstract String getMachineType();

}
