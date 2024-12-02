import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgramAdmin implements ActionListener {
    private static JFrame frame;
    private static JButton acctButton;
    private static JButton bookButton;
    private static JButton roomButton;
    private static JButton logoutButton;
    private static JButton createRoomButton;
    private static JButton createAdminButton;
	private static JButton allAccountsButton;
	private static JButton deleteRoomButton;

    private static JLabel userLabel;
    private static JTextField enterUser;
    private static JComboBox<String> userActionDropdown;
    private static JTextField declineUserField;
    private static JComboBox<String> bookingActionDropdown;
	private static DefaultTableModel model = new DefaultTableModel();
	
	private static int idNum;
	
	public ProgramAdmin(int idNum) {
        this.idNum = idNum;
    }
    
    public static int getIdNum(){
        return idNum;
    }

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        String buttonName = temp.getName();
        switch (buttonName) {
            case "acctButton":
                DisplayNonApprovedAccounts();
                break;
			case "allAccountsButton":
				DisplayApprovedAccounts();
				break;
            case "createRoomButton":
                RoomCreatorWithGUI.main(new String[] {});
                break;
			case "deleteRoomButton":
				deleteRoom();
				break;
            case "createAdminButton":
                SignUpAdmin signUpAdmin = new SignUpAdmin();
                signUpAdmin.signUpMain();
                break;
            case "bookButton":
                DisplayRoomBookRequests();
                break;
            case "roomButton":
                displayAllRooms();
                break;
			case "assignScheduleButton": // Added missing case
				displayProfessorsForScheduling(); // Call the scheduling method
				break;
			case "modifyScheduleButton":
			ModifyRegularSchedules.displayRegularSchedules();
			break;
            case "logoutButton":
                frame.dispose();
                Start.main(new String[]{});
                break;
        }
    }

    public static void adminMenu(){
        frame = new JFrame("Program Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // View Requests for Account Approval
        gbc.gridx = 0;
        gbc.gridy = 0;
        acctButton = new JButton("View Requests for Account Approval");
        acctButton.addActionListener(new ProgramAdmin(getIdNum()));
        acctButton.setName("acctButton");
        panel.add(acctButton, gbc);
		
        // View All Accounts in the Database
        gbc.gridx = 0;
        gbc.gridy = 1;
        allAccountsButton = new JButton("View All Approved Accounts in the Database");
        allAccountsButton.addActionListener(new ProgramAdmin(getIdNum()));
        allAccountsButton.setName("allAccountsButton");
        panel.add(allAccountsButton, gbc);

        // Create New Admin
        gbc.gridx = 0;
        gbc.gridy = 2;
        createAdminButton = new JButton("Create New Admin");
        createAdminButton.addActionListener(new ProgramAdmin(getIdNum()));
        createAdminButton.setName("createAdminButton");
        panel.add(createAdminButton, gbc);

        // Create New Room
        gbc.gridx = 0;
        gbc.gridy = 3;
        createRoomButton = new JButton("Create New Room");
        createRoomButton.addActionListener(new ProgramAdmin(getIdNum()));
        createRoomButton.setName("createRoomButton");
        panel.add(createRoomButton, gbc);
		
		// Delete a room
        gbc.gridx = 0;
        gbc.gridy = 4;
        deleteRoomButton = new JButton("Delete a Room");
        deleteRoomButton.addActionListener(new ProgramAdmin(getIdNum()));
        deleteRoomButton.setName("deleteRoomButton");
        panel.add(deleteRoomButton, gbc);

        // View Requests for Room Reservation
        gbc.gridx = 0;
        gbc.gridy = 5;
        bookButton = new JButton("View Requests for Room Reservation");
        bookButton.addActionListener(new ProgramAdmin(getIdNum()));
        bookButton.setName("bookButton");
        panel.add(bookButton, gbc);

        // View Room Availability
        gbc.gridx = 0;
        gbc.gridy = 6;
        roomButton = new JButton("View Rooms");
        roomButton.addActionListener(new ProgramAdmin(getIdNum()));
        roomButton.setName("roomButton");
        panel.add(roomButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		
		JButton assignScheduleButton = new JButton("Assign Regular Schedule");
		assignScheduleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayProfessorsForScheduling(); // Call the method to display professors
			}
		});
		assignScheduleButton.setName("assignScheduleButton");
		panel.add(assignScheduleButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8; // Place after the last button's y-position
		JButton modifyScheduleButton = new JButton("Modify Regular Schedules");
		modifyScheduleButton.addActionListener(e -> {
				ModifyRegularSchedules.displayRegularSchedules(); // Open the modification interface
			});
		modifyScheduleButton.setName("modifyScheduleButton");
			panel.add(modifyScheduleButton, gbc);
			
		// Logout
		gbc.gridx = 0;
		gbc.gridy = 9;
		logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ProgramAdmin(getIdNum()));
		logoutButton.setName("logoutButton");
		panel.add(logoutButton, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }

	private static void DisplayNonApprovedAccounts() {
		JFrame frame = new JFrame("Non-Approved Users");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);

		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID Number");
		model.addColumn("First Name");
		model.addColumn("Last Name");
		model.addColumn("Role");

		// Create JTable
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);
	
		refreshNonApprovedUsersTable(model);

		// Add a text field and dropdown to approve/decline users
		JPanel panel = new JPanel();
		JLabel userLabel = new JLabel("Enter ID Number:");
		panel.add(userLabel);
		JTextField enterUser = new JTextField(15);
		panel.add(enterUser);

		// Dropdown for actions (Approve/Decline)
		String[] actions = {"Option", "Approve", "Decline"};
		JComboBox<String> userActionDropdown = new JComboBox<>(actions);
		panel.add(userActionDropdown);

		// Action listener for dropdown
		userActionDropdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedAction = (String) userActionDropdown.getSelectedItem();
				String userIdText = enterUser.getText().trim();

				// Check if the ID number field is empty
				if (userIdText.equals("")) {
					JOptionPane.showMessageDialog(null, "Please enter an ID number!", "Error", JOptionPane.ERROR_MESSAGE);
					return;  
				}
				
				else{
					try {
						// Parse the user ID from the text field
						int userId = Integer.parseInt(userIdText);

						// Check if a valid action is selected
						if ("Option".equals(selectedAction)) {
							JOptionPane.showMessageDialog(null, "Please choose an action to perform!", "Warning", JOptionPane.WARNING_MESSAGE);
						}
						
						else{
							// Process the selected action
							if ("Approve".equals(selectedAction)) {
								approveUser(userId); 
								enterUser.setText("");
								refreshNonApprovedUsersTable(model);
							} else if ("Decline".equals(selectedAction)) {
								declineUser(userId);  // Decline the user
								enterUser.setText("");
								// Refresh the non-approved users table after decline
								refreshNonApprovedUsersTable(model);
							}
						}

					} catch (NumberFormatException ex) {
						// Catch invalid number format
						JOptionPane.showMessageDialog(null, "Invalid ID format! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
					} catch (Exception ex) {
						// General exception handling for any other errors
						JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
		}});



		frame.add(panel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	private static void refreshNonApprovedUsersTable(DefaultTableModel model) {
		// Clear existing table data
		model.setRowCount(0);

		String sql = "SELECT id_number, firstname, lastname, role FROM nonApprovedUsers";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {

			// Loop through result set and populate the table
			while (resultSet.next()) {
				int idNumber = resultSet.getInt("id_number");  // Ensure column name matches the DB
				String firstname = resultSet.getString("firstname");
				String lastname = resultSet.getString("lastname");
				String role = resultSet.getString("role");
				model.addRow(new Object[]{idNumber, firstname, lastname, role});
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

    //Moves the user from non=approve to the table of all approved users	
	private static void approveUser(int idNumber) {
		String selectSql = "SELECT * FROM nonApprovedUsers WHERE id_number = ?";
		String deleteSql = "DELETE FROM nonApprovedUsers WHERE id_number = ?";
		String insertSql = "INSERT INTO users (idnumber, firstname, lastname, password, role) " +
						   "SELECT id_number, firstname, lastname, password, role FROM nonApprovedUsers WHERE id_number = ?";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca")) {
			connection.setAutoCommit(false);  // Begin transaction

			// Check if the user exists in the nonApprovedUsers table
			try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
				selectStmt.setInt(1, idNumber);
				ResultSet rs = selectStmt.executeQuery();

				if (rs.next()) {
					// Proceed to insert into the users table 
					try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
						insertStmt.setInt(1, idNumber);  
						insertStmt.executeUpdate();
					}

					// Remove the user from nonApprovedUsers
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
						deleteStmt.setInt(1, idNumber);
						deleteStmt.executeUpdate();
					}

					connection.commit();  // Commit the transaction
					JOptionPane.showMessageDialog(null, "User approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

					// Refresh the non-approved users table after approval
					refreshNonApprovedUsersTable(model);  
				} else {
					JOptionPane.showMessageDialog(null, "User not found in the non-approved list.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (SQLException e) {
				connection.rollback();
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error approving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error connecting to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

    private static void declineUser(int userId) {
		String sql = "DELETE FROM nonApprovedUsers WHERE id_number = ?";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setInt(1, userId);  // Set the user ID parameter

			// Execute the delete query
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				JOptionPane.showMessageDialog(null, "User with ID " + userId + " request has been declined and removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
				
				// Refresh the table
				String selectNonApprovedUsersSql = "SELECT id_number, firstname, lastname, role FROM nonApprovedUsers";
				refreshNonApprovedUsersTable(model);
			} else {
				JOptionPane.showMessageDialog(null, "No user found with ID " + userId, "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			System.out.println("Error deleting user: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "An error occurred while declining the user.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private static void deleteRoom() {
		// Create the frame and table for room deletion
		JFrame frame = new JFrame("Delete Room");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 600);
		
		// DefaultTableModel for room data
		DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Room Name", "Category", "Capacity", "Building"}, 0);
		JTable roomTable = new JTable(tableModel);
		
		// Fetch room data from the database and populate the table
		refreshRooms(tableModel);

		// Label, TextField, and Button for room deletion
		JLabel label = new JLabel("Enter the room name of the room you want to remove: ");
		JTextField roomNameField = new JTextField(20);
		
		// Make the button smaller by setting a preferred size
		JButton removeButton = new JButton("Remove Room");
		removeButton.setPreferredSize(new Dimension(120, 30));  // Set preferred size to make it smaller
		
		// ActionListener for the "Remove Room" button
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String roomName = roomNameField.getText();
				if (roomName.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Please enter a room name", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				roomRemover(roomName);  // Call room remover with the room name
				refreshRooms(tableModel);  // Refresh room data in the table after deletion
			}
		});

		// Panel to organize components
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Stack components vertically

		// Add components to the panel
		panel.add(new JScrollPane(roomTable));  // Add table to scroll pane
		
		// Spacer to add some space between the table and the input section
		panel.add(Box.createVerticalStrut(10)); // Vertical space between table and input area
		
		// Create a new JPanel with FlowLayout to align label, text field, and button horizontally
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		inputPanel.add(label);  // Add the label
		inputPanel.add(roomNameField);  // Add the text field
		inputPanel.add(removeButton);  // Add the button
		
		// Add the inputPanel (label, text field, button) to the main panel
		panel.add(inputPanel);
		
		frame.add(panel);
		frame.setVisible(true);
	}

	// Method to refresh room data and update the table
	private static void refreshRooms(DefaultTableModel tableModel) {
		// Clear the current data in the table
		tableModel.setRowCount(0);

		// Fetch updated room data from the database and populate the table
		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca")) {
			String fetchRoomsSql = "SELECT room_name, category, max_capacity, building FROM rooms";
			try (PreparedStatement stmt = connection.prepareStatement(fetchRoomsSql);
				 ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String roomName = rs.getString("room_name");
					String category = rs.getString("category");
					int capacity = rs.getInt("max_capacity");
					String building = rs.getString("building");
					tableModel.addRow(new Object[]{roomName, category, capacity, building});
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error fetching room data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private static void roomRemover(String roomName) {
		// SQL queries to delete the room from the related tables
		String deleteFromApprovedBookingsSql = "DELETE FROM approvedBookings WHERE room_name = ?";
		String deleteFromUnapproveBookingsSql = "DELETE FROM unapproveBookings WHERE room_name = ?";
		String deleteFromRegularSchedulesSql = "DELETE FROM regularSchedules WHERE room_name = ?";
		String deleteFromSecurityRequestsSql = "DELETE FROM security_requests WHERE room = ?";
		String deleteFromItsRequestsSql = "DELETE FROM its_requests WHERE room = ?";

		// Declare the connection variable here so it can be accessed in both try and catch blocks
		Connection connection = null;

		try {
			// Establish connection to the database
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");

			// Begin a transaction to ensure all deletions are handled atomically
			connection.setAutoCommit(false);

			// Delete entries in the related tables
			try (PreparedStatement stmt = connection.prepareStatement(deleteFromApprovedBookingsSql)) {
				stmt.setString(1, roomName);  // Use room name as parameter
				stmt.executeUpdate();
			}

			try (PreparedStatement stmt = connection.prepareStatement(deleteFromUnapproveBookingsSql)) {
				stmt.setString(1, roomName);
				stmt.executeUpdate();
			}

			try (PreparedStatement stmt = connection.prepareStatement(deleteFromRegularSchedulesSql)) {
				stmt.setString(1, roomName);
				stmt.executeUpdate();
			}

			try (PreparedStatement stmt = connection.prepareStatement(deleteFromSecurityRequestsSql)) {
				stmt.setString(1, roomName);
				stmt.executeUpdate();
			}

			try (PreparedStatement stmt = connection.prepareStatement(deleteFromItsRequestsSql)) {
				stmt.setString(1, roomName);
				stmt.executeUpdate();
			}

			// Now, delete the room from the 'rooms' table
			String deleteRoomSql = "DELETE FROM rooms WHERE room_name = ?";
			try (PreparedStatement stmt = connection.prepareStatement(deleteRoomSql)) {
				stmt.setString(1, roomName);
				int rowsAffected = stmt.executeUpdate();

				if (rowsAffected > 0) {
					JOptionPane.showMessageDialog(null, "Room " + roomName + " and its associated data have been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "No room found with name " + roomName, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			// Commit the transaction if all deletions were successful
			connection.commit();
		} catch (SQLException e) {
			// Rollback the transaction in case of any error
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
			}

			JOptionPane.showMessageDialog(null, "Error deleting room and its associated data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			// Ensure the connection is closed
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException closeEx) {
					closeEx.printStackTrace();
				}
			}
		}
	}
	
	private static void DisplayApprovedAccounts() {
		JFrame frame = new JFrame("Approved Users");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);

		// Create table
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID Number");
		model.addColumn("First Name");
		model.addColumn("Last Name");
		model.addColumn("Role");

		// Create JTable with the model
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);

		// SQL query to select all users from the users table
		String sql = "SELECT idNumber, firstName, lastName, role FROM users";

		try (Connection connection = DatabaseHelper.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {

			// Loop through result set and populate the table
			while (resultSet.next()) {
				int idNumber = resultSet.getInt("idNumber");
				String firstName = resultSet.getString("firstName");
				String lastName = resultSet.getString("lastName");
				String role = resultSet.getString("role");
				model.addRow(new Object[]{idNumber, firstName, lastName, role});
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetchinga data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		frame.setVisible(true);
	}
	
	private static void DisplayRoomBookRequests() {
		// Establish connection to the database
		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca")) {
			
			// Create frame for displaying room booking requests
			JFrame frame = new JFrame("Reservation Requests");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(600, 400);
			frame.setLocationRelativeTo(null);

			// Define the column names for the room booking table
			String[] columnNames = {"Booking ID", "Professor ID", "First Name", "Last Name", "Booking Date", "Time Slot", "Room Name", "Room Category"};

			// DefaultTableModel with empty data
			DefaultTableModel model = new DefaultTableModel(columnNames, 0);

			// Create JTable and set it to fill the viewport
			JTable table = new JTable(model);
			table.setFillsViewportHeight(true);
			JScrollPane scrollPane = new JScrollPane(table);
			frame.add(scrollPane, BorderLayout.CENTER);

			// SQL query to select all data from the unapproveBookings table
			String sql = "SELECT P.id AS booking_id, P.professor_id, U.firstName, U.lastName, P.booking_date, P.time_slot, P.room_name, P.room_category " +
						 "FROM unapproveBookings P " +
						 "INNER JOIN users U ON P.professor_id = U.idNumber";  // Corrected the alias in JOIN

			// Refresh the table with the current data
			refreshRoomBookingTable(model, connection, sql);  // Pass connection to refresh method

			// Add a text field and dropdown to approve/decline room bookings
			JPanel panel = new JPanel();
			JLabel userLabel = new JLabel("Enter Booking ID to approve/decline:");
			panel.add(userLabel);
			JTextField enterUser = new JTextField(15);
			panel.add(enterUser);

			// Dropdown for actions (Approve/Decline)
			JComboBox<String> bookingActionDropdown = new JComboBox<>(new String[]{"Option", "Approve", "Decline"});
			panel.add(bookingActionDropdown);

			// Action listener for dropdown
			bookingActionDropdown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String selectedAction = (String) bookingActionDropdown.getSelectedItem();
					String bookingIdText = enterUser.getText().trim();
					if (bookingIdText.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter a Booking ID!", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							int bookingId = Integer.parseInt(bookingIdText);
							if ("Option".equals(selectedAction)) {
								JOptionPane.showMessageDialog(null, "Please choose an action to perform!", "Warning", JOptionPane.WARNING_MESSAGE);
							} else if ("Approve".equals(selectedAction)) {
								approveBooking(bookingId, connection);  // Approve the booking
								enterUser.setText("");
								refreshRoomBookingTable(model, connection, sql);  // Refresh after action
							} else if ("Decline".equals(selectedAction)) {
								declineBooking(bookingId, connection);  // Decline the booking
								enterUser.setText("");
								refreshRoomBookingTable(model, connection, sql);  // Refresh after action
							}
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null, "Invalid Booking ID format!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});

			// Add the panel with input fields at the bottom of the frame
			frame.add(panel, BorderLayout.SOUTH);
			frame.setVisible(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void declineBooking(int id, Connection connection) {
		String sql = "DELETE FROM unapproveBookings WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, id);  // Set the ID parameter

			int rowsAffected = stmt.executeUpdate();  // Execute the delete query

			if (rowsAffected > 0) {
				JOptionPane.showMessageDialog(null, "Booking with ID " + id + " has been declined.", "Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "No booking found with ID " + id, "Error", JOptionPane.ERROR_MESSAGE);
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error deleting booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	
	//Moves the user from non-approve to the table of all approved bookings
    private static void approveBooking(int idNumber, Connection connection) {
		String selectSql = "SELECT * FROM unapproveBookings WHERE id = ?";
		String deleteSql = "DELETE FROM unapproveBookings WHERE id = ?";
		String insertSql = "INSERT INTO approvedBookings (professor_id, booking_date, time_slot, room_name, room_category) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, idNumber);
			try (ResultSet rs = selectStmt.executeQuery()) {

				if (rs.next()) {
					// If the booking exists, move it to approvedBookings table
					String professorId = rs.getString("professor_id");
					Date bookingDate = rs.getDate("booking_date");
					String timeSlot = rs.getString("time_slot");
					String roomName = rs.getString("room_name");
					String roomCategory = rs.getString("room_category");

					// Insert the booking into the approvedBookings table
					try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
						insertStmt.setString(1, professorId);
						insertStmt.setDate(2, bookingDate);
						insertStmt.setString(3, timeSlot);
						insertStmt.setString(4, roomName);
						insertStmt.setString(5, roomCategory);
						insertStmt.executeUpdate();
					}

					// Remove the booking from unapproveBookings
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
						deleteStmt.setInt(1, idNumber);
						deleteStmt.executeUpdate();
					}

					JOptionPane.showMessageDialog(null, "Booking approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error approving booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
 
	private static void refreshRoomBookingTable(DefaultTableModel model, Connection connection, String sql) {
		model.setRowCount(0);  // Clear existing rows in the table

		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			// Fetch data and display it in the table
			while (rs.next()) {
				int bookingId = rs.getInt("booking_id");
				int professorId = rs.getInt("professor_id");
				String professorFirstName = rs.getString("firstName");
				String professorLastName = rs.getString("lastName");
				Date bookingDate = rs.getDate("booking_date");
				String timeSlot = rs.getString("time_slot");
				String roomName = rs.getString("room_name");
				String roomCategory = rs.getString("room_category");

				model.addRow(new Object[]{bookingId, professorId, professorFirstName, professorLastName, bookingDate, timeSlot, roomName, roomCategory});
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to fetch data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void displayAllRooms() {
		JFrame frame = new JFrame("All Rooms");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 400);
		frame.setLocationRelativeTo(null);

		// Define column names for the room table
		String[] columnNames = {"Room ID", "Category", "Room Name", "Max Capacity", "Tags", "Building"};

		// Create DefaultTableModel with empty data
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Create JTable with the model
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);

		// SQL query to retrieve all rooms
		String sql = "SELECT id, category, room_name, max_capacity, tags, building FROM rooms";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {

			// Loop through the result set and populate the table model
			while (resultSet.next()) {
				int roomId = resultSet.getInt("id");
				String category = resultSet.getString("category");
				String roomName = resultSet.getString("room_name");
				int maxCapacity = resultSet.getInt("max_capacity");
				String tags = resultSet.getString("tags");
				String building = resultSet.getString("building");

				// Add row to the table model
				model.addRow(new Object[]{roomId, category, roomName, maxCapacity, tags, building});
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetchingb data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Make the frame visible
		frame.setVisible(true);
	}
	
	private static void displayProfessorsForScheduling() {
		JFrame frame = new JFrame("Assign Regular Schedule");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
	
		// Create table model to display professors
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID Number");
		model.addColumn("First Name");
		model.addColumn("Last Name");
		model.addColumn("Role");
	
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);
	
		String sql = "SELECT idNumber, firstName, lastName, role FROM users WHERE role = 'Professor'";
	
		try (Connection connection = DatabaseHelper.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {
	
			// Populate table with professor data
			while (resultSet.next()) {
				int idNumber = resultSet.getInt("idNumber");
				String firstName = resultSet.getString("firstName");
				String lastName = resultSet.getString("lastName");
				String role = resultSet.getString("role");
				model.addRow(new Object[]{idNumber, firstName, lastName, role});
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching professor data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		// Panel for action buttons
		JPanel panel = new JPanel();
		JButton assignButton = new JButton("Assign Schedule");
		assignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					int professorId = (int) model.getValueAt(selectedRow, 0); // Get selected professor ID
					frame.dispose(); // Close the window
					RoomScheduling.roomBookMain(professorId); // Call RoomScheduling to assign a schedule
				} else {
					JOptionPane.showMessageDialog(frame, "Please select a professor.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel.add(assignButton);
		frame.add(panel, BorderLayout.SOUTH);
	
		frame.setVisible(true);
	}
}
