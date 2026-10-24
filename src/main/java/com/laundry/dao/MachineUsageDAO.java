package com.laundry.dao;

import com.laundry.systemclass.MachineUsage;
import com.laundry.config.DatabaseConnection;
import com.laundry.dao.MachineDAO.MachineUsageDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineUsageDAO {

    // 1. ADD MACHINE USAGE (When Attendant assigns a machine)
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

    // 2. GET MACHINES USED BY AN ORDER
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

    // DELETE MACHINE USAGE RECORD (Frees machine assignment)
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

    public List<MachineUsageDetail> getMachinesWithUsageDetailsByType(String machineType) {
        List<MachineUsageDetail> list = new ArrayList<>();
        String q = "SELECT m.MachineID, m.MachineType, m.MachineStatus, " +
                       "mu.LaundryLoadKg, mu.OrderID, mu.LaundryDuration " +
                       "FROM Machine m " +
                       "LEFT JOIN MachineUsage mu ON m.MachineID = mu.MachineID " +
                       "WHERE m.MachineType = ? " +
                       "ORDER BY m.MachineID ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setString(1, machineType);
            
            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("MachineID");
                    String type = rs.getString("MachineType");
                    String status = rs.getString("MachineStatus");
                    
                    boolean isInUse = "In Use".equalsIgnoreCase(status);
                    
                    double load = isInUse ? rs.getDouble("LaundryLoadKg") : 0.0;
                    int orderId = rs.getInt("OrderID");
                    String assignedTo = (isInUse && !rs.wasNull() && orderId > 0) ? "Order #" + orderId : "None";
                    int duration = isInUse ? rs.getInt("LaundryDuration") : 0;
                    
                    list.add(new MachineUsageDetail(id, type, load, assignedTo, status, duration));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Machine Details): " + e.getMessage());
        }
        return list;
    }
}