package com.laundry.userclass;

public class Customer extends User{

    private String referenceNumber;
    private String phoneNumber;
    private String barangay;
    private String street;
    private String blockNo;

    public Customer(String firstName, String lastName, String referenceNumber, String phoneNumber, 
        String barangay, String street, String blockNo){
        super(firstName, lastName);
        this.referenceNumber = referenceNumber;
        this.phoneNumber = phoneNumber;
        this.barangay = barangay;
        this.street = street;
        this.blockNo = blockNo;
    }

    public String getReferenceNumber(){ return referenceNumber; }
    public String getPhoneNumber(){ return phoneNumber; }
    public String getBarangay(){ return barangay; }
    public String getStreet() { return street; }
    public String getBlockNo() { return blockNo; }
    
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBarangay(String barangay){ this.barangay = barangay; }
    public void setStreet(String street){ this.street = street; }
    public void setBlockNo(String blockNo) { this.blockNo = blockNo; }
}