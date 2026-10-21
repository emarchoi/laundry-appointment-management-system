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
        String q = "INSERT INTO Appointment (AppointmentID, ReferenceNumber, AppointmentType, "
                + "AppointmentTotalPrice, AppointmentStatus, AppointmentDate, EstimatedCompletionFinished) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement s = conn.prepareStatement(q)) {

            s.setInt(1, appointment.getAppointmentId());
            s.setString(2, appointment.getReferenceNumber());
            s.setString(3, appointment.getAppointmentType());
            s.setDouble(4, appointment.getAppointmentTotalPrice());
            s.setString(5, appointment.getAppointmentStatus());
            s.setDate(6, Date.valueOf(appointment.getAppointmentDate()));

            if (appointment.getECF() == 0) {
                s.setNull(7, Types.INTEGER);
            } else {
                s.setInt(7, appointment.getECF());
            }

            return s.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database Error (Create Appointment): " + e.getMessage());
            return false;
        }
    }

    // 2. GET APPOINTMENT BY REFERENCE NUMBER
    public List<Appointment> getAppointmentByReference(String referenceNumber) {

        List<Appointment> appointments = new ArrayList<>();
        String q = "SELECT * FROM Appointment "
                + "WHERE ReferenceNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, referenceNumber);

            try (ResultSet rs = s.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("AppointmentID");
                    String type = rs.getString("AppointmentType");
                    LocalDate date = rs.getDate("AppointmentDate").toLocalDate();

                    Appointment appointment
                            = new Appointment(id, referenceNumber, type, date);

                    appointment.setAppointmentStatus(
                            rs.getString("AppointmentStatus"));

                    // Safely handle nullable StaffID
                    int staffId = rs.getInt("StaffID");
                    if (!rs.wasNull()) {
                        appointment.setStaffId(staffId);
                    }

                    // Safely handle nullable ECF
                    int ecf = rs.getInt("EstimatedCompletionFinished");
                    if (!rs.wasNull()) {
                        appointment.setECF(ecf);
                    }

                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Database Error (Get Appointment by Reference): "
                    + e.getMessage());
        }
        return appointments;
    }

    

    // 4. FULL UPDATE (Used by Attendant/Rider to update Status, StaffID, Price, and ECF)
    public boolean updateAppointment(Appointment appointment) {
        String q = "UPDATE Appointment SET StaffID = ?, AppointmentStatus = ?, "
                        + "AppointmentTotalPrice = ?, EstimatedCompletionFinished = ? "
                        + "WHERE AppointmentID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            // Safely set StaffID to NULL if unassigned
            if (appointment.getStaffId() == null) {
                s.setNull(1, Types.INTEGER);
            } else {
                s.setInt(1, appointment.getStaffId());
            }
            
            s.setString(2, appointment.getAppointmentStatus());
            s.setDouble(3, appointment.getAppointmentTotalPrice());
            
            // Safely set ECF to NULL if it is 0
            if (appointment.getECF() == 0) {
                s.setNull(4, Types.INTEGER);
            } else {
                s.setInt(4, appointment.getECF());
            }
            
            s.setInt(5, appointment.getAppointmentId());
            
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Update Appointment): " + e.getMessage());
            return false;
        }
    }
}
