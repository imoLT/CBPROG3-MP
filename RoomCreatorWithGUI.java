import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RoomCreatorWithGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomCreatorWithGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Room Creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        // Components for room category
        JLabel categoryLabel = new JLabel("Room Category:");
        String[] categories = {"Classroom", "Laboratory"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);

        // Components for room name
        JLabel roomNameLabel = new JLabel("Room Name:");
        JTextField roomNameField = new JTextField();

        // Components for max capacity
        JLabel maxCapacityLabel = new JLabel("Max Capacity:");
        JTextField maxCapacityField = new JTextField();

        // Components for tags
        JLabel tagsLabel = new JLabel("Tags (comma-separated):");
        JTextField tagsField = new JTextField();

        // Submit button
        JButton submitButton = new JButton("Create Room");
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        // Add components to the frame
        frame.add(categoryLabel);
        frame.add(categoryComboBox);

        frame.add(roomNameLabel);
        frame.add(roomNameField);

        frame.add(maxCapacityLabel);
        frame.add(maxCapacityField);

        frame.add(tagsLabel);
        frame.add(tagsField);

        frame.add(submitButton);
        frame.add(statusLabel);

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = categoryComboBox.getSelectedItem().toString().toLowerCase();
                String roomName = roomNameField.getText().trim();
                String maxCapacityText = maxCapacityField.getText().trim();
                String tags = tagsField.getText().trim();

                if (roomName.isEmpty() || maxCapacityText.isEmpty() || tags.isEmpty()) {
                    statusLabel.setText("All fields are required!");
                    return;
                }

                try {
                    int maxCapacity = Integer.parseInt(maxCapacityText);

                    // Save to database
                    if (saveRoomToDatabase(category, roomName, maxCapacity, tags)) {
                        statusLabel.setText("Room created successfully!");
                        clearFields(roomNameField, maxCapacityField, tagsField);
                    } else {
                        statusLabel.setText("Failed to create room.");
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Max capacity must be a number.");
                }
            }
        });

        // Show the frame
        frame.setVisible(true);
    }

    // Modify this method to use DatabaseHelper for getting the connection
    private static boolean saveRoomToDatabase(String category, String roomName, int maxCapacity, String tags) {
        String insertQuery = "INSERT INTO rooms (category, room_name, max_capacity, tags) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();  // Use DatabaseHelper to get the connection
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, category);
            pstmt.setString(2, roomName);
            pstmt.setInt(3, maxCapacity);
            pstmt.setString(4, tags);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void clearFields(JTextField roomNameField, JTextField maxCapacityField, JTextField tagsField) {
        roomNameField.setText("");
        maxCapacityField.setText("");
        tagsField.setText("");
    }
}
