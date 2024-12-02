// Other imports and class declarations
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ModifyRegularSchedules {
    public static void displayRegularSchedules() {
        JFrame scheduleFrame = new JFrame("Modify Regular Schedules");
        scheduleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        scheduleFrame.setSize(800, 400);
        scheduleFrame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Regular Schedules:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        String[] columnNames = {"ID", "Professor ID", "Professor Name", "Schedule Dates", "Time Slot", "Room Name"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable scheduleTable = new JTable(tableModel);

        // Fetch schedules from the database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM regularSchedules")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int professorId = rs.getInt("professor_id");
                String professorName = rs.getString("professor_name");
                String scheduleDates = rs.getString("schedule_dates");
                String timeSlot = rs.getString("time_slot");
                String roomName = rs.getString("room_name");
                tableModel.addRow(new Object[]{id, professorId, professorName, scheduleDates, timeSlot, roomName});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(scheduleFrame, "Error fetching schedules from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(scheduleTable);

        JButton modifyButton = new JButton("Modify Selected Schedule");
        modifyButton.addActionListener(e -> {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow != -1) {
                int scheduleId = (int) tableModel.getValueAt(selectedRow, 0);
                int professorId = (int) tableModel.getValueAt(selectedRow, 1);
                String scheduleDates = (String) tableModel.getValueAt(selectedRow, 3);
                String timeSlot = (String) tableModel.getValueAt(selectedRow, 4);
                String roomName = (String) tableModel.getValueAt(selectedRow, 5);

                try {
                    List<LocalDate> parsedDates = new ArrayList<>();
                    if (scheduleDates != null && !scheduleDates.trim().isEmpty()) {
                        for (String date : scheduleDates.split(",")) {
                            parsedDates.add(LocalDate.parse(date.trim()));
                        }
                    }

                    if (parsedDates.isEmpty()) {
                        parsedDates.add(LocalDate.now());
                    }

                    launchRoomScheduling(scheduleId, professorId, parsedDates, timeSlot, roomName);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(scheduleFrame, "Error parsing schedule dates: " + ex.getMessage(), "Invalid Data", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(scheduleFrame, "Please select a schedule to modify.");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(modifyButton);

        scheduleFrame.add(label, BorderLayout.NORTH);
        scheduleFrame.add(scrollPane, BorderLayout.CENTER);
        scheduleFrame.add(buttonPanel, BorderLayout.SOUTH);

        scheduleFrame.setVisible(true);
    }

    private static void launchRoomScheduling(int scheduleId, int professorId, List<LocalDate> currentDates, String currentTimeSlot, String currentRoomName) {
        // Set the current details for modification
        RoomScheduling.setSelectedDates(currentDates);
        RoomScheduling.setSelectedTimeSlot(currentTimeSlot);
        RoomScheduling.setSelectedRoom(currentRoomName);
    
        // Open the RoomScheduling program
        RoomScheduling.roomBookMain(professorId, (updatedDates, updatedTimeSlot, updatedRoomName) -> {
            updateRegularSchedule(scheduleId, updatedDates, updatedTimeSlot, updatedRoomName);
        });
        
    }
    
    
    private static void updateRegularSchedule(int scheduleId, List<LocalDate> updatedDates, String updatedTimeSlot, String updatedRoomName) {
        System.out.println("Attempting to update schedule with ID: " + scheduleId);
        System.out.println("Updated Dates: " + updatedDates);
        System.out.println("Time Slot: " + updatedTimeSlot);
        System.out.println("Room Name: " + updatedRoomName);
    
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca")) {
            // Check if the schedule ID exists
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM regularSchedules WHERE id = ?")) {
                checkStmt.setInt(1, scheduleId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("Schedule ID " + scheduleId + " does not exist. Aborting update.");
                    JOptionPane.showMessageDialog(null, "Schedule does not exist. Unable to update.");
                    return;
                }
            }
    
            // Proceed with the update if the ID exists
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE regularSchedules SET schedule_dates = ?, time_slot = ?, room_name = ? WHERE id = ?")) {
    
                String updatedDatesStr = String.join(",", updatedDates.stream().map(LocalDate::toString).toArray(String[]::new));
                pstmt.setString(1, updatedDatesStr);
                pstmt.setString(2, updatedTimeSlot);
                pstmt.setString(3, updatedRoomName);
                pstmt.setInt(4, scheduleId);
    
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Schedule updated successfully. Rows affected: " + rowsAffected);
                    JOptionPane.showMessageDialog(null, "Schedule updated successfully!");
                    displayRegularSchedules(); // Refresh the displayed schedules
                } else {
                    System.err.println("No rows were updated for schedule ID: " + scheduleId);
                    JOptionPane.showMessageDialog(null, "No rows updated. Schedule might not exist.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating schedule with ID: " + scheduleId);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating schedule. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
