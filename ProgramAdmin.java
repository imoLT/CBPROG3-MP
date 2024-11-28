import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgramAdmin implements ActionListener {
    private static JFrame frame;
    private static JButton acctButton; //Used to approve accounts
	private static JButton bookButton; //Used to approve requests to book rooms
	private static JButton roomButton; //Used to view all rooms and their details
	private static JButton logoutButton; //Used to logout
	private static JButton createRoomButton; //Used to create more rooms
	private static JButton createAdminButton; //Used to create more admins	
	
	private static JLabel userLabel;
    private static JTextField enterUser;
    private static JButton approveBtn;

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        String buttonName = temp.getName();
        switch (buttonName) {
            case "acctButton":
                DisplayNonApprovedAccounts();
                break;
            case "createRoomButton":
                RoomCreatorWithGUI.main(new String[]{});
                break;
            case "createAdminButton":
                SignUpAdmin signUpAdmin = new SignUpAdmin();
                signUpAdmin.signUpMain();
                break;
            case "bookButton":
				DisplayRoomBookRequests();
                break;
            case "roomButton":
				//////////////////////////////////////////////////////
                JOptionPane.showMessageDialog(null, "Room menu here!");
				//////////////////////////////////////////////////////
                break;
            case "logoutButton":
                frame.dispose();
                Start.main(new String[]{});
                break;
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Program Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // acctButton
        gbc.gridx = 0;
        gbc.gridy = 0;
        acctButton = new JButton("View Requests for Account Approval");
        acctButton.addActionListener(new ProgramAdmin());
        acctButton.setName("acctButton");
        panel.add(acctButton, gbc);

        // createAdminButton
        gbc.gridx = 0;
        gbc.gridy = 1;
        createAdminButton = new JButton("Create New Admin");
        createAdminButton.addActionListener(new ProgramAdmin());
        createAdminButton.setName("createAdminButton");
        panel.add(createAdminButton, gbc);

        // createRoomButton
        gbc.gridx = 0;
        gbc.gridy = 2;
        createRoomButton = new JButton("Create New Room");
        createRoomButton.addActionListener(new ProgramAdmin());
        createRoomButton.setName("createRoomButton");
        panel.add(createRoomButton, gbc);

        // bookButton
        gbc.gridx = 0;
        gbc.gridy = 3;
        bookButton = new JButton("View Requests for Room Reservation");
        bookButton.addActionListener(new ProgramAdmin());
        bookButton.setName("bookButton");
        panel.add(bookButton, gbc);

        // roomButton
        gbc.gridx = 0;
        gbc.gridy = 4;
        roomButton = new JButton("View Room Availability");
        roomButton.addActionListener(new ProgramAdmin());
        roomButton.setName("roomButton");
        panel.add(roomButton, gbc);

        // logoutButton
        gbc.gridx = 0;
        gbc.gridy = 5;
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ProgramAdmin());
        logoutButton.setName("logoutButton");
        panel.add(logoutButton, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }
	
	//Displays a table of all non-approved users
	public static void DisplayNonApprovedAccounts() {
        JFrame frame = new JFrame("Non-Approved Users");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Number");
        model.addColumn("Role"); // Removed password column

        // Create JTable
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Query to select all data from the nonApprovedUsers table
        String sql = "SELECT * FROM nonApprovedUsers";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Fetch and add data to table (exclude password)
            while (rs.next()) {
                String idNumber = rs.getString("id_number");
                String role = rs.getString("role");
                model.addRow(new Object[]{idNumber, role}); // Only add ID and Role
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add a text field and button to approve users by entering ID
        JPanel panel = new JPanel();
        userLabel = new JLabel("Enter ID Number to Approve:");
		panel.add(userLabel);
        enterUser = new JTextField(15);
		panel.add(enterUser);
        approveBtn = new JButton("Approve User");
		panel.add(approveBtn);

        approveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (enterUser.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter an ID number!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int idNum = Integer.parseInt(enterUser.getText());
                    approveUser(idNum);  // Call method to approve user
                }
            }
        });
        
        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

	//Moves the user from non=approve to the table of all approved users
    public static void approveUser(int idNumber) {
		String dbUrl = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
		String dbUser = "root";
		String dbPassword = "Vianca";
		
		// SQL queries
		String selectSql = "SELECT * FROM nonApprovedUsers WHERE id_number = ?";
		String deleteSql = "DELETE FROM nonApprovedUsers WHERE id_number = ?";
		String insertSql = "INSERT INTO users (idNumber, password, role) SELECT id_number, password, role FROM nonApprovedUsers WHERE id_number = ?";

		try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
			// Check if the user exists in the nonApprovedUsers table
			try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
				selectStmt.setInt(1, idNumber);
				ResultSet rs = selectStmt.executeQuery();

				if (rs.next()) {
					// If found, move them to users table
					try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
						insertStmt.setInt(1, idNumber);
						insertStmt.executeUpdate();
					}

					// Remove the user from nonApprovedUsers
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
						deleteStmt.setInt(1, idNumber);
						deleteStmt.executeUpdate();
					}

					JOptionPane.showMessageDialog(null, "User approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "User not found in the non-approved list.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error approving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void DisplayRoomBookRequests() {
		JFrame frame = new JFrame("Reservation Requests");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);

		// Define the column names for the table
		String[] columnNames = {"Booking ID", "Professor ID", "Booking Date", "Time Slot", "Room Name", "Room Category"};

		// DefaultTableModel with empty data
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Create JTable
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);

		// Query to select all data from the unapproveBookings table
		String sql = "SELECT * FROM unapproveBookings";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			// Fetch and add data to table
			while (rs.next()) {
				int id = rs.getInt("id");
				String professorId = rs.getString("professor_id");
				Date bookingDate = rs.getDate("booking_date");
				String timeSlot = rs.getString("time_slot");
				String roomName = rs.getString("room_name");
				String roomCategory = rs.getString("room_category");

				// Add row to the table model
				model.addRow(new Object[]{id, professorId, bookingDate, timeSlot, roomName, roomCategory});
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Add a text field and button to approve users by entering ID
		JPanel panel = new JPanel();
		JLabel userLabel = new JLabel("Enter ID of the request you want to approve:");
		panel.add(userLabel);
		JTextField enterUser = new JTextField(15);
		panel.add(enterUser);
		JButton approveBtn = new JButton("Approve Request");
		panel.add(approveBtn);

		approveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userId = enterUser.getText().trim();
				if (userId.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter an ID number!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						int idNum = Integer.parseInt(userId);  // Parse the ID number
						updateRoom(idNum);  // Call method to approve the reservation
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(null, "Invalid ID format!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		frame.add(panel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	//Moves the user from non-approve to the table of all approved bookings
    public static void updateRoom(int idNumber) {
		String dbUrl = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
		String dbUser = "root";
		String dbPassword = "Vianca";

		// SQL queries
		String selectSql = "SELECT * FROM unapproveBookings WHERE id = ?";
		String deleteSql = "DELETE FROM unapproveBookings WHERE id = ?";
		String insertSql = "INSERT INTO approvedBookings (professor_id, booking_date, time_slot, room_name, room_category) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
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

}
