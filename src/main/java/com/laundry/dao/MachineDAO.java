package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.machineclass.*;

import java.sql.*;
import java.util.*;

public class MachineDAO {

    // Helper Class for Machine Details with Usage
    public static class MachineUsageDetail {
        private int machineId;
        private String machineType;
        private double laundryLoad;
        private String assignedTo;
        private String machineStatus;
        private int duration;

        public MachineUsageDetail(int machineId, String machineType, double laundryLoad, String assignedTo, String machineStatus, int duration) {
            this.machineId = machineId;
            this.machineType = machineType;
            this.laundryLoad = laundryLoad;
            this.assignedTo = assignedTo;
            this.machineStatus = machineStatus;
            this.duration = duration;
        }

        public int getMachineId() { return machineId; }
        public String getMachineType() { return machineType; }
        public double getLaundryLoad() { return laundryLoad; }
        public String getAssignedTo() { return assignedTo; }
        public String getMachineStatus() { return machineStatus; }
        public int getDuration() { return duration; }
    }

    // 1. GET ALL MACHINES (Enter Machine Type: Washer / Dryer)
    public List<Machine> getMachinesByType(String machineType) {

        List<Machine> machines = new ArrayList<>();
        String q = "SELECT * FROM Machine WHERE MachineType = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, machineType);

            try (ResultSet rs = s.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("MachineID");
                    String status = rs.getString("MachineStatus");

                    // Polymorphism
                    if ("Washer".equalsIgnoreCase(machineType)) {
                        machines.add(new Washer(id, status));
                    } else if ("Dryer".equalsIgnoreCase(machineType)) {
                        machines.add(new Dryer(id, status));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Machines): " + e.getMessage());
        }
        return machines;
    }

    // GET SINGLE MACHINE BY ID
    public Machine getMachineById(int machineId) {
        String query = "SELECT * FROM Machine WHERE MachineID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, machineId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("MachineType");
                    String status = rs.getString("MachineStatus");
                    
                    if ("Washer".equalsIgnoreCase(type)) {
                        return new Washer(machineId, status);
                    } else if ("Dryer".equalsIgnoreCase(type)) {
                        return new Dryer(machineId, status);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Machine By ID): " + e.getMessage());
        }
        return null;
    }

    // 2. UPDATE MACHINE STATUS (Available -> In Use)
    public boolean updateMachineStatus(int machineId, String newStatus) {

        String q = "UPDATE Machine "
                + "SET MachineStatus = ? "
                + "WHERE MachineID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement s = conn.prepareStatement(q)) {

            s.setString(1, newStatus);
            s.setInt(2, machineId);

            return s.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database Error (Update Machine) " + e.getMessage());
            return false;
        }
    }

    // 3. GET MACHINES WITH USAGE DETAILS FILTERED BY TYPE ("Washer" or "Dryer")
    public List<MachineUsageDetail> getMachineAssignmentDetails(String machineType) {
        List<MachineUsageDetail> machUsageDet = new ArrayList<>();
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
                    
                    double load = rs.getDouble("LaundryLoadKg");
                    if (rs.wasNull()) load = 0.0;
                    
                    int orderId = rs.getInt("OrderID");
                    String assignedTo = (rs.wasNull() || orderId == 0) ? "None" : "Order #" + orderId;
                    
                    int duration = rs.getInt("LaundryDuration");
                    if (rs.wasNull()) duration = 0;
                    
                    machUsageDet.add(new MachineUsageDetail(id, type, load, assignedTo, status, duration));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Machine Details): " + e.getMessage());
        }
        return machUsageDet;
    }
}


