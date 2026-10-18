package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    // 1. CREATE APPOINTMENT
    public boolean createAppointment(Appointment appointment) {

        String q = "INSERT INTO Appointment (AppointmentID, ReferenceNumber, " 
                        + "AppointmentType, AppointmentDate, AppointmentStatus) " 
                        + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement s = conn.prepareStatement(q)) {

            s.setInt(1, appointment.getAppointmentId());
            s.setString(2, appointment.getReferenceNumber());
            s.setString(3, appointment.getAppointmentType());
            s.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            s.setString(5, appointment.getAppointmentStatus());

            return s.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database Error (Create Appointment): " + e.getMessage());
            return false;
        }
    }
}
