package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.LaundryOrder;

import java.sql.*;
import java.util.*;

/*
    Data Access Object (DAO) for managing LaundryOrder records in the database
*/
public class LaundryOrderDAO {

    // Inserts a new laundry order record into the database
    // Used in Booking, initial order of the Customer
    // FOLLOWS CRUD - Create
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

    // Retrieves all laundry orders linked to a specific appointment ID
    // Used mainly to show summarized orders of an appointment
    // FOLLOWS CRUD - Read
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

    // Updates the weight, price, and machines used for a laundry order
    // (Used by Attendant after weighing laundry)
    // FOLLOWS CRUD - Update
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