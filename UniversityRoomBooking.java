import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class UniversityRoomBooking {
    private static LocalDate selectedDate = null; // Single selected date
    private static ArrayList<RoomBooking> bookedRooms = new ArrayList<>();

    public static void roomBookMain(int professorId) {
        DatabaseHelper.initializeDatabase();
        openCalendar(LocalDate.now().getMonthValue(), professorId);
    }
    
    private static void openCalendar(int initialMonth, int professorId) {
        JFrame frame = new JFrame("Calendar - Single Date Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                openTimeSlotMenu(selectedDate, professorId); // Pass professorId to time slot menu
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

    private static void openTimeSlotMenu(LocalDate selectedDate, int professorId) {
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

            String timeSlot = startTime + " - " + endTime;

            // Create button for the time slot
            JButton timeSlotButton = new JButton(timeSlot);
            timeSlotButton.addActionListener(e -> {
                openRoomBooking(timeSlot, professorId, selectedDate);
            });
            panel.add(timeSlotButton);

            // Update current time for next slot
            currentMinute += intervalMinutes + gapMinutes;
            if (currentMinute >= 60) {
                currentMinute -= 60;
                currentHour++;
            }
        }

        // Add the panel to a scroll pane for visibility
        timeSlotFrame.add(new JScrollPane(panel), BorderLayout.CENTER);
        timeSlotFrame.setVisible(true); // Show the time slots window
    }

    private static void openRoomBooking(String timeSlot, int professorId, LocalDate selectedDate) {
        // Fetch available room names from the database
        ArrayList<String> roomNames = DatabaseHelper.getRoomNames();
        
        // Create a frame for room booking
        JFrame roomFrame = new JFrame("Select a Room");
        roomFrame.setSize(400, 300);
        roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Display buttons for each room
        for (String roomName : roomNames) {
            JButton roomButton = new JButton(roomName);
            roomButton.addActionListener(e -> {
                DatabaseHelper.insertBooking(professorId, selectedDate, timeSlot, roomName);
                JOptionPane.showMessageDialog(roomFrame, "Room booked successfully!");
                roomFrame.dispose();
            });
            panel.add(roomButton);
        }

        roomFrame.add(new JScrollPane(panel));
        roomFrame.setVisible(true); // Show the room booking frame
    }

    // RoomBooking Class
    public static class RoomBooking {
        private LocalDate date;
        private String timeSlot;
        private String room;
        private boolean booked;
        private int professorId;

        public RoomBooking(LocalDate date, String timeSlot, String room, boolean booked, int professorId) {
            this.date = date;
            this.timeSlot = timeSlot;
            this.room = room;
            this.booked = booked;
            this.professorId = professorId;
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

        public int getProfessorId() {
            return professorId;
        }
    }

    // DatabaseHelper class using your provided code
    public static class DatabaseHelper {
        // JDBC connection URL to the ProgMP database
        private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
        private static final String DB_USER = "root";  // MySQL username
        private static final String DB_PASSWORD = "Vianca";  // MySQL password

        // Method to get a connection to the database
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }

        // Method to initialize the database
        public static void initializeDatabase() {
            try (Connection connection = getConnection()) {
                // Perform any database initialization here if needed
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static ArrayList<String> getRoomNames() {
            ArrayList<String> roomNames = new ArrayList<>();
            String query = "SELECT room_name FROM rooms";  // Assuming table name is 'rooms'

            try (Connection connection = getConnection();
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    roomNames.add(rs.getString("room_name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return roomNames;
        }

        public static void insertBooking(int professorId, LocalDate date, String timeSlot, String roomName) {
			String query = "INSERT INTO bookings (professor_id, booking_date, time_slot, room_name) VALUES (?, ?, ?, ?)";

			try (Connection connection = getConnection();
				 PreparedStatement stmt = connection.prepareStatement(query)) {

				stmt.setInt(1, professorId);  // Set professorId as the first parameter
				stmt.setDate(2, java.sql.Date.valueOf(date));  // Set booking date
				stmt.setString(3, timeSlot);  // Set time slot
				stmt.setString(4, roomName);  // Set room name

				stmt.executeUpdate();  // Execute the insert statement
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
}
