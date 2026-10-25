package com.laundry.machineclass;

// Dryer machine class with a fixed price of PHP 65.00 (Client)

public class Dryer extends Machine {

    private static final double DRYER_PRICE = 65.0;

    public Dryer(int machineId, String machineStatus) {
        super(machineId, DRYER_PRICE, machineStatus);
    }

    // Method overriding: Polymorphism
    // Provides the implementation of the abstract method in Machine.
    @Override
    public String getMachineType() { return "Dryer"; }
}