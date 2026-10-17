package com.laundry.modelsuser;

// Abstract Class Staff: Inherited by DeliveryRider and LaundryAttendant
public abstract class Staff extends User{
    private int staffId;

    public Staff(String firstName, String lastName, int staffId){
        super(firstName, lastName);
        this.staffId = staffId;
    }

    public int getStaffId(){ return staffId; }
    public void setStaffId(int staffId){ this.staffId = staffId; }

    public abstract String getStaffType();
}