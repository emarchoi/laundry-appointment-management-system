package com.laundry.userclass;

/*
    Represents a laundry attendant who handles most of the system functions.
*/

public class LaundryAttendant extends Staff{

    public LaundryAttendant(int staffId, String firstName, String lastName){
        super(firstName, lastName, staffId);
    }

    // Method Overriiding: Shows Polymorphism
    // Concrete implementation of getStaffType()
    @Override
    public String getStaffType(){
        return "LaundryAttendant";
    }
}