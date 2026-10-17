package com.laundry.modelsuser;

public class DeliveryRider extends Staff{

    public DeliveryRider(int staffId, String firstName, String lastName){
        super(firstName, lastName, staffId);
    }

    @Override
    public String getStaffType(){
        return "DeliveryRider";
    }
}