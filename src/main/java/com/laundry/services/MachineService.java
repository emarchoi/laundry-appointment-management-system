package com.laundry.services;

import com.laundry.systemclass.LaundryOrder;
import com.laundry.machineclass.*;

// Machine Usage
public class MachineService {

    public int calculateMachinesRequired(double totalWeighKg){
        return (int)Math.ceil(totalWeighKg / Machine.getMaxCapacity());
    }

    public double calculateOrderPrice(int machinesUsed, Machine machine){
        return machinesUsed * machine
    }
}
