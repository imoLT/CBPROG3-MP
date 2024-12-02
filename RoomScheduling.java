import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class RoomScheduling {
    private static List<LocalDate> selectedDates = new ArrayList<>();
    private static String selectedTimeSlot = "";
    private static String selectedRoom = "";
    public interface RoomScheduleCallback {
        void onScheduleModified(List<LocalDate> updatedDates, String updatedTimeSlot, String updatedRoomName);
    }

    private static RoomScheduleCallback callback;

	public static void roomBookMain(int idNum, RoomScheduleCallback scheduleCallback) {
		callback = scheduleCallback; // Store the callback
		DatabaseHelper.initializeDatabase();
		openCalendar(LocalDate.now().getMonthValue(), idNum); // Open the calendar interface
	}

	public static void roomBookMainWithCallback(int idNum, RoomScheduleCallback scheduleCallback) {
		callback = scheduleCallback; // Store the callback
		DatabaseHelper.initializeDatabase();
		openCalendar(LocalDate.now().getMonthValue(), idNum);
	}

	public static void roomBookMain(int idNum) {
		// Delegate to the new method without using a callback
		roomBookMainWithCallback(idNum, null);
	}


	public static void roomBookMain(int idNum, List<LocalDate> currentDates, String currentTimeSlot, String currentRoomName) {
			// Set the current details for modification
		setSelectedDates(currentDates);
		setSelectedTimeSlot(currentTimeSlot);
		setSelectedRoom(currentRoomName);

			DatabaseHelper.initializeDatabase();
		openCalendar(LocalDate.now().getMonthValue(), idNum);
	}

		
	private static void openCalendar(int initialMonth, int idNum) {
		JFrame frame = new JFrame("Calendar - Multiple Date Selection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 500);
		frame.setLayout(new BorderLayout());
		
		int currentYear = LocalDate.now().getYear();
		List<LocalDate> selectedDates = new ArrayList<>(); // Store multiple selected dates
		
		// Create the month dropdown and label
		String[] months = {
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
		};
		JComboBox<String> monthDropdown = new JComboBox<>(months);
		monthDropdown.setSelectedIndex(initialMonth - 1); // Default month
	
		JLabel monthYearLabel = new JLabel("", SwingConstants.CENTER);
		monthYearLabel.setFont(new Font("Arial", Font.BOLD, 20));
	
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(monthDropdown, BorderLayout.WEST);
		topPanel.add(monthYearLabel, BorderLayout.CENTER);
		frame.add(topPanel, BorderLayout.NORTH);
	
		// Create a table model and calendar table
		String[] columns = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Prevent cell editing
			}
		};
		JTable calendarTable = new JTable(tableModel);
	
		// Update the calendar for the selected month
		Runnable updateCalendar = () -> {
			int selectedMonth = monthDropdown.getSelectedIndex() + 1;
			tableModel.setRowCount(0); // Clear the table
			YearMonth yearMonth = YearMonth.of(currentYear, selectedMonth);
			int daysInMonth = yearMonth.lengthOfMonth();
			LocalDate firstDayOfMonth = LocalDate.of(currentYear, selectedMonth, 1);
			int startDay = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Adjust Sunday as the first column
	
			Object[] week = new Object[7];
			for (int i = 0; i < startDay; i++) {
				week[i] = ""; // Empty cells before the first day
			}
			for (int day = 1; day <= daysInMonth; day++) {
				week[startDay++] = day;
				if (startDay == 7) {
					tableModel.addRow(week);
					week = new Object[7];
					startDay = 0;
				}
			}
			if (startDay != 0) {
				tableModel.addRow(week); // Add remaining days
			}
	
			monthYearLabel.setText(yearMonth.getMonth() + " " + currentYear);
		};
		
		// Highlight selected dates
		calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setBackground(Color.WHITE); // Reset background
	
				if (value != null && !value.toString().isEmpty()) {
					int day = Integer.parseInt(value.toString());
					int selectedMonth = monthDropdown.getSelectedIndex() + 1;
					LocalDate cellDate = LocalDate.of(currentYear, selectedMonth, day);
	
					if (selectedDates.contains(cellDate)) {
						c.setBackground(Color.CYAN); // Highlight selected dates
					}
				}
	
				return c;
			}
		});
	
		// Add a listener to the dropdown to update the calendar
		monthDropdown.addActionListener(e -> updateCalendar.run());
	
		// Add the calendar to the frame
		JScrollPane scrollPane = new JScrollPane(calendarTable);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		// Create a panel for buttons and labels
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		frame.add(bottomPanel, BorderLayout.SOUTH);
	
		JLabel dateLabel = new JLabel("Select dates.", SwingConstants.CENTER);
		dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		bottomPanel.add(dateLabel);
		JButton confirmButton = new JButton("Confirm Dates");
		confirmButton.addActionListener(e -> {
			if (!selectedDates.isEmpty()) {
				frame.dispose(); // Close the calendar interface
				// Proceed to time slot selection
				openTimeSlotMenu(selectedDates, idNum);
			}
			
		});
			
			
		JButton clearButton = new JButton("Clear Dates");
		clearButton.setEnabled(false);
		clearButton.addActionListener(e -> {
			selectedDates.clear();
			dateLabel.setText("Select dates.");
			calendarTable.repaint(); // Repaint calendar to clear highlighting
			confirmButton.setEnabled(false);
			clearButton.setEnabled(false);
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(confirmButton);
		buttonPanel.add(clearButton);
		bottomPanel.add(buttonPanel);
	
		// Add a mouse listener to the table for date selection
		calendarTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = calendarTable.rowAtPoint(e.getPoint());
				int col = calendarTable.columnAtPoint(e.getPoint());
				Object value = calendarTable.getValueAt(row, col);
	
				if (value != null && !value.toString().isEmpty()) {
					int day = Integer.parseInt(value.toString());
					int selectedMonth = monthDropdown.getSelectedIndex() + 1;
					LocalDate clickedDate = LocalDate.of(currentYear, selectedMonth, day);
	
					if (selectedDates.contains(clickedDate)) {
						selectedDates.remove(clickedDate); // Deselect date
					} else {
						selectedDates.add(clickedDate); // Select date
					}
	
					dateLabel.setText("Selected Dates: " + selectedDates);
					calendarTable.repaint(); // Highlight selected dates
					confirmButton.setEnabled(!selectedDates.isEmpty());
					clearButton.setEnabled(!selectedDates.isEmpty());
				}
			}
		});
		
		updateCalendar.run(); // Populate calendar initially
		frame.setVisible(true); // Show frame
	}
		
			

	private static void openTimeSlotMenu(List<LocalDate> selectedDate, int idNum) {
		// Create a new frame for time slots
		JFrame timeSlotFrame = new JFrame("Time Slots");
		timeSlotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		timeSlotFrame.setSize(300, 400);
		timeSlotFrame.setLayout(new BorderLayout());

		// Add a label showing the selected timeframe
		JLabel timeframeLabel = new JLabel("Date: " + selectedDate);
		timeframeLabel.setFont(new Font("Arial", Font.BOLD, 14));
		timeframeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeSlotFrame.add(timeframeLabel, BorderLayout.NORTH);
			
		// Create a panel for time slots
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// Define time slot generation logic
		int endHour = 23; // End at 10:00 PM
		int intervalMinutes = 90; // Each slot is 1 hour 30 minutes
		int gapMinutes = 15; // 15 minutes between slots

		int currentHour = 7;
		int currentMinute = 30;

		while (currentHour < endHour || (currentHour == endHour && currentMinute == 0)) {
				// Calculate the start and end times for the slot
				int endSlotHour = currentHour + (currentMinute + intervalMinutes) / 60;
				int endSlotMinute = (currentMinute + intervalMinutes) % 60;

				String startTime = String.format("%02d:%02d", currentHour, currentMinute);
				String endTime = String.format("%02d:%02d", endSlotHour, endSlotMinute);

				// Create a button for the time slot
				JButton timeSlotButton = new JButton(startTime + " - " + endTime);
				timeSlotButton.addActionListener(e -> openRoomSelectionMenu(selectedDate, timeSlotButton.getText(), idNum));
				panel.add(timeSlotButton);

				// Move to the next time slot (add interval + gap)
				int totalMinutes = currentMinute + intervalMinutes + gapMinutes;
				currentHour += totalMinutes / 60;
				currentMinute = totalMinutes % 60;
		}

		// Add the panel to a scroll pane
		JScrollPane scrollPane = new JScrollPane(panel);
		timeSlotFrame.add(scrollPane, BorderLayout.CENTER);
		timeSlotFrame.setVisible(true);
	}

	private static void openRoomSelectionMenu(List<LocalDate> selectedDate, String timeSlot, int idNum) {
		JFrame roomFrame = new JFrame("Room Selection");
		roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		roomFrame.setSize(600, 300);
		roomFrame.setLayout(new BorderLayout());
			
		// Label showing the selected date and time slot
		JLabel label = new JLabel("Date: " + selectedDate + " | Slot: " + timeSlot);
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setHorizontalAlignment(SwingConstants.CENTER);
			
		// Panel for filters
		JPanel filterPanel = new JPanel(new GridLayout(4, 2, 10, 10));
			
		// Dropdown to select room type
		JLabel categoryLabel = new JLabel("Room Type:");
		JComboBox<String> categoryDropdown = new JComboBox<>(new String[]{"Classroom", "Laboratory"});
			
		// Input for building
		JLabel buildingLabel = new JLabel("Building:");
		JTextField buildingField = new JTextField();

		// Input for max capacity
		JLabel maxCapacityLabel = new JLabel("Max Capacity:");
		JTextField maxCapacityField = new JTextField();
			
		// Input for tags
		JLabel tagsLabel = new JLabel("Tags (comma-separated):");
		JTextField tagsField = new JTextField();
			
		// Add components to filter panel
		filterPanel.add(categoryLabel);
		filterPanel.add(categoryDropdown);
		filterPanel.add(buildingLabel);
		filterPanel.add(buildingField);
		filterPanel.add(maxCapacityLabel);
		filterPanel.add(maxCapacityField);
		filterPanel.add(tagsLabel);
		filterPanel.add(tagsField);
			
		// Proceed button to apply filters
		JButton proceedButton = new JButton("Show Matching Rooms");
		proceedButton.addActionListener(e -> {
			try {
				String selectedType = (String) categoryDropdown.getSelectedItem();
				String dbType = selectedType.equalsIgnoreCase("Classroom") ? "classroom" : "laboratory";
				int maxCapacity = Integer.parseInt(maxCapacityField.getText().trim());
				String building = buildingField.getText().trim();
				String tags = tagsField.getText().trim();
		
				// Call showRooms with the filters
				showRooms(roomFrame, dbType, maxCapacity, building, tags, selectedDate, timeSlot, idNum);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(roomFrame, "Please enter a valid number for max capacity.");
			}
		});
			
		// Add components to the frame
		roomFrame.add(label, BorderLayout.NORTH);
		roomFrame.add(filterPanel, BorderLayout.CENTER);
		roomFrame.add(proceedButton, BorderLayout.SOUTH);
		roomFrame.setVisible(true);
	}
		
	private static ArrayList<String[]> fetchRoomDetailsWithCategory(String category, int minCapacity, String building, String tags) {
		ArrayList<String[]> rooms = new ArrayList<>();
		String[] tagArray = tags.isEmpty() ? new String[0] : tags.split(",");
		StringBuilder tagCondition = new StringBuilder();
	
		// Build condition for tags
		for (int i = 0; i < tagArray.length; i++) {
			tagCondition.append("LOWER(tags) LIKE ?");
			if (i < tagArray.length - 1) {
				tagCondition.append(" AND ");
			}
		}
		
		String fetchQuery = "SELECT room_name, max_capacity, building, tags, category FROM rooms WHERE LOWER(category) = ? AND max_capacity >= ? AND LOWER(building) LIKE ?";
		if (tagCondition.length() > 0) {
			fetchQuery += " AND " + tagCondition;
		}
		
		System.out.println("Final SQL Query: " + fetchQuery);
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pstmt = conn.prepareStatement(fetchQuery)) {
		
			// Set query parameters
			pstmt.setString(1, category.toLowerCase());
			pstmt.setInt(2, minCapacity);
			pstmt.setString(3, "%" + building.trim().toLowerCase() + "%");
		
			int paramIndex = 4;
			for (String tag : tagArray) {
				pstmt.setString(paramIndex++, "%" + tag.trim().toLowerCase() + "%");
			}
		
			// Execute query and fetch results
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String roomName = rs.getString("room_name");
				String capacity = rs.getString("max_capacity");
				String roomBuilding = rs.getString("building");
				String roomTags = rs.getString("tags");
				String roomCategory = rs.getString("category");
				rooms.add(new String[]{roomName, capacity, roomBuilding, roomTags, roomCategory});
			}
		
		} catch (SQLException e) {
			System.err.println("Error executing query: " + e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("Rooms fetched: " + rooms.size());
		return rooms;
	}
		
		
	private static void showRooms(JFrame parentFrame, String type, int maxCapacity, String building, String tags, List<LocalDate> selectedDate, String timeSlot, int idNum) {
		parentFrame.dispose();
		
		JFrame roomFrame = new JFrame("Matching Rooms");
		roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		roomFrame.setSize(800, 400);
		roomFrame.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("Rooms matching your criteria:");
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		String[] columnNames = {"Room Name", "Max Capacity", "Building", "Tags", "Category", "Status"};
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		JTable roomTable = new JTable(tableModel);
		
		ArrayList<String[]> roomDetails = fetchRoomDetailsWithCategory(type, maxCapacity, building, tags);
		
		if (roomDetails.isEmpty()) {
			JOptionPane.showMessageDialog(roomFrame, "No matching rooms found. Please adjust your criteria.", "No Results", JOptionPane.INFORMATION_MESSAGE);
		} else {
			for (String[] roomDetail : roomDetails) {
				String roomName = roomDetail[0];
				String capacity = roomDetail[1];
				String roomBuilding = roomDetail[2];
				String roomTags = roomDetail[3];
				String category = roomDetail[4];
				boolean isBooked = isRoomBooked(selectedDate, timeSlot, roomName, roomBuilding);
		
				tableModel.addRow(new Object[]{roomName, capacity, roomBuilding, roomTags, category, isBooked ? "Booked" : "Available"});
			}
		}
		
		JScrollPane scrollPane = new JScrollPane(roomTable);
		JButton bookButton = new JButton("Book Room");
		
		bookButton.addActionListener(e -> {
			int selectedRow = roomTable.getSelectedRow();
			if (selectedRow != -1 && !selectedDate.isEmpty()) {
				String roomName = (String) tableModel.getValueAt(selectedRow, 0);
				String timeSlots = timeSlot;
				try {
					RoomBooking.insertRegularSchedule(idNum, selectedDate, timeSlots, roomName);
					JOptionPane.showMessageDialog(roomFrame, "Regular schedule added successfully.");
					roomFrame.dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(roomFrame, "An error occurred. Please try again.");
					ex.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(roomFrame, "Please select a room and at least one date.");
			}
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(bookButton);
		
		roomFrame.add(label, BorderLayout.NORTH);
		roomFrame.add(scrollPane, BorderLayout.CENTER);
		roomFrame.add(buttonPanel, BorderLayout.SOUTH);
		roomFrame.setVisible(true);
	}
		

	// Method to check if a room is booked for a given date and time slot
	private static boolean isRoomBooked(List<LocalDate> dates, String timeSlot, String room, String building) {
		String checkQuery = "SELECT COUNT(*) FROM approvedBookings WHERE booking_date = ? AND time_slot = ? AND room_name = ? AND building = ?";
			
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
				 
			for (LocalDate date : dates) { // Iterate through all the dates
				pstmt.setDate(1, java.sql.Date.valueOf(date)); // Set the date
				pstmt.setString(2, timeSlot); // Set the time slot
				pstmt.setString(3, room); // Set the room name
				pstmt.setString(4, building); // Set the building name
		
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next() && rs.getInt(1) > 0) {
						return true; // Room is booked for this date
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Room is not booked for any of the given dates
	}
		
		
	public static List<LocalDate> getSelectedDates() {
		return selectedDates;
	}

	public static String getSelectedTimeSlot() {
		return selectedTimeSlot;
	}

	public static String getSelectedRoom() {
		return selectedRoom;
	}

	// Setters to update selections
	public static void setSelectedDates(List<LocalDate> dates) {
		selectedDates = dates; // New dates are stored
	}
		
	public static void setSelectedTimeSlot(String timeSlot) {
		selectedTimeSlot = timeSlot; // New time slot is stored
	}
		
	public static void setSelectedRoom(String room) {
		selectedRoom = room; // New room name is stored
	}
		
	public static class RoomBooking {
		private LocalDate date;
		private String timeSlot;
		private String room;
		private boolean booked;

		public RoomBooking(LocalDate date, String timeSlot, String room, boolean booked) {
			this.date = date;
			this.timeSlot = timeSlot;
			this.room = room;
			this.booked = booked;
		}

		public LocalDate getDate() {
			return date;
		}

		public String getTimeSlot() {
			return timeSlot;
		}

		public String getRoom() {
			return room;
		}

		public boolean isBooked() {
			return booked;
		}

		public void setBooked(boolean booked) {
			this.booked = booked;
		}

	public static void insertRegularSchedule(int idNum, List<LocalDate> dates, String timeSlot, String roomName) {
		String datesString = dates.stream()
		.map(LocalDate::toString)
		.collect(Collectors.joining(",")); // Convert dates to comma-separated string

		// Query to check if a schedule exists for the professor
		String checkQuery = "SELECT id FROM regularSchedules WHERE professor_id = ?";
		// Query to update an existing schedule by ID
		String updateQuery = "UPDATE regularSchedules SET schedule_dates = ?, time_slot = ?, room_name = ? WHERE id = ?";
		// Query to insert a new schedule
		String insertQuery = "INSERT INTO regularSchedules (professor_id, professor_name, schedule_dates, time_slot, room_name) " +
		   "VALUES (?, (SELECT CONCAT(firstName, ' ', lastName) FROM users WHERE idNumber = ?), ?, ?, ?)";

		try (Connection conn = DatabaseHelper.getConnection()) {
			// Check if the schedule already exists for the professor
			int scheduleId = -1; // Placeholder for the existing schedule ID
			try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
				checkStmt.setInt(1, idNum);
				try (ResultSet rs = checkStmt.executeQuery()) {
					if (rs.next()) {
					scheduleId = rs.getInt("id"); // Get the ID of the existing record
					}
				}
			}

			if (scheduleId != -1) {
				// Update the existing schedule using its ID
				try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
					updateStmt.setString(1, datesString);
					updateStmt.setString(2, timeSlot);
					updateStmt.setString(3, roomName);
					updateStmt.setInt(4, scheduleId);
					updateStmt.executeUpdate();
					System.out.println("Regular schedule updated successfully.");
				}
			} else {
				// Insert a new schedule if no existing schedule is found
				try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
					insertStmt.setInt(1, idNum);
					insertStmt.setInt(2, idNum);
					insertStmt.setString(3, datesString);
					insertStmt.setString(4, timeSlot);
					insertStmt.setString(5, roomName);
					insertStmt.executeUpdate();
					System.out.println("Regular schedule inserted successfully.");
				}
			}
		} catch (SQLException e) {
		System.err.println("Error handling regular schedule.");
		e.printStackTrace();}
		}
	}
}
