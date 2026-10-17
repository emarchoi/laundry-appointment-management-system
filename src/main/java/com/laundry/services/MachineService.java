package com.laundry.services;

import com.laundry.systemclass.LaundryOrder;
import com.laundry.machineclass.*;

// Machine Usage
public class MachineService {

    public int calculateMachinesRequired(double totalWeightKg) {
        return (int) Math.ceil(totalWeightKg / Machine.getMaxCapacity());
    }

    public double calculateOrderPrice(int machinesUsed, Machine machine) {
        return machinesUsed * machine.getBasePrice();
    }

    public double computeLaundryLoad(double totalWeightKg, int machinesUsed) {
        if (machinesUsed == 0) {
            return 0;
        }
        return totalWeightKg / machinesUsed;
    }
}
