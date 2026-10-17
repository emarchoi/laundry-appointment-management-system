package com.laundry.models;

public class Customer {
    private String referenceNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String barangay;
    private String street;
    private String blockNo;
    
    public String getReferenceNumber(){return referenceNumber;}
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getPhoneNumber(){return phoneNumber;}
    public String getBarangay(){return barangay;}
    public String getStreet(){return street;}
    public String getBlockNo(){return blockNo;}

    public void setReferenceNumber(String referenceNumber){this.referenceNumber = referenceNumber;}
    public void setFirstName(String firstName){this.firstName = firstName;}
    public void setLastName(String lastName){this.lastName = lastName;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
    public void setBaranagay(String barangay){this.barangay = barangay;}
    public void setStreet(String street){this.street = street;}
    public void setBlockNo(String blockNo){this.blockNo = blockNo;}
}