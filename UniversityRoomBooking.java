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
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class UniversityRoomBooking {
    private static LocalDate selectedDate = null; // Single selected date
    private static ArrayList<RoomBooking> bookedRooms = new ArrayList<>();

    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase(); // Ensure the database is set up
        openCalendar(LocalDate.now().getMonthValue());
    }
    
    private static void openCalendar(int initialMonth) {
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
                openTimeSlotMenu(selectedDate); // Proceed to time slots
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
    
    // Other methods (time slots, room selection, etc.) remain unchanged


    private static void openTimeSlotMenu(LocalDate selectedDate) {
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
            timeSlotButton.addActionListener(e -> openRoomSelectionMenu(selectedDate, timeSlotButton.getText()));
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

    private static void openRoomSelectionMenu(LocalDate selectedDate, String timeSlot) {
        JFrame roomFrame = new JFrame("Room Selection");
        roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roomFrame.setSize(400, 200);
        roomFrame.setLayout(new BorderLayout());
    
        // Label showing the selected date and time slot
        JLabel label = new JLabel("Date: " + selectedDate + " | Slot: " + timeSlot);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    
        // Dropdown to select room type
        JComboBox<String> roomTypeDropdown = new JComboBox<>(new String[]{"Classrooms", "Laboratories"});
        roomTypeDropdown.setFont(new Font("Arial", Font.PLAIN, 12));
    
        // Button to proceed with room selection
        JButton proceedButton = new JButton("Select Room Type");
        proceedButton.addActionListener(e -> {
            String selectedType = (String) roomTypeDropdown.getSelectedItem();
            String dbType = selectedType.equalsIgnoreCase("Classrooms") ? "classroom" : "laboratory"; // Map dropdown value
            showRooms(roomFrame, dbType, selectedDate, timeSlot); // Fetch and display rooms
        });
    
        // Panel to hold dropdown and button
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(roomTypeDropdown);
        panel.add(proceedButton);
    
        // Add components to the frame
        roomFrame.add(label, BorderLayout.NORTH);
        roomFrame.add(panel, BorderLayout.CENTER);
        roomFrame.setVisible(true);
    }
    
    
    private static ArrayList<String> fetchRoomsFromDatabase(String category) {
        ArrayList<String> rooms = new ArrayList<>();
        String fetchQuery = "SELECT room_name FROM rooms WHERE LOWER(category) = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/room_db", "root", "cbinfom");
             PreparedStatement pstmt = conn.prepareStatement(fetchQuery)) {
            
            pstmt.setString(1, category.toLowerCase()); // Ensure case-insensitivity
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rooms.add(rs.getString("room_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    

        if (rooms.isEmpty()) {
            System.out.println("No rooms found for category: " + category);
        } else {
            System.out.println("Rooms fetched for category '" + category + "': " + rooms);
        }
        
        return rooms;
    }
    
    

private static void showRooms(JFrame parentFrame, String type, LocalDate selectedDate, String timeSlot) {
    parentFrame.dispose(); // Close the previous frame

    ArrayList<String> rooms = fetchRoomsFromDatabase(type); // Fetch rooms dynamically

    JFrame roomFrame = new JFrame(type + " Selection");
    roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    roomFrame.setSize(400, 200);
    roomFrame.setLayout(new BorderLayout());

    // Label showing selected date and time slot
    JLabel label = new JLabel("Date: " + selectedDate + " | Slot: " + timeSlot);
    label.setFont(new Font("Arial", Font.BOLD, 14));
    label.setHorizontalAlignment(SwingConstants.CENTER);

    // Dropdown for room selection
    JComboBox<String> roomDropdown = new JComboBox<>();
    for (String room : rooms) {
        boolean isBooked = isRoomBooked(selectedDate, timeSlot, room);
        roomDropdown.addItem(room + (isBooked ? " (Booked)" : ""));
    }

    // Book button
    JButton bookButton = new JButton("Book Room");
    bookButton.addActionListener(e -> {
        String selectedRoom = (String) roomDropdown.getSelectedItem();
        if (selectedRoom != null) {
            if (selectedRoom.endsWith("(Booked)")) {
                JOptionPane.showMessageDialog(roomFrame, "This room is already booked.");
            } else {
                // Extract room name from dropdown item
                String roomName = selectedRoom.replace(" (Booked)", "");
                bookedRooms.add(new RoomBooking(selectedDate, timeSlot, roomName, true));
                JOptionPane.showMessageDialog(roomFrame, "Room " + roomName + " successfully booked!");
                roomDropdown.removeItem(selectedRoom); // Remove or update the dropdown item
                roomDropdown.addItem(roomName + " (Booked)");
            }
        }
    });

    // Done button to finish booking
    JButton doneButton = new JButton("Done Booking");
    doneButton.addActionListener(e -> {
        if (!bookedRooms.isEmpty()) {
            for (RoomBooking booking : bookedRooms) {
                DatabaseHelper.insertBooking(booking.getDate(), booking.getTimeSlot(), booking.getRoom());
            }
            JOptionPane.showMessageDialog(roomFrame, "All bookings have been saved to the database.");
        } else {
            JOptionPane.showMessageDialog(roomFrame, "No bookings were made.");
        }
        roomFrame.dispose();
    });

    // Bottom panel for buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(bookButton);
    buttonPanel.add(doneButton);

    // Add components to the frame
    roomFrame.add(label, BorderLayout.NORTH);
    roomFrame.add(roomDropdown, BorderLayout.CENTER);
    roomFrame.add(buttonPanel, BorderLayout.SOUTH);
    roomFrame.setVisible(true);
}


    // Method to check if a room is booked for a given date and time slot
    private static boolean isRoomBooked(LocalDate date, String timeSlot, String room) {
        for (RoomBooking booking : bookedRooms) {
            if (booking.getDate().equals(date) && booking.getTimeSlot().equals(timeSlot) && booking.getRoom().equals(room)) {
                return booking.isBooked();
            }
        }
        return false;
    }

    // Method to remove booking when unbooking
    private static void removeBooking(LocalDate date, String timeSlot, String room) {
        bookedRooms.removeIf(booking -> booking.getDate().equals(date) && booking.getTimeSlot().equals(timeSlot) && booking.getRoom().equals(room));
    }

    // Method for the arraylist
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
    }
}
