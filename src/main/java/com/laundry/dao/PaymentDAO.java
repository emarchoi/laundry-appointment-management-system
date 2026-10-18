package com.laundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.Cash;
import com.laundry.systemclass.DigitalPayment;
import com.laundry.systemclass.Payment;

public class PaymentDAO {

    // 1. CREATE INITIAL PAYMENT RECORD
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

    // 2. GET PAYMENT BY APPOINTMENT ID
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

    // 3. UPDATE PAYMENT STATUS (When customer pays)
    public boolean updatePayment(Payment payment, String method) {
        String q = "UPDATE Payment SET AmountPaid = ?, PaymentStatus = ?, PaymentMethod = ? WHERE PaymentID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setDouble(1, payment.getAmountPaid());
            s.setString(2, payment.getPaymentStatus());
            s.setString(3, method);
            s.setInt(4, payment.getPaymentId());
            
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Update Payment): " + e.getMessage());
            return false;
        }
    }
}