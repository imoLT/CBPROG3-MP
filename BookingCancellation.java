import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class BookingCancellation {

    public static void showCancellation(int idNum) {
        JFrame frame = new JFrame("Booking Cancellation");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500); 
        frame.setLayout(new BorderLayout());  

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());  

        // Table 
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Reservation ID");
        model.addColumn("Reserved On");
        model.addColumn("Time");
        model.addColumn("Room Name");
        model.addColumn("Room Category");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);  

        // Create a panel for label, text field, and button at the bottom of the tab;e
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); 

        JLabel label = new JLabel("Enter reservation ID of the room you want to cancel:");
        bottomPanel.add(label, gbc); 

        gbc.gridy = 1;
        JTextField enterId = new JTextField(15);
        bottomPanel.add(enterId, gbc); 

        gbc.gridy = 2; 
        JButton cancelBookBtn = new JButton("Cancel Booking");
        cancelBookBtn.setName("cancelBookBtn");
        
		cancelBookBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCancelBooking(e, enterId, frame, idNum, table);
            }
        }); 
		
        bottomPanel.add(cancelBookBtn, gbc); 
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadBookings(model, idNum);
        frame.add(panel);  
        frame.setVisible(true);  
    }

    private static void handleCancelBooking(ActionEvent e, JTextField enterId, JFrame frame, int idNum, JTable table) {
        String enteredId = enterId.getText();  
            
        if (enteredId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a reservation ID!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                int reservationId = Integer.parseInt(enteredId);

                boolean success = cancelReservation(reservationId, idNum);
                    
                if (success) {
                    JOptionPane.showMessageDialog(frame, "Booking with ID " + reservationId + " cancelled successfully.");
                    refreshTable(table, idNum);
                } else {
                    JOptionPane.showMessageDialog(frame, "Reservation ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid reservation ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fetches the professor's bookings into the table
    private static void loadBookings(DefaultTableModel model, int idNum) {
        String sql = "SELECT * FROM approvedBookings WHERE professor_id = ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, idNum);  // Set the professor's ID as parameter in the query
            ResultSet rs = stmt.executeQuery();
            
            // Fetch and add data to the table
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));  // Reservation ID
                row.add(rs.getDate("booking_date"));  // Reserved On
                row.add(rs.getString("time_slot"));  // Time
                row.add(rs.getString("room_name"));  // Room Name
                row.add(rs.getString("room_category"));  // Room Category
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to cancel a reservation
    private static boolean cancelReservation(int reservationId, int idNum) {
        String sql = "DELETE FROM approvedBookings WHERE id = ? AND professor_id = ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            stmt.setInt(2, idNum);  // Only the id who made the reservation can cancel
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // The booking is cancelled if more than one row is affected
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error cancelling booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Method to refresh the table after cancellation
    private static void refreshTable(JTable table, int idNum) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);  // Clear the table
        
        loadBookings(model, idNum);  // Reload the professor's bookings
    }
}
