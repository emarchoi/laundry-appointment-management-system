package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.Appointment;
import com.laundry.systemclass.Cash;
import com.laundry.systemclass.DigitalPayment;
import com.laundry.systemclass.Payment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
    Data Access Object (DAO) for managing Appointment records in the database.
*/
public class AppointmentDAO {

    // Inserts a new Appointment record into the database
    // Follows CRUD - Create
    public boolean createAppointment(Appointment appointment) {
        String q = "INSERT INTO Appointment (AppointmentID, ReferenceNumber, AppointmentType, "
                + "AppointmentTotalPrice, AppointmentStatus, AppointmentDate, EstimatedCompletionFinished) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement s = conn.prepareStatement(q)) {

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

    // Shortcut for retrieval from the database
    // Helper method to map a single database ResultSet row into the appointment
    private Appointment appointmentHelper(ResultSet rs) throws SQLException {
        int id = rs.getInt("AppointmentID");
        String refNum = rs.getString("ReferenceNumber");
        String type = rs.getString("AppointmentType");
        LocalDate date = rs.getDate("AppointmentDate").toLocalDate();

        Appointment appt = new Appointment(id, refNum, type, date);
        appt.setAppointmentStatus(rs.getString("AppointmentStatus"));

        int staffId = rs.getInt("StaffID");
        if (!rs.wasNull()) {
            appt.setStaffId(staffId);
        }

        int ecf = rs.getInt("EstimatedCompletionFinished");
        if (!rs.wasNull()) {
            appt.setECF(ecf);
        }

        double totalPrice = rs.getDouble("AppointmentTotalPrice");
        appt.setAppointmentTotalPrice(totalPrice);

        // Attach Payment object if present in DB
        int paymentId = rs.getInt("PaymentID");
        if (!rs.wasNull() && paymentId > 0) {
            String pStatus = rs.getString("PaymentStatus");
            String pMethod = rs.getString("PaymentMethod");
            double pAmount = rs.getDouble("AmountPaid");

            // Shows Polymorphism 
            // Payment reference and DigitalPayment / Cash object
            Payment payment;
            if ("E-Payment".equalsIgnoreCase(pMethod)) {
                payment = new DigitalPayment(paymentId, id, 0.0);
            } else {
                payment = new Cash(paymentId, id, 0.0);
            }
            payment.setAmountPaid(pAmount);
            if ("Paid".equalsIgnoreCase(pStatus)) {
                payment.markPaid();
            }
            appt.setPayment(payment);
        }

        return appt;
    }

    // Fetches appointments matching a specific reference number
    // Used by the AppointmentService to get a full details of the Appointment
    // Follows CRUD - Read
    public List<Appointment> getAppointmentsByReference(String referenceNumber) {
        List<Appointment> appointments = new ArrayList<>();
        String q = "SELECT a.*, p.PaymentID, p.AmountPaid, p.PaymentMethod, p.PaymentStatus " +
                       "FROM Appointment a " +
                       "LEFT JOIN Payment p ON a.AppointmentID = p.AppointmentID " +
                       "WHERE a.ReferenceNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, referenceNumber);

            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    appointments.add(appointmentHelper(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Appointments by Reference): " + e.getMessage());
        }
        return appointments;
    }

    // Fetches full appointments details with a Pending status
    // Follows CRUD - Read
    public List<Appointment> getPendingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String q = "SELECT a.*, p.PaymentID, p.AmountPaid, p.PaymentMethod, p.PaymentStatus " +
                       "FROM Appointment a " +
                       "LEFT JOIN Payment p ON a.AppointmentID = p.AppointmentID " +
                       "WHERE a.AppointmentStatus = ? " +
                       "ORDER BY a.AppointmentID DESC";

        String pendStatus = "Pending";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, pendStatus);

            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    appointments.add(appointmentHelper(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Appointments by Status): " + e.getMessage());
        }
        return appointments;
    }

    // Fetches full appointment details
    // Used for retrieving all appointments in the database
    // Follows CRUD - Read
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String q = "SELECT a.*, p.PaymentID, p.AmountPaid, p.PaymentMethod, p.PaymentStatus " +
                       "FROM Appointment a " +
                       "LEFT JOIN Payment p ON a.AppointmentID = p.AppointmentID " +
                       "ORDER BY a.AppointmentID DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q);
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                appointments.add(appointmentHelper(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get All Appointments): " + e.getMessage());
        }
        return appointments;
    }

    // Retrives full delivery appointment details
    // Used in the DeliveryRider Menu
    // Follows CRUD - Read
    public List<Appointment> getDeliveryAppointment() {
        List<Appointment> appointments = new ArrayList<>();
        String q = "SELECT a.*, p.PaymentID, p.AmountPaid, p.PaymentMethod, p.PaymentStatus " +
                       "FROM Appointment a " +
                       "LEFT JOIN Payment p ON a.AppointmentID = p.AppointmentID " +
                       "WHERE a.AppointmentType = ? " +
                       "ORDER BY a.AppointmentID DESC";

        String deliveryApt = "Delivery";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, deliveryApt);

            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    appointments.add(appointmentHelper(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Appointments by Type): " + e.getMessage());
        }
        return appointments;
    }

    // Full Update (Used by Attendant/Rider to update Status, StaffID, Price, and ECF)
    // Follows CRUD - Update
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

    // Bulk Cleanup of Cancelled Appointments
    // Deletion is in order based on the Foreign Key
    // Follows CRUD - Delete
    public boolean clearCancelledAppointments() {
        String deletePaymentSql = "DELETE FROM Payment WHERE AppointmentID IN " +
                                  "(SELECT AppointmentID FROM Appointment WHERE AppointmentStatus = 'Cancelled')";
        
        String deleteUsageSql   = "DELETE FROM MachineUsage WHERE OrderID IN " +
                                  "(SELECT OrderID FROM LaundryOrder WHERE AppointmentID IN " +
                                  "(SELECT AppointmentID FROM Appointment WHERE AppointmentStatus = 'Cancelled'))";
        
        String deleteOrdersSql  = "DELETE FROM LaundryOrder WHERE AppointmentID IN " +
                                  "(SELECT AppointmentID FROM Appointment WHERE AppointmentStatus = 'Cancelled')";
        
        String deleteApptSql    = "DELETE FROM Appointment WHERE AppointmentStatus = 'Cancelled'";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement s1 = conn.prepareStatement(deletePaymentSql);
                 PreparedStatement s2 = conn.prepareStatement(deleteUsageSql);
                 PreparedStatement s3 = conn.prepareStatement(deleteOrdersSql);
                 PreparedStatement s4 = conn.prepareStatement(deleteApptSql)) {

                s1.executeUpdate();
                s2.executeUpdate();
                s3.executeUpdate();
                int rowsDeleted = s4.executeUpdate();

                conn.commit(); 
                return rowsDeleted > 0;
            } catch (SQLException e) {
                conn.rollback(); 
                System.err.println("Database Error (Rollback Delete Cancelled): " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Delete Cancelled Appointments): " + e.getMessage());
            return false;
        }
    }
}
