import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class UniversityRoomBooking {
    private static LocalDate selectedDate = null;
	private static int idNum;
	
	public void UniverisityRoomBooking(int idNum){
		this.idNum = idNum;
	}

    public static void roomBookMain(int idNum) {
        DatabaseHelper.initializeDatabase();
        openCalendar(LocalDate.now().getMonthValue(), idNum);
    }
    
    private static void openCalendar(int initialMonth, int idNum) {
        JFrame frame = new JFrame("Calendar - Single Date Selection");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        int currentYear = LocalDate.now().getYear();

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

        // Highlight the selected date
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Color.WHITE); // Reset background

                if (value != null && !value.toString().isEmpty() && selectedDate != null) {
                    int day = Integer.parseInt(value.toString());
                    int selectedMonth = monthDropdown.getSelectedIndex() + 1;
                    LocalDate cellDate = LocalDate.of(currentYear, selectedMonth, day);

                    if (cellDate.equals(selectedDate)) {
                        c.setBackground(Color.CYAN); // Highlight the selected date
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

        JLabel dateLabel = new JLabel("Select a date.", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bottomPanel.add(dateLabel);

        JButton confirmButton = new JButton("Confirm Date");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> {
            if (selectedDate != null) {
                frame.dispose(); // Close calendar
                openTimeSlotMenu(selectedDate, idNum); // Pass idNum to time slot menu
            }
        });

        JButton clearButton = new JButton("Clear Date");
        clearButton.setEnabled(false);
        clearButton.addActionListener(e -> {
            selectedDate = null;
            dateLabel.setText("Select a date.");
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
                    selectedDate = LocalDate.of(currentYear, selectedMonth, day);
                    dateLabel.setText("Selected Date: " + selectedDate);
                    calendarTable.repaint(); // Highlight selected date
                    confirmButton.setEnabled(true);
                    clearButton.setEnabled(true);
                }
            }
        });

        updateCalendar.run(); // Populate calendar initially
        frame.setVisible(true); // Show frame
    }

    private static void openTimeSlotMenu(LocalDate selectedDate, int idNum) {
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
			timeSlotButton.addActionListener(e -> {
				// Close the time slot frame
				timeSlotFrame.dispose();
				openRoomSelectionMenu(selectedDate, timeSlotButton.getText(), idNum);
			});
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

    private static void openRoomSelectionMenu(LocalDate selectedDate, String timeSlot, int idNum) {
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

				// Check if the building exists in the database
				if (!buildingExist(building)) {
					JOptionPane.showMessageDialog(roomFrame, "Building " + building + " not found anywhere in the database", "Building Not Found", JOptionPane.WARNING_MESSAGE);
				} else {
					// Call showRooms with the filters
					showRooms(roomFrame, dbType, maxCapacity, building, tags, selectedDate, timeSlot, idNum);
				}
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
        // Split tags by commas and trim whitespace
        String[] tagArray = tags.split(",");
        StringBuilder tagCondition = new StringBuilder();
        for (int i = 0; i < tagArray.length; i++) {
            tagCondition.append("LOWER(tags) LIKE ?");
            if (i < tagArray.length - 1) {
                tagCondition.append(" AND ");
            }
        }
    
        String fetchQuery = "SELECT room_name, max_capacity, building, tags, category FROM rooms " +
                            "WHERE LOWER(category) = ? AND max_capacity >= ? AND LOWER(building) LIKE ? " +
                            (tagCondition.length() > 0 ? " AND " + tagCondition : "");
    
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             PreparedStatement pstmt = conn.prepareStatement(fetchQuery)) {
    
            pstmt.setString(1, category.toLowerCase());
            pstmt.setInt(2, minCapacity);
            pstmt.setString(3, "%" + building.toLowerCase() + "%");
    
            // Set dynamic tag conditions
            int paramIndex = 4;
            for (String tag : tagArray) {
                pstmt.setString(paramIndex++, "%" + tag.trim().toLowerCase() + "%");
            }
    
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
            e.printStackTrace();
        }
        return rooms;
    }
    
     
    private static void showRooms(JFrame parentFrame, String type, int maxCapacity, String building, String tags, LocalDate selectedDate, String timeSlot, int idNum) {
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
    
        // Fetch rooms from the database that match the filters
        ArrayList<String[]> roomDetails = fetchRoomDetailsWithCategory(type, maxCapacity, building, tags);
    
        for (String[] roomDetail : roomDetails) {
            String roomName = roomDetail[0];
            String capacity = roomDetail[1];
            String roomBuilding = roomDetail[2];
            String roomTags = roomDetail[3];
            String category = roomDetail[4];
            boolean isBooked = isRoomBooked(selectedDate, timeSlot, roomName, roomBuilding);
    
            // Add room data to the table
            tableModel.addRow(new Object[]{roomName, capacity, roomBuilding, roomTags, category, isBooked ? "Booked" : "Available"});
        }
    
        JScrollPane scrollPane = new JScrollPane(roomTable);
    
        JButton bookButton = new JButton("Book Room");
        bookButton.addActionListener(e -> {
			int selectedRow = roomTable.getSelectedRow();
			if (selectedRow != -1) {
				String roomName = (String) tableModel.getValueAt(selectedRow, 0);
				String roomCategory = (String) tableModel.getValueAt(selectedRow, 4);
				String roomBuilding = (String) tableModel.getValueAt(selectedRow, 2);
				String status = (String) tableModel.getValueAt(selectedRow, 5);

				// Check if the room exists in the database
				if (!roomExist(roomName)) {
					JOptionPane.showMessageDialog(roomFrame, "The room " + roomName + " does not exist in the database. Please choose another room.");
				}
				else{
					if ("Booked".equals(status)) {
						JOptionPane.showMessageDialog(roomFrame, "This room is already booked. Please choose another room.");
					} else {
						try {
							RoomBooking.insertBooking(idNum, selectedDate, timeSlot, roomName, roomCategory, roomBuilding);
							JOptionPane.showMessageDialog(roomFrame,
									"Room request sent to admin!\n" +
									"Details:\n" +
									"Booked by: " + idNum + "\n" +
									"Room: " + roomName + "\n" +
									"Category: " + roomCategory + "\n" +
									"Building: " + roomBuilding + "\n" +
									"Date: " + selectedDate + "\n" +
									"Time Slot: " + timeSlot,
									"Booking Confirmation",
									JOptionPane.INFORMATION_MESSAGE);
							roomFrame.dispose();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(roomFrame, "An error occurred while booking the room. Please try again.");
							ex.printStackTrace();
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(roomFrame, "Please select a room to book.");
			}
		});
    
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(bookButton);
    
        roomFrame.add(label, BorderLayout.NORTH);
        roomFrame.add(scrollPane, BorderLayout.CENTER);
        roomFrame.add(buttonPanel, BorderLayout.SOUTH);
        roomFrame.setVisible(true);
    }
    

	private static boolean roomExist(String roomName) {
		String query = "SELECT COUNT(*) FROM rooms WHERE room_name = ?";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pstmt = conn.prepareStatement(query)) {
			
			pstmt.setString(1, roomName);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next() && rs.getInt(1) > 0) {
				return true; // Room exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Room does not exist
	}
	
	private static boolean buildingExist(String building) {
		String checkBuildingQuery = "SELECT COUNT(*) FROM rooms WHERE LOWER(building) = ?";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pstmt = conn.prepareStatement(checkBuildingQuery)) {

			pstmt.setString(1, building.toLowerCase()); 
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;  
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;  
	}



    // Method to check if a room is booked for a given date and time slot
	private static boolean isRoomBooked(LocalDate date, String timeSlot, String room, String building) {
		// Query to check both the approvedBookings and regularSchedules tables
		String checkQuery = 
			"SELECT COUNT(*) FROM (" +
			"    SELECT 1 FROM approvedBookings WHERE booking_date = ? AND time_slot = ? AND room_name = ? AND building = ?" +
			"    UNION" +
			"    SELECT 1 FROM regularSchedules WHERE FIND_IN_SET(?, schedule_dates) > 0 AND time_slot = ? AND room_name = ?" +
			") AS combinedBookings";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
			 PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {

			// For approvedBookings table
			pstmt.setDate(1, Date.valueOf(date));  // Using booking_date column for approvedBookings
			pstmt.setString(2, timeSlot);
			pstmt.setString(3, room);
			pstmt.setString(4, building);

			// For regularSchedules table
			pstmt.setString(5, date.toString());  // Converting date to string for use with FIND_IN_SET
			pstmt.setString(6, timeSlot);
			pstmt.setString(7, room);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true; // Room is booked in either of the tables
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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

        public static void insertBooking(int idNum, LocalDate date, String timeSlot, String roomName, String roomCategory, String roomBuilding) {
            String checkQuery = "SELECT COUNT(*) FROM users WHERE idNumber = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
        
                pstmt.setInt(1, idNum);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Proceed with booking insertion into unapproveBookings
                    String insertQuery = "INSERT INTO unapproveBookings (professor_id, booking_date, time_slot, room_name, room_category, building) " +
                                         "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPstmt = conn.prepareStatement(insertQuery)) {
                        insertPstmt.setInt(1, idNum);
                        insertPstmt.setDate(2, Date.valueOf(date));  // Convert LocalDate to SQL Date
                        insertPstmt.setString(3, timeSlot);
                        insertPstmt.setString(4, roomName);
                        insertPstmt.setString(5, roomCategory);
                        insertPstmt.setString(6, roomBuilding);
                        insertPstmt.executeUpdate();
                        System.out.println("Booking inserted into unapproveBookings table.");
                    } catch (SQLException e) {
                        System.out.println("Error inserting booking into unapproveBookings.");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Error: professor_id does not exist in users table.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }        
    }        
}
