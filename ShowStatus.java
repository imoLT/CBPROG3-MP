import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class ShowStatus {

    public static void showRoomStatus(int idNum) {
        JFrame frame = new JFrame("Room Status");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);  // Adjust size as needed
        frame.setLayout(new BorderLayout());

        // Table panel
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Reservation ID");
        model.addColumn("Booking Date");
        model.addColumn("Time Slot");
        model.addColumn("Room Name");
        model.addColumn("Room Category");
        model.addColumn("Status");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Load room bookings
        loadRoomStatus(model, idNum);

        frame.setVisible(true);  // Display the frame
    }

    // Method to load the professor's room bookings into the table (both approved and unapproved)
    private static void loadRoomStatus(DefaultTableModel model, int idNum) {
        String sql = "SELECT id, booking_date, time_slot, room_name, room_category, 'Approved' AS status FROM approvedBookings WHERE professor_id = ? "
                   + "UNION "
                   + "SELECT id, booking_date, time_slot, room_name, room_category, 'Unapproved' AS status FROM unapproveBookings WHERE professor_id = ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, idNum);  // Set the professor's ID as parameter
            stmt.setInt(2, idNum);  // Set the professor's ID again for the second query

            ResultSet rs = stmt.executeQuery();

            // Fetch and add data to the table
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));  // Reservation ID
                row.add(rs.getDate("booking_date"));  // Booking Date
                row.add(rs.getString("time_slot"));  // Time Slot
                row.add(rs.getString("room_name"));  // Room Name
                row.add(rs.getString("room_category"));  // Room Category
                row.add(rs.getString("status"));  // Booking status (Approved/Unapproved)
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading room status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
