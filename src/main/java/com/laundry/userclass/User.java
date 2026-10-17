package com.laundry.userclass;

// Abstract class: User (Inherited by Customer and Staff class)
public abstract class User {

    private String firstName;
    private String lastName;

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName(){ return firstName; }
    public String getLastName(){ return lastName; }
    
    public void setFirstName(String firstName){ this.firstName = firstName; }
    public void setLastName(String lastName){ this.lastName = lastName; }

    public String getFullName(){ return firstName + " " + lastName; }
}
