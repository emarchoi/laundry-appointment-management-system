package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.LaundryOrder;

import java.sql.*;
import java.util.*;

public class LaundryOrderDAO {

    public boolean addOrder(LaundryOrder order) {

        // 1. Add Order
        String q = "INSERT INTO LaundryOrder (OrderID, AppointmentID, LaundryService, "
                + "LaundryTotalKg, OrderTotalPrice, MachinesUsed) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            PreparedStatement s = conn.prepareStatement(q);

            s.setInt(1, order.getOrderId());
            s.setInt(2, order.getAppointmentId());
            s.setString(3, order.getLaundryService());
            s.setDouble(4, order.getLaundryTotalKg());
            s.setDouble(5, order.getOrderTotalPrice());
            s.setInt(6, order.getMachinesUsed());

            return s.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database Error (Add Order): " + e.getMessage());
            return false;
        }
    }

    // 2. Get Orders by AppointmentID
    public List<LaundryOrder> getOrdersOfAppointment(int appointmentId) {

        List<LaundryOrder> orders = new ArrayList<>();
        String q = "SELECT * FROM LaundryOrder "
                + "WHERE AppointmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            PreparedStatement s = conn.prepareStatement(q);

            s.setInt(1, appointmentId);

            try (ResultSet rs = s.executeQuery()) {
                while(rs.next()) {

                    LaundryOrder order = new LaundryOrder(
                        rs.getInt("OrderID"), 
                        rs.getInt("AppointmentID"),
                        rs.getString("LaundryService")
                    );
                    order.setLaundryTotalKg(rs.getDouble("LaundryTotalKg"));
                    order.setOrderTotalPrice(rs.getDouble("OrderTotalPrice"));
                    order.setMachinesUsed(rs.getInt("MachinesUsed"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (Get Orders: " + e.getMessage());
        }
    }
}