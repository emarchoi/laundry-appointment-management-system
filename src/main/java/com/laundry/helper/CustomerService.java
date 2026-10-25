package com.laundry.helper;

import com.laundry.dao.CustomerDAO;
import com.laundry.userclass.Customer;

/*
    Helper class that manages customer validation and registration rules
*/
public class CustomerService {

    // Data Access Object for database operations on customer records
    private CustomerDAO customerDAO = new CustomerDAO();

    // Validates and registers a new customer into the database
    public boolean registerCustomer(Customer customer) {
        validateCustomer(customer);
        return customerDAO.addCustomer(customer);
    }

    // Validate method before inserting to the database
    public void validateCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }

        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }

        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()
            || customer.getPhoneNumber().length() < 1
            || customer.getPhoneNumber().length() > 11) {
                throw new IllegalArgumentException("Phone Number must be exactly 11 digits.");
        }

        if (customer.getBarangay() == null || customer.getBarangay().trim().isEmpty()) {
            throw new IllegalArgumentException("Barangay cannot be empty.");
        }

        if (customer.getStreet() == null || customer.getStreet().trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be empty.");
        }

        if (customer.getBlockNo() == null || customer.getBlockNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Block No cannot be empty.");
        }
    }
}