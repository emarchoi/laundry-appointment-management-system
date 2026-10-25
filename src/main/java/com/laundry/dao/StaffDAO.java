package com.laundry.dao;

import com.laundry.config.DatabaseConnection;
import com.laundry.userclass.*;

import java.sql.*;

/*
    Data Access Object (DAO) for authenticating staff members in the database
*/
public class StaffDAO {

    // Authenticates a staff member using their Staff ID and Full Name
    // Returns a polymorphic Staff instance (Attendant or DeliveryRider) based on their assigned role
    // FOLLOWS CRUD - Read
    public Staff loginStaff(int staffId, String fullName) {

        String q = "SELECT * FROM Staff "
                + "WHERE StaffID = ? "
                + " AND (FirstName + ' ' + Lastname) = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
            PreparedStatement s = conn.prepareStatement(q)) {

            s.setInt(1, staffId);
            s.setString(2, fullName);

            try (ResultSet rs = s.executeQuery()) {

                if (rs.next()) {

                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String staffType = rs.getString("StaffType");

                    // Polymorphism: Instantiate Attendant or DeliveryRider depending on StaffType value
                    if ("LaundryAttendant".equalsIgnoreCase(staffType)) {
                        return new LaundryAttendant(staffId, firstName, lastName);
                    } else if ("DeliveryRider".equalsIgnoreCase(staffType)) {
                        return new DeliveryRider(staffId, firstName, lastName);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (Staff Login): " + e.getMessage());
        }
        return null;
    }
}
