package com.laundry.machineclass;

public class Dryer extends Machine {

    private static final double DRYER_PRICE = 65.0;

    public Dryer(int machineId, String machineStatus) {
        super(machineId, DRYER_PRICE, machineStatus);
    }

    @Override
    public String getMachineType() { return "Dryer"; }
}