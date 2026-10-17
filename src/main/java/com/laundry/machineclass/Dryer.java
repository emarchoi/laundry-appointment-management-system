package com.laundry.machineclass;

public class Dryer extends Machine {
    public Dryer(int machineId, double basePrice, String machineStatus) {
        super(machineId, basePrice, machineStatus);
    }

    @Override
    public String getMachineType() { return "Dryer"; }
}