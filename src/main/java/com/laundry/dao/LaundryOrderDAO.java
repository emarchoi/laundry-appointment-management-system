package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.LaundryOrder;

import java.sql.*;
import java.util.*;

public class LaundryOrderDAO {

    // 1. ADD NEW ORDER
    public boolean addOrder(LaundryOrder order) {
        String q = "INSERT INTO LaundryOrder (OrderID, AppointmentID, LaundryService, "
                + "LaundryTotalKg, OrderTotalPrice, MachinesUsed) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(q)) {

            stmt.setInt(1, order.getOrderId());
            stmt.setInt(2, order.getAppointmentId());
            stmt.setString(3, order.getLaundryService());
            stmt.setDouble(4, order.getLaundryTotalKg());
            stmt.setDouble(5, order.getOrderTotalPrice());
            stmt.setInt(6, order.getMachinesUsed());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database Error (Add Order): " + e.getMessage());
            return false;
        }
    }

    // 2. GET ORDERS BY APPOINTMENT ID
    public List<LaundryOrder> getOrdersByAppointment(int appointmentId) {
        List<LaundryOrder> orders = new ArrayList<>();
        String q = "SELECT * FROM LaundryOrder WHERE AppointmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(q)) {

            stmt.setInt(1, appointmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LaundryOrder order = new LaundryOrder(
                            rs.getInt("OrderID"),
                            rs.getInt("AppointmentID"),
                            rs.getString("LaundryService")
                    );
                    order.setLaundryTotalKg(rs.getDouble("LaundryTotalKg"));
                    order.setOrderTotalPrice(rs.getDouble("OrderTotalPrice"));
                    order.setMachinesUsed(rs.getInt("MachinesUsed"));

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Get Orders): " + e.getMessage());
        }
        return orders;
    }

    // 3. UPDATE ORDER (Used by Attendant after weighing laundry)
    public boolean updateOrderDetails(LaundryOrder order) {
        String query = "UPDATE LaundryOrder "
                    + "SET LaundryTotalKg = ?, "
                    + "OrderTotalPrice = ?, "
                    + "MachinesUsed = ? "
                    + "WHERE OrderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setDouble(1, order.getLaundryTotalKg());
            stmt.setDouble(2, order.getOrderTotalPrice());
            stmt.setInt(3, order.getMachinesUsed());
            stmt.setInt(4, order.getOrderId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error (Update Order): " + e.getMessage());
            return false;
        }

    }
}