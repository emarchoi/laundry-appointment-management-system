package com.laundry.machineclass;

public class Washer extends Machine {

    private static final double WASHER_PRICE = 50.0;

    public Washer(int machineId, String machineStatus) {
        super(machineId, WASHER_PRICE, machineStatus);
    }

    @Override
    public String getMachineType() { return "Washer"; }
}