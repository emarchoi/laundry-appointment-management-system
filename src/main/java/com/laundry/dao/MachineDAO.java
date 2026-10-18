package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.machineclass.*;

import java.sql.*;
import java.util.*;

public class MachineDAO {

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

    // 2. UPDATE MACHINE STATUS (Available -> In Use)
    public boolean updateMachineStatus(int machineId, String newStatus) {

        String q = "UPDATE Machine "
                + " SET MachineStatus = ? "
                + " WHERE MachineID = ?";
        
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
}
