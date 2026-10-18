import com.laundry.config.DatabaseConnection;
import com.laundry.systemclass.MachineUsage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineUsageDAO {

    // 1. ADD MACHINE USAGE (When Attendant assigns a machine)
    public boolean addMachineUsage(MachineUsage usage) {
        String q = "INSERT INTO MachineUsage (OrderID, MachineID, LaundryLoadKg, "
                        + "PriceCharged, LaundryStartTime, LaundryDuration) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement s = conn.prepareStatement(q)) {
             
            s.setInt(1, usage.getOrderId());
            s.setInt(2, usage.getMachineId());
            s.setDouble(3, usage.getLaundryLoad());
            s.setDouble(4, usage.getPriceCharge());
            s.setTime(5, Time.valueOf(usage.getLaundryStartTime()));
            s.setInt(6, usage.getLaundryDuration());
            
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
                        rs.getDouble("PriceCharged"),
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
}