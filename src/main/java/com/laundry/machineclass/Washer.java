package com.laundry.machineclass;

public class Washer extends Machine {
    public Washer(int machineId, double basePrice, String machineStatus) {
        super(machineId, basePrice, machineStatus);
    }

    @Override
    public String getMachineType() { return "Washer"; }
}