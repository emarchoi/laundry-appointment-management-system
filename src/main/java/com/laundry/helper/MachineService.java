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

    public double calculateOrderPrice(int machinesUsed, Machine machine) {
        return machinesUsed * machine.getBasePrice();
    }

    public double computeLaundryLoad(double totalWeightKg, int machinesUsed) {
        if (machinesUsed == 0) {
            return 0;
        }
        return totalWeightKg / machinesUsed;
    }

    public void computeAndSetECF(Appointment appointment, List<MachineUsage> machines) {

        int totalMinutes = 0;

        for(MachineUsage machine : machines) {

            totalMinutes += machine.getLaundryDuration();
        }

        appointment.setECF(totalMinutes);
    }
}
