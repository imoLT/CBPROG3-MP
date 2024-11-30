import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RoomEditorWithGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomEditorWithGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Room Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        // Components to select a room
        JLabel selectRoomLabel = new JLabel("Select Room ID:");
        JTextField roomIdField = new JTextField();

        JButton loadButton = new JButton("Load Room");

        // Components for room category
        JLabel categoryLabel = new JLabel("Room Category:");
        JTextField categoryField = new JTextField();

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
        JButton submitButton = new JButton("Update Room");
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        // Add components to the frame
        frame.add(selectRoomLabel);
        frame.add(roomIdField);
        frame.add(loadButton);

        frame.add(categoryLabel);
        frame.add(categoryField);

        frame.add(roomNameLabel);
        frame.add(roomNameField);

        frame.add(maxCapacityLabel);
        frame.add(maxCapacityField);

        frame.add(tagsLabel);
        frame.add(tagsField);

        frame.add(submitButton);
        frame.add(statusLabel);

        // Load button action
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomIdText = roomIdField.getText().trim();

                if (roomIdText.isEmpty()) {
                    statusLabel.setText("Room ID is required!");
                    return;
                }

                try {
                    int roomId = Integer.parseInt(roomIdText);
                    Room room = loadRoomFromDatabase(roomId);

                    if (room != null) {
                        categoryField.setText(room.category);
                        roomNameField.setText(room.name);
                        maxCapacityField.setText(String.valueOf(room.maxCapacity));
                        tagsField.setText(room.tags);
                        statusLabel.setText("Room loaded successfully.");
                    } else {
                        statusLabel.setText("Room not found.");
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Room ID must be a number.");
                }
            }
        });

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomIdText = roomIdField.getText().trim();
                String category = categoryField.getText().trim();
                String roomName = roomNameField.getText().trim();
                String maxCapacityText = maxCapacityField.getText().trim();
                String tags = tagsField.getText().trim();

                if (roomIdText.isEmpty() || category.isEmpty() || roomName.isEmpty() || maxCapacityText.isEmpty() || tags.isEmpty()) {
                    statusLabel.setText("All fields are required!");
                    return;
                }

                try {
                    int roomId = Integer.parseInt(roomIdText);
                    int maxCapacity = Integer.parseInt(maxCapacityText);

                    // Update database
                    if (updateRoomInDatabase(roomId, category, roomName, maxCapacity, tags)) {
                        statusLabel.setText("Room updated successfully!");
                    } else {
                        statusLabel.setText("Failed to update room.");
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Room ID and Max Capacity must be numbers.");
                }
            }
        });

        // Show the frame
        frame.setVisible(true);
    }

    private static Room loadRoomFromDatabase(int roomId) {
        String query = "SELECT * FROM rooms WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getString("category"),
                        rs.getString("room_name"),
                        rs.getInt("max_capacity"),
                        rs.getString("tags")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean updateRoomInDatabase(int roomId, String category, String roomName, int maxCapacity, String tags) {
        String updateQuery = "UPDATE rooms SET category = ?, room_name = ?, max_capacity = ?, tags = ? WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, category);
            pstmt.setString(2, roomName);
            pstmt.setInt(3, maxCapacity);
            pstmt.setString(4, tags);
            pstmt.setInt(5, roomId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Room class for loading room details
    static class Room {
        String category;
        String name;
        int maxCapacity;
        String tags;

        Room(String category, String name, int maxCapacity, String tags) {
            this.category = category;
            this.name = name;
            this.maxCapacity = maxCapacity;
            this.tags = tags;
        }
    }
}
