package com.laundry.util;

import com.laundry.dao.CustomerDAO;
import com.laundry.userclass.Customer;

public class CustomerService {
    private CustomerDAO customerDAO = new CustomerDAO();

    public boolean registerCustomer(Customer customer) {
        
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().length() != 11) {
            throw new IllegalArgumentException("Phone number must be exactly 11 digits.");
        }
        return customerDAO.addCustomer(customer);
    }
}