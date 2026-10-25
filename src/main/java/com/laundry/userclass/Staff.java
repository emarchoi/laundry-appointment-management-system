package com.laundry.userclass;

/**
    An abstract base class for all staff.
    It extends the User class to get First Name and Last Name.
    And is inherited by LaundryAttendant and DeliveryRider.
*/
public abstract class Staff extends User{
    private int staffId;

    public Staff(String firstName, String lastName, int staffId){
        super(firstName, lastName);
        this.staffId = staffId;
    }

    // Getter and Setter for the DAO
    public int getStaffId(){ return staffId; }
    public void setStaffId(int staffId){ this.staffId = staffId; }

    // Abstract method, body is implemented by child class.
    // Returns the staff role name ("LaundryAttendant" or "DeliveryRider").
    public abstract String getStaffType();
}