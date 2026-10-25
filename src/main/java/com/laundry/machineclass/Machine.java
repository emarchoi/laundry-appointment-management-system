package com.laundry.machineclass;

/*
    The machineclass package contains all machine-related classes in the system.
*/

/*
    Abstract base class for physical laundry machines.
    Inherited by two Machines (Washer and Dryer).
*/

public abstract class Machine {
    
    // Defines the maximum capacity for all machines.
    private static final double MAX_CAPACITY = 7.0;
    private int machineId;
    private double basePrice;
    private String machineStatus;

    public Machine(int machineId, double basePrice, String machineStatus){
        this.machineId = machineId;
        this.basePrice = basePrice;
        this.machineStatus = machineStatus;
        // "Available", "In Use", "Under Maintenance"
    }

    // Getters and Setters
    public int getMachineId() { return machineId; }
    public static double getMaxCapacity() { return MAX_CAPACITY; }
    public double getBasePrice() { return basePrice; }
    public String getMachineStatus() { return machineStatus; }

    // Concrete Method, checks if machine is available.
    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(machineStatus);
    }

    // Abstract method; implemented by the child classes.
    public abstract String getMachineType();

}
