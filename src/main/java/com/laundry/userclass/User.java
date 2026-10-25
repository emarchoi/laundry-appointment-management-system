package com.laundry.userclass;

/*
    ABSTRACT CLASS showing a base user.
    Inherited by Customer and Staff classes.
*/
public abstract class User {

    private String firstName;
    private String lastName;

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    
    public String getFirstName(){ return firstName; }
    public String getLastName(){ return lastName; }
    
    public void setFirstName(String firstName){ this.firstName = firstName; }
    public void setLastName(String lastName){ this.lastName = lastName; }

    public String getFullName(){ return firstName + " " + lastName; }
}
