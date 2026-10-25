package com.laundry.machineclass;

// Washer machine class with a fixed price of PHP 50.00 (Client)


public class Washer extends Machine {

    private static final double WASHER_PRICE = 50.0;

    public Washer(int machineId, String machineStatus) {
        super(machineId, WASHER_PRICE, machineStatus);
    }

    // Method overriding: Polymorphism
    // Provides the implementation of the abstract method in Machine.
    @Override
    public String getMachineType() { return "Washer"; }
}