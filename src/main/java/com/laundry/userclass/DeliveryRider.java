package com.laundry.userclass;

/*
    Represents a delivery rider who handles the delivery functions.
*/

public class DeliveryRider extends Staff{

    public DeliveryRider(int staffId, String firstName, String lastName){
        super(firstName, lastName, staffId);
    }

    // Method Overriiding: Shows Polymorphism
    // Concrete implementation of getStaffType()
    @Override
    public String getStaffType(){
        return "DeliveryRider";
    }
}