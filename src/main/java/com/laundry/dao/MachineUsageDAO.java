package com.laundry.dao;

import com.laundry.systemclass.MachineUsage;
import com.laundry.config.DatabaseConnection;
import com.laundry.dao.MachineDAO.MachineUsageDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
    Data Access Object (DAO) for managing MachineUsage records in the database.
    Connects laundry orders to assigned physical machines
*/

public class MachineUsageDAO {

    // Inserts a new machine usage record linking a laundry order to a physical machine
    // Used when Attendant assigns a machine
    // FOLLOWS CRUD - Create
    public boolean addMachineUsage(MachineUsage usage) {
        String q = "INSERT INTO MachineUsage (OrderID, MachineID, LaundryLoadKg, "
                        + "LaundryStartTime, LaundryDuration) "
                        + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setInt(1, usage.getOrderId());
            s.setInt(2, usage.getMachineId());
            s.setDouble(3, usage.getLaundryLoad());
            s.setTime(4, Time.valueOf(usage.getLaundryStartTime()));
            s.setInt(5, usage.getLaundryDuration());
            
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Add Machine Usage): " + e.getMessage());
            return false;
        }
    }

    // Retrieves all machine usage records associated with a specific laundry order ID
    // Mainly used for machine usage and order tracking
    // Used for releasing assigned machines (In AttendantMenu & DeliveryRiderMenu)
    // Used for computing ecf
    // FOLLOW CRUD - Read
    public List<MachineUsage> getUsageByOrder(int orderId) {
        List<MachineUsage> usages = new ArrayList<>();
        String q = "SELECT * FROM MachineUsage WHERE OrderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setInt(1, orderId);
            
            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    MachineUsage usage = new MachineUsage(
                        rs.getInt("OrderID"),
                        rs.getInt("MachineID"),
                        rs.getDouble("LaundryLoadKg"),
                        rs.getTime("LaundryStartTime").toLocalTime()
                    );
                    usages.add(usage);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Machine Usage): " + e.getMessage());
        }
        return usages;
    }

    // Deletes all machine usage records linked to an order
    // Used to free up assigned machines when an appointment is completed or ready for pickup
    // FOLLOWS CRUD - Update and Delete
    public boolean deleteUsageByOrder(int orderId) {
        String q = "DELETE FROM MachineUsage WHERE OrderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setInt(1, orderId);
            return s.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Delete Machine Usage): " + e.getMessage());
            return false;
        }
    }

}