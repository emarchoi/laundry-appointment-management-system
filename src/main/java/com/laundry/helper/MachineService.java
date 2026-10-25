package com.laundry.helper;

import com.laundry.machineclass.Machine;

/*
    Helper class that handles machine calculations, weight validation, 
    and machine availability checks

    Used in Weighing and Assignment - AttendantMenu
*/
public class MachineService {

    // Calculation of Machines Required
    public int calculateMachinesRequired(double totalWeightKg) {
        return (int) Math.ceil(totalWeightKg / Machine.getMaxCapacity());
    }

    // Calculation of Order Price based on the Machines Used and Machine Base Price
    public double calculateOrderPrice(int machinesUsed, double machinePrice) {
        return machinesUsed * machinePrice;
    }

    // Evenly distributed laundry load for all machines used
    public double calculateLaundryLoad(double totalWeightKg, int machinesUsed) {
        if (machinesUsed == 0) {
            return 0;
        }
        return totalWeightKg / machinesUsed;
    }
}
