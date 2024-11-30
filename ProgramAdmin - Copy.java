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
	private static JButton removeUserButton;

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
			case "removeUserButton":
				removeUser(getIdNum());
				break;
			case "allAccountsButton":
				DisplayApprovedAccounts();
				break;
            case "createRoomButton":
                RoomCreatorWithGUI.main(new String[] {});
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
		
		// Remove User
        gbc.gridx = 0;
        gbc.gridy = 1;
        removeUserButton = new JButton("Remove a User");
        removeUserButton.addActionListener(new ProgramAdmin(getIdNum()));
        removeUserButton.setName("removeUserButton");
        panel.add(removeUserButton, gbc);

        // View All Accounts in the Database
        gbc.gridx = 0;
        gbc.gridy = 2;
        allAccountsButton = new JButton("View All Approved Accounts in the Database");
        allAccountsButton.addActionListener(new ProgramAdmin(getIdNum()));
        allAccountsButton.setName("allAccountsButton");
        panel.add(allAccountsButton, gbc);

        // Create New Admin
        gbc.gridx = 0;
        gbc.gridy = 3;
        createAdminButton = new JButton("Create New Admin");
        createAdminButton.addActionListener(new ProgramAdmin(getIdNum()));
        createAdminButton.setName("createAdminButton");
        panel.add(createAdminButton, gbc);

        // Create New Room
        gbc.gridx = 0;
        gbc.gridy = 4;
        createRoomButton = new JButton("Create New Room");
        createRoomButton.addActionListener(new ProgramAdmin(getIdNum()));
        createRoomButton.setName("createRoomButton");
        panel.add(createRoomButton, gbc);

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
			@Override
			public void actionPerformed(ActionEvent e) {
				displayProfessorsForScheduling(); // Call the method to display professors
			}
		});
		assignScheduleButton.setName("assignScheduleButton");
		panel.add(assignScheduleButton, gbc);
		
		// Logout
		gbc.gridx = 0;
		gbc.gridy = 8;
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
					return;  // Exit the method
				}

				try {
					// Parse the user ID from the text field
					int userId = Integer.parseInt(userIdText);

					// Check if a valid action is selected
					if ("Option".equals(selectedAction)) {
						JOptionPane.showMessageDialog(null, "Please choose an action to perform!", "Warning", JOptionPane.WARNING_MESSAGE);
						return;  // Exit if no action is selected
					}

					// Process the selected action
					if ("Approve".equals(selectedAction)) {
						approveUser(userId);  // Approve the user
						// Refresh the approved users table after approval
						refreshNonApprovedUsersTable(model);
					} else if ("Decline".equals(selectedAction)) {
						declineUser(userId);  // Decline the user
						// Refresh the non-approved users table after decline
						refreshNonApprovedUsersTable(model);
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
		});



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
					// Proceed to insert into the users table with validated role
					try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
						insertStmt.setInt(1, idNumber);  // Insert using the user id
						insertStmt.executeUpdate();
					}

					// Remove the user from nonApprovedUsers
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
						deleteStmt.setInt(1, idNumber);
						deleteStmt.executeUpdate();
					}

					connection.commit();  // Commit the transaction
					JOptionPane.showMessageDialog(null, "User approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

					// Refresh the non-approved users table after approval (no need to refresh approved users table)
					refreshNonApprovedUsersTable(model);  // Refresh non-approved users table only
				} else {
					JOptionPane.showMessageDialog(null, "User not found in the non-approved list.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (SQLException e) {
				connection.rollback();  // Rollback if there's an error
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
				
				// Refresh the **non-approved users table** to reflect the removal
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
		JFrame frame = new JFrame("Reservation Requests");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);

		// Define the column names for the room booking table
		String[] columnNames = {"Booking ID", "Professor ID", "First Name", "Last Name", "Booking Date", "Time Slot", "Room Name", "Room Category"};

		// DefaultTableModel with empty data
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Create JTable
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);

		// SQL query to select all data from the unapproveBookings table
		String sql = "SELECT P.id AS booking_id, P.professor_id, U.firstName, U.lastName, P.booking_date, P.time_slot, P.room_name, P.room_category " +
                 "FROM unapproveBookings P " +
                 "INNER JOIN users U ON ub.professor_id = u.idNumber";
				 
		// Refresh the table with the current data (room booking data)
		refreshRoomBookingTable(model, sql);

		// Add a text field and dropdown to approve/decline room bookings
		JPanel panel = new JPanel();
		JLabel userLabel = new JLabel("Enter Booking ID to approve/decline:");
		panel.add(userLabel);
		JTextField enterUser = new JTextField(15);
		panel.add(enterUser);

		// Dropdown for actions (Approve/Decline)
		String[] actions = {"Option", "Approve", "Decline"};
		bookingActionDropdown = new JComboBox<>(actions);
		panel.add(bookingActionDropdown);

		// Action listener for dropdown
		bookingActionDropdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedAction = (String) bookingActionDropdown.getSelectedItem();
				String bookingIdText = enterUser.getText().trim();
				if (bookingIdText.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a Booking ID!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					int bookingId = Integer.parseInt(bookingIdText);
					if ("Option".equals(selectedAction)) {
						JOptionPane.showMessageDialog(null, "Please choose an action to perform!", "Warning", JOptionPane.WARNING_MESSAGE);
					} else if ("Approve".equals(selectedAction)) {
						approveBooking(bookingId);  // Approve the booking
						refreshRoomBookingTable(model, sql); // Refresh the table after approval
					} else if ("Decline".equals(selectedAction)) {
						declineBooking(bookingId);  // Decline the booking
						refreshRoomBookingTable(model, sql); // Refresh the table after declining
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Invalid Booking ID format!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		frame.add(panel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	private static void declineBooking(int id) {
        
        // SQL query to delete a booking from the unapproveBookings table using the 'id' column
        String sql = "DELETE FROM unapproveBookings WHERE id = ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Set the id parameter to the booking ID that is passed to the method
            stmt.setInt(1, id);
            
            // Execute the delete query
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Booking with ID " + id + " has been declined and removed.");
            } else {
                System.out.println("No booking found with ID " + id);
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	//Moves the user from non-approve to the table of all approved bookings
    private static void approveBooking(int idNumber) {
		// SQL queries
		String selectSql = "SELECT * FROM unapproveBookings WHERE id = ?";
		String deleteSql = "DELETE FROM unapproveBookings WHERE id = ?";
		String insertSql = "INSERT INTO approvedBookings (professor_id, booking_date, time_slot, room_name, room_category) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca")) {
			// Fetch the booking from unapproveBookings table by ID
			try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
				selectStmt.setInt(1, idNumber);
				ResultSet rs = selectStmt.executeQuery();

				if (rs.next()) {
					// If the booking exists, move it to approvedBookings table
					String professorId = rs.getString("professor_id");
					Date bookingDate = rs.getDate("booking_date");
					String timeSlot = rs.getString("time_slot");
					String roomName = rs.getString("room_name");
					String roomCategory = rs.getString("room_category");

					// Insert the booking into approvedBookings table
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
	
	private static void refreshRoomBookingTable(DefaultTableModel model, String sql) {
		// Clear the existing rows in the table
		model.setRowCount(0);

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			// Fetch data and display it to the table
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
	
	private static void removeUser(int getIdNum) {
		// Create the JFrame for the "Remove User" dialog
		JFrame frame = new JFrame("Remove User");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(1000, 400);
		frame.setLocationRelativeTo(null);

		// Create table model with columns matching the data structure (ID Number, First Name, Last Name, Role)
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

		// SQL query to select all users from the users table, excluding the user with the given id and those with "Program Admin" role
		String sql = "SELECT idNumber, firstName, lastName, role FROM users WHERE idNumber != ? AND role != 'Program Admin'";

		// Refresh table with data
		try (Connection connection = DatabaseHelper.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {

			// Set the user ID parameter to exclude the user with the given id
			statement.setInt(1, getIdNum());

			try (ResultSet resultSet = statement.executeQuery()) {
				// Loop through the result set and populate the table model
				while (resultSet.next()) {
					int idNumber = resultSet.getInt("idNumber");
					String firstName = resultSet.getString("firstName");
					String lastName = resultSet.getString("lastName");
					String role = resultSet.getString("role");
					model.addRow(new Object[]{idNumber, firstName, lastName, role});
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Create a panel for the user input components using GridBagLayout for better control
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);  // Add padding between components

		// Label for ID input
		JLabel userIdLabel = new JLabel("Enter the ID number of the user you want to remove: ");
		gbc.gridx = 0; // First column
		gbc.gridy = 0; // First row
		gbc.anchor = GridBagConstraints.WEST; // Left-align the label
		panel.add(userIdLabel, gbc);

		// Text field for entering the user ID
		JTextField userIdField = new JTextField(15);
		gbc.gridx = 1; // Second column (right after the label)
		gbc.gridy = 0; // Keep it in the same row as the label
		gbc.fill = GridBagConstraints.HORIZONTAL; // Allow the text field to fill the available space
		gbc.anchor = GridBagConstraints.WEST; // Left-align the text field
		panel.add(userIdField, gbc);

		// Remove User Button
		JButton removeUserButton = new JButton("Remove User");
		removeUserButton.setPreferredSize(new Dimension(120, 30));  // Set a smaller button size
		gbc.gridx = 2; // Third column (right after the text field)
		gbc.gridy = 0; // Keep it in the same row as the text field
		gbc.fill = GridBagConstraints.NONE; // Don't allow the button to stretch
		gbc.anchor = GridBagConstraints.EAST; // Right-align the button
		panel.add(removeUserButton, gbc);

		// Add the panel with text field and button below the table
		frame.add(panel, BorderLayout.SOUTH);

		// Add action listener for remove user button
		removeUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userIdField.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Please enter a user ID!", "Error", JOptionPane.ERROR_MESSAGE);
				} 
				else {
					try {
						int userId = Integer.parseInt(userIdField.getText());

						// Check if the user ID exists in the table 
						boolean userExists = false;
						
						for (int i = 0; i < model.getRowCount() && !userExists; i++) {
							int idNumber = (int) model.getValueAt(i, 0); // Get ID 
							if (idNumber == userId) {
								userExists = true;
							}
						}

						if (!userExists) {
							// If user ID doesn't exist in the table, show a warning message
							JOptionPane.showMessageDialog(frame, "Invalid User ID! Please try again.", "Warning", JOptionPane.WARNING_MESSAGE);
						} 
						
						else {
							userRemoval(userId);

							// Refresh the table by clearing it and reloading the data
							model.setRowCount(0);  // Clear existing rows

							// Reload the table data after removal
							try (Connection connection = DatabaseHelper.getConnection();
								 PreparedStatement statement = connection.prepareStatement(sql)) {
								statement.setInt(1, getIdNum()); // Exclude the same user again

								try (ResultSet resultSet = statement.executeQuery()) {
									while (resultSet.next()) {
										int idNumber = resultSet.getInt("idNumber");
										String firstName = resultSet.getString("firstName");
										String lastName = resultSet.getString("lastName");
										String role = resultSet.getString("role");
										model.addRow(new Object[]{idNumber, firstName, lastName, role});
									}
								}

							} catch (SQLException ex) {
								JOptionPane.showMessageDialog(frame, "Error fetching data after user removal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
						}

					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(frame, "Please enter a valid numeric ID!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		// Make the frame visible
		frame.setVisible(true);
	}


	private static void userRemoval(int id) {
		// Catch multiple exceptions (especially SQLException)
		try (Connection conn = DatabaseHelper.getConnection()) {
			// Get the id from the users table
			String sql = "SELECT * FROM users WHERE idNumber = ?";
			
			try (PreparedStatement pst = conn.prepareStatement(sql)) {
				pst.setInt(1, id);

				try (ResultSet rs = pst.executeQuery()) {
					if (rs.next()) {
						// If user found, check role
						String role = rs.getString("role");

						if (role.equals("Professor")) {
							removeProfessor(id);
						} else {
							// Handle other roles like ITS, Security
							JOptionPane.showMessageDialog(null, "ITS or Security here!");
						}
					} else {
						JOptionPane.showMessageDialog(null, "No user found with that ID.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error occurred while executing query: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error preparing SQL statement: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error occurred while connecting to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void removeProfessor(int id) {
		// Using try-with-resources to automatically close the resources
		Connection conn = null;
		try {
			conn = DatabaseHelper.getConnection();
			conn.setAutoCommit(false);  // Start a transaction

			// First, delete the user from the 'users' table
			String deleteUserSQL = "DELETE FROM users WHERE idNumber = ?";
			try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSQL)) {
				deleteUserStmt.setInt(1, id);
				int rowsAffected = deleteUserStmt.executeUpdate();

				if (rowsAffected == 0) {
					// No user was deleted
					JOptionPane.showMessageDialog(null, "No professor found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
					conn.rollback();  // Rollback the transaction
				} else {
					// Commit the transaction if the deletion from 'users' table is successful
					conn.commit();
					JOptionPane.showMessageDialog(null, "Professor and related data successfully removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
				}
			}

		} catch (SQLException e) {
			// Handle SQL exceptions (rollback and show error message)
			if (conn != null) {
				try {
					conn.rollback();  // Rollback the transaction if anything fails
				} catch (SQLException rollbackException) {
					rollbackException.printStackTrace(); // Handle rollback exception
				}
			}

			// Show error message if any exception occurs
			JOptionPane.showMessageDialog(null, "Error occurred while removing user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

		} catch (Exception e) {
			// Catch any other unexpected exceptions (non-SQLExceptions)
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

		} finally {
			// Ensure that the connection is closed after use
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
			@Override
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
