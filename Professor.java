import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Professor extends UniversityRoomBooking implements ActionListener {
    private JButton bookClassBtn, cancelBookBtn, checkBookBtn, callITBtn, callSecBtn, submitITSRequestBtn, logoutBtn, submitSecRequestBtn, regSchedBtn;
    private JFrame frame;
    private int idNum;
	private JComboBox<String> roomField, roomFieldITS;  // Declare roomField as JComboBox
    private JTextField issueDetailField, roomFieldS, SissueDetailField;
    private JLabel roomLabel, issueLabel, issueDetailLabel, roomLabelS, issueLabelS, SissueDetailLabel;
    private JComboBox<String> issueDropdown, issueDropdownS;
    private Connection conn;

    public Professor(int idNum){
        this.idNum = idNum;
        professorMenu();
		
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getIdNum() {
        return this.idNum;
    }

   public void professorMenu() {
		frame = new JFrame("Professor Dashboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 500);
		frame.setLayout(new BorderLayout());

		// Main panel for buttons with GridBagLayout
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JPanel cardPanel = new JPanel(new CardLayout()); // For ITS and Security sections

		// Create GridBagConstraints to manage button layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);  // Padding between components
		gbc.anchor = GridBagConstraints.CENTER;  // Center the buttons
		gbc.fill = GridBagConstraints.HORIZONTAL;  // Make buttons stretch horizontally
		gbc.gridx = 0;  // Column index
		gbc.gridy = 0;  // Row index

		// Initialize buttons and their action listeners
		bookClassBtn = new JButton("Book Room");
		bookClassBtn.setName("bookClassBtn");
		bookClassBtn.addActionListener(this);
		mainPanel.add(bookClassBtn, gbc);

		cancelBookBtn = new JButton("Cancel Booking");
		cancelBookBtn.setName("cancelBookBtn");
		cancelBookBtn.addActionListener(this);
		gbc.gridy = 1;  // Move to the next row
		mainPanel.add(cancelBookBtn, gbc);

		checkBookBtn = new JButton("Check Room Status");
		checkBookBtn.setName("checkBookBtn");
		checkBookBtn.addActionListener(this);
		gbc.gridy = 2;  // Move to the next row
		mainPanel.add(checkBookBtn, gbc);

		regSchedBtn = new JButton("Check my Regular Schedule");
		regSchedBtn.setName("regSchedBtn");
		regSchedBtn.addActionListener(this);
		gbc.gridy = 3;  // Move to the next row
		mainPanel.add(regSchedBtn, gbc);

		logoutBtn = new JButton("Logout");
		logoutBtn.setName("logoutBtn");
		logoutBtn.addActionListener(this);
		gbc.gridy = 4;  // Move to the next row
		mainPanel.add(logoutBtn, gbc);

		callITBtn = new JButton("Call ITS");
		callITBtn.setName("callITBtn");
		callITBtn.addActionListener(e -> showITSSection(cardPanel));
		gbc.gridy = 5;  // Move to the next row
		mainPanel.add(callITBtn, gbc);

		callSecBtn = new JButton("Call Security");
		callSecBtn.setName("callSecBtn");
		callSecBtn.addActionListener(e -> showSecuritySection(cardPanel));
		gbc.gridx = 1;
		gbc.gridy = 5;
		mainPanel.add(callSecBtn, gbc);

		// Initialize the room dropdowns for ITS and Security
		roomField = new JComboBox<>();  // Initialize roomField here
		roomFieldITS = new JComboBox<>();  // Initialize roomFieldITS here

		// Add ITS section to CardLayout
		JPanel itsPanel = new JPanel(new GridBagLayout());
		setupITSPanel(itsPanel);

		// Add Security section to CardLayout
		JPanel securityPanel = new JPanel(new GridBagLayout());
		setupSecurityPanel(securityPanel);

		// Add both panels to the CardLayout
		cardPanel.add(itsPanel, "ITS");
		cardPanel.add(securityPanel, "Security");

		// Add the main panel and card panel to the frame
		frame.add(mainPanel, BorderLayout.NORTH);
		frame.add(cardPanel, BorderLayout.CENTER);

		// Initially, ensure no menu is shown
		((CardLayout) cardPanel.getLayout()).show(cardPanel, "");

		frame.setVisible(true);

		// Populate the room dropdowns
		populateRoomDropdown();
		populateITSRoomDropdown();
	}

	private void setupITSPanel(JPanel itsPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		// Room label and dropdown for ITS panel
		roomLabel = new JLabel("Room:");
		roomFieldITS = new JComboBox<>();  // Ensure roomFieldITS is initialized
		populateRoomDropdown();  // Populate the room dropdown with room names from the database

		gbc.gridx = 0; gbc.gridy = 0; itsPanel.add(roomLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 0; itsPanel.add(roomFieldITS, gbc);

		// Issue label and dropdown
		issueLabel = new JLabel("Issue Description:");
		issueDropdown = new JComboBox<>(new String[]{"Projector assistance", "OS problems", "Installing of applications", "Others: (Please specify)"});
		issueDropdown.addActionListener(this);
		gbc.gridx = 0; gbc.gridy = 1; itsPanel.add(issueLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 1; itsPanel.add(issueDropdown, gbc);

		// Issue details label and text field (visible only for 'Others' option)
		issueDetailLabel = new JLabel("Please specify the issue:");
		issueDetailField = new JTextField(15);
		issueDetailField.setVisible(false);
		issueDetailLabel.setVisible(false);
		gbc.gridx = 0; gbc.gridy = 2; itsPanel.add(issueDetailLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 2; itsPanel.add(issueDetailField, gbc);

		// Submit button for ITS request
		submitITSRequestBtn = new JButton("Submit ITS Request");
		submitITSRequestBtn.setName("submitITSRequestBtn");
		submitITSRequestBtn.addActionListener(this);
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; itsPanel.add(submitITSRequestBtn, gbc);
	}

	private void setupSecurityPanel(JPanel securityPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		// Room label and dropdown
		roomLabel = new JLabel("Room:");
		roomField = new JComboBox<>();  // Instantiate roomField before using it
		populateRoomDropdown();  // Populate the room dropdown with room names from the database

		gbc.gridx = 0; gbc.gridy = 0; securityPanel.add(roomLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 0; securityPanel.add(roomField, gbc);

		// Issue label and dropdown
		issueLabel = new JLabel("Issue Description:");
		issueDropdown = new JComboBox<>(new String[]{"Get TV Remote", "Up or Down Airconditioner", "Others: (Please specify)"});
		issueDropdown.addActionListener(this);
		gbc.gridx = 0; gbc.gridy = 1; securityPanel.add(issueLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 1; securityPanel.add(issueDropdown, gbc);

		// Issue details label and text field (visible only for 'Others' option)
		issueDetailLabel = new JLabel("Please specify the issue:");
		issueDetailField = new JTextField(15);
		issueDetailField.setVisible(false);
		issueDetailLabel.setVisible(false);
		gbc.gridx = 0; gbc.gridy = 2; securityPanel.add(issueDetailLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 2; securityPanel.add(issueDetailField, gbc);

		submitSecRequestBtn = new JButton("Submit Security Request");
		submitSecRequestBtn.setName("submitSecRequestBtn");
		submitSecRequestBtn.addActionListener(this);
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; securityPanel.add(submitSecRequestBtn, gbc);
	}

	private void populateRoomDropdown() {
		String sql = "SELECT room_name FROM rooms";  // Query to fetch room names
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pst = conn.prepareStatement(sql);
			 ResultSet rs = pst.executeQuery()) {

			// Initialize
			roomField.removeAllItems();

			// default value (to be used for error handling)
			roomField.addItem("Select a room");

			// Loop through the result set and add room names to the dropdown
			while (rs.next()) {
				String roomName = rs.getString("room_name");
				roomField.addItem(roomName);  // Add the room name to the combo box
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching rooms: " + ex.getMessage());
		}
	}

    private void showITSSection(JPanel cardPanel) {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "ITS");
    }

    private void showSecuritySection(JPanel cardPanel) {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Security");
    }
	
	private void populateITSRoomDropdown() {
		String sql = "SELECT room_name FROM rooms";  // Query to fetch room names
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pst = conn.prepareStatement(sql);
			 ResultSet rs = pst.executeQuery()) {

			roomFieldITS.removeAllItems();  // Clear previous items

			// Default value
			roomFieldITS.addItem("Select a room");

			// Loop through the result set and add room names to the dropdown
			while (rs.next()) {
				String roomName = rs.getString("room_name");
				roomFieldITS.addItem(roomName);  // Add the room name to the combo box
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching rooms: " + ex.getMessage());
		}
	}

	
	private void submitITSRequest() {
		String issueDescription = (String) issueDropdown.getSelectedItem();

		// Use textfield for "Others"
		if (issueDescription.equals("Others: (Please specify)")) {
			issueDescription = issueDetailField.getText();
		}

		// Validate room selection and issue description
		String room = (String) roomFieldITS.getSelectedItem();  // Get the selected room from the dropdown
		if (room == null || room.equals("Select a room") || issueDescription.isEmpty()) {
			if (room == null || room.equals("Select a room")) {
				JOptionPane.showMessageDialog(frame, "Please select a room!", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(frame, "Please fill in the textbox.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			try {
				String query = "INSERT INTO its_requests (room, issue_description, status) VALUES (?, ?, 'Pending')";
				try (PreparedStatement pst = conn.prepareStatement(query)) {
					pst.setString(1, room);  // Pass the selected room
					pst.setString(2, issueDescription);  // Pass the issue description
					pst.executeUpdate();  // Execute the insert query

					JOptionPane.showMessageDialog(frame, "ITS request submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

					// Reset fields after submission
					roomFieldITS.setSelectedIndex(0); 
					issueDropdown.setSelectedIndex(0);  
					issueDetailField.setText(""); 
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Error submitting its request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void submitSecRequest() {
		String issueDescription = (String) issueDropdown.getSelectedItem();

		// Use textfield for "Others"
		if (issueDescription.equals("Others: (Please specify)")) {
			issueDescription = issueDetailField.getText();
		}

		// Validate room selection and issue description
		String room = (String) roomField.getSelectedItem();  // Get the selected room from the dropdown
		if (room == null || room.equals("Select a room") || issueDescription.isEmpty()) {
			if (room == null || room.equals("Select a room")) {
				JOptionPane.showMessageDialog(frame, "Please select a room!", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(frame, "Please fill in the textbox.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			try {
				String query = "INSERT INTO security_requests (room, issue_description, status) VALUES (?, ?, 'Pending')";
				try (PreparedStatement pst = conn.prepareStatement(query)) {
					pst.setString(1, room);  // Pass the selected room
					pst.setString(2, issueDescription);  // Pass the issue description
					pst.executeUpdate();  // Execute the insert query

					JOptionPane.showMessageDialog(frame, "Security request submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

					// Reset fields after submission
					roomField.setSelectedIndex(0); 
					issueDropdown.setSelectedIndex(0);  
					issueDetailField.setText(""); 
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Error submitting Security request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private boolean isRoomValid(String roomName) {
        String roomQuery = "SELECT * FROM rooms WHERE room_name = ?";
        try (PreparedStatement pst = conn.prepareStatement(roomQuery)) {
            pst.setString(1, roomName);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error validating room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

	public void actionPerformed(ActionEvent e) {
		// Get the source of the event
		Object source = e.getSource();

		// Handle dropdown for "issueDropdown"
		if (source == issueDropdown) {
			String selected = (String) issueDropdown.getSelectedItem();
			boolean isOther = selected != null && selected.equals("Others: (Please specify)");
			issueDetailField.setVisible(isOther);
			issueDetailLabel.setVisible(isOther);
		}
		// Handle dropdown for "issueDropdownS"
		else if (source == issueDropdownS) {
			String selected = (String) issueDropdownS.getSelectedItem();
			boolean isOther = selected != null && selected.equals("Others: (Please specify)");
			SissueDetailField.setVisible(isOther);
			SissueDetailLabel.setVisible(isOther);
		}
		// Handle button actions
		else {
			// Handle the button by checking its name
			JButton btn = (JButton) source;  // This will work since we know the source is a button here
			String buttonName = btn.getName();
			
			// Switch on the button name to call the appropriate method
			switch (buttonName) {
				case "bookClassBtn":
					bookRoom();
					break;
				case "cancelBookBtn":
					cancelBooking();
					break;
				case "checkBookBtn":
					checkRoomStatus();
					break;
				case "regSchedBtn":
					displayRegularSched(getIdNum());
					break;
				case "logoutBtn":
					logout();
					break;
				case "submitITSRequestBtn":
					submitITSRequest();
					break;
				case "submitSecRequestBtn":
					submitSecRequest();
					break;
				// You can add more button cases here if needed
			}
		}
	}

    private void bookRoom() {
        UniversityRoomBooking.roomBookMain(getIdNum());
    }

    private void cancelBooking() {
        BookingCancellation.showCancellation(getIdNum());
    }

    private void checkRoomStatus() {
        ShowStatus.showRoomStatus(getIdNum());
    }

    private void logout() {
        frame.dispose();
        Start.main(new String[] {});
    }

    // Display professor's regular schedule in a table
    private void displayRegularSched(int professorId) {
        // Fetch the professor's regular schedule
        List<Schedule> scheduleList = getRegularSchedule(professorId);
		
		if (scheduleList.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Admin is yet to set up your regular schedule. Please check again later.", "No Schedule Available", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			// Create the table
			String[] columnNames = {"Day", "Time Slot", "Room Name"};
			
			Object[][] data = new Object[scheduleList.size()][3];
			
			for (int i = 0; i < scheduleList.size(); i++) {
				Schedule schedule = scheduleList.get(i);
				data[i][0] = schedule.getDay();
				data[i][1] = schedule.getTimeSlot();
				data[i][2] = schedule.getRoomName();
			}

			// Create the JTable with data and column names
			JTable scheduleTable = new JTable(data, columnNames);
			JScrollPane scrollPane = new JScrollPane(scheduleTable);
		}
	}

    // Fetches the professor's regular schedule from the database
    private List<Schedule> getRegularSchedule(int professorId) {
        List<Schedule> scheduleList = new ArrayList<>();

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");

            // SQL query
            String sql = "SELECT schedule_dates, time_slot, room_name FROM regularSchedules WHERE professor_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, professorId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Extract data from the result set
                String scheduleDates = rs.getString("schedule_dates");
                String timeSlot = rs.getString("time_slot");
                String roomName = rs.getString("room_name");

                // Split the schedule dates (comma-separated) into individual days
                String[] days = scheduleDates.split(",");
                for (String day : days) {
                    // Create a Schedule object for each day and add to the list
                    scheduleList.add(new Schedule(day.trim(), timeSlot, roomName));
                }
            }

            // Close the resources
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching schedule data.");
        }

        return scheduleList;
    }
}
