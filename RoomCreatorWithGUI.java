import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class RoomCreatorWithGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomCreatorWithGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Room Creator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(7, 2, 10, 10)); // Adjust layout to fit additional field

        // Components for room category
        JLabel categoryLabel = new JLabel("Room Category:");
        String[] categories = {"Classroom", "Laboratory"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);

        // Components for building
        JLabel buildingLabel = new JLabel("Building:");
        JTextField buildingField = new JTextField();

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

        frame.add(buildingLabel); // Add building field between category and room name
        frame.add(buildingField);

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
			String category = categoryComboBox.getSelectedItem().toString();
			String building = buildingField.getText().trim();
			String roomName = roomNameField.getText().trim();
			String maxCapacityText = maxCapacityField.getText().trim();
			String tags = tagsField.getText().trim();

			if (roomName.isEmpty() || building.isEmpty() || maxCapacityText.isEmpty() || tags.isEmpty()) {
				statusLabel.setText("All fields are required!");
				return;
			}

			try {
				int maxCapacity = Integer.parseInt(maxCapacityText);

				// Attempt to save the room to the database
				if (saveRoomToDatabase(category, building, roomName, maxCapacity, tags)) {
					statusLabel.setText("Room created successfully!");
					clearFields(buildingField, roomNameField, maxCapacityField, tagsField);
				} else {
					statusLabel.setText("Room already exists! Please try again.");
				}
			} catch (NumberFormatException ex) {
				statusLabel.setText("Max capacity must be a number.");
			}
		}
	});

        // Show the frame
        frame.setVisible(true);
    }
	
	private static boolean roomExists(String building, String roomName) {
		String query = "SELECT COUNT(*) FROM rooms WHERE building = ? AND room_name = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, building);
			pstmt.setString(2, roomName);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0; // If count > 0, the room exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If no room is found, return false
	}

    private static boolean saveRoomToDatabase(String category, String building, String roomName, int maxCapacity, String tags) {
		// Check if the room already exists in the database
		if (roomExists(building, roomName)) {
			return false;
		}

		// If the room doesn't exist, proceed with the insertion
		String insertQuery = "INSERT INTO rooms (category, building, room_name, max_capacity, tags) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

			pstmt.setString(1, category);
			pstmt.setString(2, building);
			pstmt.setString(3, roomName);
			pstmt.setInt(4, maxCapacity);
			pstmt.setString(5, tags);

			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

    private static void clearFields(JTextField buildingField, JTextField roomNameField, JTextField maxCapacityField, JTextField tagsField) {
        buildingField.setText("");
        roomNameField.setText("");
        maxCapacityField.setText("");
        tagsField.setText("");
    }
}
