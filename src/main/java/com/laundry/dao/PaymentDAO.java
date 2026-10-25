package com.laundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.Cash;
import com.laundry.systemclass.DigitalPayment;
import com.laundry.systemclass.Payment;

/*
    Data Access Object (DAO) for managing Payment records in the database
*/
public class PaymentDAO {

    // Inserts a new payment record into the database
    // FOLLOWS CRUD - Create and Update
    public boolean createPayment(Payment payment, String paymentMethod) {
        String q = "INSERT INTO Payment (PaymentID, AppointmentID, "
                        + "AmountPaid, PaymentMethod, PaymentStatus) "
                        + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setInt(1, payment.getPaymentId());
            s.setInt(2, payment.getAppointmentId());
            s.setDouble(3, payment.getAmountPaid());
            s.setString(4, paymentMethod);
            s.setString(5, payment.getPaymentStatus());
            
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Create Payment): " + e.getMessage());
            return false;
        }
    }

    // Retrieves payment details for a specific appointment ID
    // Used by AppointmentService to attach details to Appointment
    // FOLLOWS CRUD - Read and Update   
    public Payment getPaymentByAppointment(int appointmentId) {
        String q = "SELECT * FROM Payment WHERE AppointmentID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setInt(1, appointmentId);
            
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("PaymentID");
                    double amountPaid = rs.getDouble("AmountPaid");
                    String method = rs.getString("PaymentMethod");
                    String status = rs.getString("PaymentStatus");
                    
                    // Polymorphism
                    Payment payment;
                    if ("E-Payment".equalsIgnoreCase(method)) {
                        payment = new DigitalPayment(id, appointmentId, 0.0); // 0.0 will be updated by the Service
                    } else {
                        payment = new Cash(id, appointmentId, 0.0);
                    }
                    
                    payment.setAmountPaid(amountPaid);
                    if ("Paid".equalsIgnoreCase(status)) {
                        payment.markPaid();
                    }
                    return payment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Payment): " + e.getMessage());
        }
        return null;
    }
}