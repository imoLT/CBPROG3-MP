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
    private JTextField roomField, issueDetailField, roomFieldS, SissueDetailField;
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
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
        JPanel cardPanel = new JPanel(new CardLayout()); // For ITS and Security sections

		GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Initialize buttons
        bookClassBtn = new JButton("Book Room");
        bookClassBtn.setName("bookClassBtn");
        bookClassBtn.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(bookClassBtn, gbc);

        cancelBookBtn = new JButton("Cancel Booking");
        cancelBookBtn.setName("cancelBookBtn");
        cancelBookBtn.addActionListener(this);
        gbc.gridx = 1; gbc.gridy = 0;
        mainPanel.add(cancelBookBtn, gbc);

        checkBookBtn = new JButton("Check Room Status");
        checkBookBtn.setName("checkBookBtn");
        checkBookBtn.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(checkBookBtn, gbc);
		
		regSchedBtn = new JButton("Check my Regular Schedule");
        regSchedBtn.setName("regSchedBtn");
        regSchedBtn.addActionListener(this);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(regSchedBtn, gbc);

        logoutBtn = new JButton("Logout");
        logoutBtn.setName("logoutBtn");
        logoutBtn.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(logoutBtn, gbc);

        callITBtn = new JButton("Call ITS");
        callITBtn.setName("callITBtn");
        callITBtn.addActionListener(e -> showITSSection(cardPanel));
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(callITBtn, gbc);

        callSecBtn = new JButton("Call Security");
        callSecBtn.setName("callSecBtn");
        callSecBtn.addActionListener(e -> showSecuritySection(cardPanel));
        gbc.gridx = 2; gbc.gridy = 2;
        mainPanel.add(callSecBtn, gbc);

        // Add ITS section to CardLayout
        JPanel itsPanel = new JPanel(new GridBagLayout());
        setupITSPanel(itsPanel);

        // Add Security section to CardLayout
        JPanel securityPanel = new JPanel(new GridBagLayout());
        setupSecurityPanel(securityPanel);

        // Add both panels to the CardLayout
        cardPanel.add(itsPanel, "ITS");
        cardPanel.add(securityPanel, "Security");

        // Add main and card panels to the frame
        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(cardPanel, BorderLayout.CENTER);

        // Initially, ensure no menu is shown
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "");

        frame.setVisible(true);
    }
	
	private void setupITSPanel(JPanel itsPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        roomLabel = new JLabel("Room:");
        roomField = new JTextField(15);
        gbc.gridx = 0; gbc.gridy = 0; itsPanel.add(roomLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; itsPanel.add(roomField, gbc);

        issueLabel = new JLabel("Issue Description:");
        issueDropdown = new JComboBox<>(new String[]{"Projector assistance", "OS problems", "Installing of applications", "Others: (Please specify)"});
        issueDropdown.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 1; itsPanel.add(issueLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; itsPanel.add(issueDropdown, gbc);

        issueDetailLabel = new JLabel("Please specify the issue:");
        issueDetailField = new JTextField(15);
        issueDetailField.setVisible(false);
        issueDetailLabel.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 2; itsPanel.add(issueDetailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; itsPanel.add(issueDetailField, gbc);

        submitITSRequestBtn = new JButton("Submit ITS Request");
        submitITSRequestBtn.setName("submitITSRequestBtn");
        submitITSRequestBtn.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; itsPanel.add(submitITSRequestBtn, gbc);
    }

    private void setupSecurityPanel(JPanel securityPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        roomLabelS = new JLabel("Room:");
        roomFieldS = new JTextField(15);
        gbc.gridx = 0; gbc.gridy = 0; securityPanel.add(roomLabelS, gbc);
        gbc.gridx = 1; gbc.gridy = 0; securityPanel.add(roomFieldS, gbc);

        issueLabelS = new JLabel("Issue Description:");
        issueDropdownS = new JComboBox<>(new String[]{"Get TV Remote", "Up or Down Airconditioner", "Others: (Please specify)"});
        issueDropdownS.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 1; securityPanel.add(issueLabelS, gbc);
        gbc.gridx = 1; gbc.gridy = 1; securityPanel.add(issueDropdownS, gbc);

        SissueDetailLabel = new JLabel("Please specify the issue:");
        SissueDetailField = new JTextField(15);
        SissueDetailField.setVisible(false);
        SissueDetailLabel.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 2; securityPanel.add(SissueDetailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; securityPanel.add(SissueDetailField, gbc);

        submitSecRequestBtn = new JButton("Submit Security Request");
        submitSecRequestBtn.setName("submitSecRequestBtn");
        submitSecRequestBtn.addActionListener(this);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; securityPanel.add(submitSecRequestBtn, gbc);
    }

    private void showITSSection(JPanel cardPanel) {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "ITS");
    }

    private void showSecuritySection(JPanel cardPanel) {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "Security");
    }

    private void submitITSRequest() {
        String room = roomField.getText();
        String issueDescription = (String) issueDropdown.getSelectedItem();

        if (issueDescription.equals("Others: (Please specify)")) {
            issueDescription = issueDetailField.getText();
        }

        if (room.isEmpty() || issueDescription.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isRoomValid(room)) {
            JOptionPane.showMessageDialog(frame, "Invalid room. Please enter a valid room name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO its_requests (room, issue_description, status) VALUES (?, ?, 'Pending')";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, room);
                pst.setString(2, issueDescription);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(frame, "ITS request submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Reset fields
                roomField.setText("");
                issueDetailField.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error submitting ITS request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitSecRequest() {
        String room = roomFieldS.getText();
        String issueDescription = (String) issueDropdownS.getSelectedItem();

        if (issueDescription.equals("Others: (Please specify)")) {
            issueDescription = SissueDetailField.getText();
        }

        if (room.isEmpty() || issueDescription.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isRoomValid(room)) {
            JOptionPane.showMessageDialog(frame, "Invalid room. Please enter a valid room name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO security_requests (room, issue_description, status) VALUES (?, ?, 'Pending')";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, room);
                pst.setString(2, issueDescription);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Security request submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Reset fields
                roomFieldS.setText("");
                SissueDetailField.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error submitting Security request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
