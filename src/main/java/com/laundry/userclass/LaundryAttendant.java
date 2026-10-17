package com.laundry.userclass;

public class LaundryAttendant extends Staff{

    public LaundryAttendant(int staffId, String firstName, String lastName){
        super(firstName, lastName, staffId);
    }

    @Override
    public String getStaffType(){
        return "LaundryAttendant";
    }
}