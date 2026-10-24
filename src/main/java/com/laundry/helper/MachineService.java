package com.laundry.helper;

import java.util.List;

import com.laundry.machineclass.Machine;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.MachineUsage;

// Machine Usage
public class MachineService {

    public int calculateMachinesRequired(double totalWeightKg) {
        return (int) Math.ceil(totalWeightKg / Machine.getMaxCapacity());
    }

    public double calculateOrderPrice(int machinesUsed, double machinePrice) {
        return machinesUsed * machinePrice;
    }

    public double calculateLaundryLoad(double totalWeightKg, int machinesUsed) {
        if (machinesUsed == 0) {
            return 0;
        }
        return totalWeightKg / machinesUsed;
    }
}
