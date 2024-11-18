import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CalendarWithRoomSelection {
    private static LocalDate startDate = null;
    private static LocalDate endDate = null;
    private static ArrayList<RoomBooking> bookedRooms = new ArrayList<>(); // To store all booked rooms

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Calendar with Date Range Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        // Get the current date
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        // Create a label for the month and year
        JLabel monthYearLabel = new JLabel(currentDate.getMonth() + " " + currentYear, SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(monthYearLabel, BorderLayout.NORTH);

        // Create a table model that prevents editing
        String[] columns = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent cell editing
            }
        };

        JTable calendarTable = new JTable(tableModel);

        // Fill the calendar with days
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        int startDay = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        // Adjust startDay to match Sunday as the first column
        startDay = (startDay % 7);

        // Fill the table with days
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
            tableModel.addRow(week); // Add the remaining days
        }

        // Add a custom cell renderer to highlight the selected timeframe
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Reset background
                c.setBackground(Color.WHITE);

                if (value != null && !value.toString().isEmpty() && startDate != null && endDate != null) {
                    int day = Integer.parseInt(value.toString());
                    LocalDate currentCellDate = LocalDate.of(currentYear, currentMonth, day);

                    if (!currentCellDate.isBefore(startDate) && !currentCellDate.isAfter(endDate)) {
                        // Highlight cells within the selected timeframe
                        c.setBackground(Color.CYAN);
                    }
                }

                return c;
            }
        });

        // Add the calendar to the frame
        JScrollPane scrollPane = new JScrollPane(calendarTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for buttons and labels
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Add a label to show the selected range
        JLabel rangeLabel = new JLabel("Select a start date and an end date.");
        rangeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(rangeLabel);

        // Create a button to confirm the timeframe
        JButton confirmButton = new JButton("Confirm Timeframe");
        confirmButton.setEnabled(false); // Initially disabled
        confirmButton.addActionListener(e -> {
            if (startDate != null && endDate != null) {
                openTimeSlotMenu(startDate, endDate);
            }
        });

        // Create a button to clear the timeframe
        JButton clearButton = new JButton("Clear Timeframe");
        clearButton.setEnabled(false); // Initially disabled
        clearButton.addActionListener(e -> {
            startDate = null;
            endDate = null;
            rangeLabel.setText("Select a start date and an end date.");
            calendarTable.repaint(); // Repaint the calendar to clear highlights
            confirmButton.setEnabled(false);
            clearButton.setEnabled(false);
        });

        // Add buttons to the panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(confirmButton);
        buttonPanel.add(clearButton);
        bottomPanel.add(buttonPanel);

        // Add a mouse listener to the table for date selection
        calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = calendarTable.rowAtPoint(e.getPoint());
                int col = calendarTable.columnAtPoint(e.getPoint());
                Object value = calendarTable.getValueAt(row, col);

                if (value != null && !value.toString().isEmpty()) {
                    int day = Integer.parseInt(value.toString());
                    LocalDate selectedDate = LocalDate.of(currentYear, currentMonth, day);

                    if (startDate == null) {
                        startDate = selectedDate;
                        rangeLabel.setText("Start Date: " + startDate);
                    } else if (endDate == null) {
                        endDate = selectedDate;

                        if (endDate.isBefore(startDate)) {
                            // Swap dates if end date is earlier
                            LocalDate temp = startDate;
                            startDate = endDate;
                            endDate = temp;
                        }

                        rangeLabel.setText("Start Date: " + startDate + " | End Date: " + endDate);
                        calendarTable.repaint(); // Repaint to apply highlighting
                        confirmButton.setEnabled(true);
                        clearButton.setEnabled(true);
                    } else {
                        // Reset if both dates are already set
                        startDate = selectedDate;
                        endDate = null;
                        rangeLabel.setText("Start Date: " + startDate);
                        calendarTable.repaint(); // Repaint to clear highlighting
                        confirmButton.setEnabled(false);
                        clearButton.setEnabled(false);
                    }
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    private static void openTimeSlotMenu(LocalDate startDate, LocalDate endDate) {
        // Create a new frame for time slots
        JFrame timeSlotFrame = new JFrame("Time Slots");
        timeSlotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        timeSlotFrame.setSize(300, 400);
        timeSlotFrame.setLayout(new BorderLayout());

        // Add a label showing the selected timeframe
        JLabel timeframeLabel = new JLabel("Timeframe: " + startDate + " to " + endDate);
        timeframeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeframeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeSlotFrame.add(timeframeLabel, BorderLayout.NORTH);
        
        // Create a panel for time slots
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Define time slot generation logic
        int startHour = 6; // Start at 6:00 AM
        int endHour = 22; // End at 10:00 PM
        int intervalMinutes = 90; // Each slot is 1 hour 30 minutes
        int gapMinutes = 15; // 15 minutes between slots

        int currentHour = startHour;
        int currentMinute = 0;

        while (currentHour < endHour || (currentHour == endHour && currentMinute == 0)) {
            // Calculate the start and end times for the slot
            int endSlotHour = currentHour + (currentMinute + intervalMinutes) / 60;
            int endSlotMinute = (currentMinute + intervalMinutes) % 60;

            String startTime = String.format("%02d:%02d", currentHour, currentMinute);
            String endTime = String.format("%02d:%02d", endSlotHour, endSlotMinute);

            // Create a button for the time slot
            JButton timeSlotButton = new JButton(startTime + " - " + endTime);
            timeSlotButton.addActionListener(e -> openRoomSelectionMenu(startDate, endDate, timeSlotButton.getText()));
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

    private static void openRoomSelectionMenu(LocalDate startDate, LocalDate endDate, String timeSlot) {
        // Create a new frame for room selection
        JFrame roomFrame = new JFrame("Room Selection");
        roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roomFrame.setSize(400, 300);
    
        JLabel label = new JLabel("Timeframe: " + startDate + " to " + endDate + " | Slot: " + timeSlot);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    
        // Create buttons for classrooms and laboratories
        JButton classroomsButton = new JButton("Classrooms");
        classroomsButton.addActionListener(e -> showRooms(roomFrame, "Classrooms", new String[]{"MRE 111/112", "MRE 113/114", "MRE 201"}, startDate, endDate, timeSlot));
    
        JButton laboratoriesButton = new JButton("Laboratories");
        laboratoriesButton.addActionListener(e -> showRooms(roomFrame, "Laboratories", new String[]{"MRELABA", "MRE309", "MRE310"}, startDate, endDate, timeSlot));
    
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(classroomsButton);
        buttonPanel.add(laboratoriesButton);
    
        roomFrame.setLayout(new BorderLayout());
        roomFrame.add(label, BorderLayout.NORTH);
        roomFrame.add(buttonPanel, BorderLayout.CENTER);
        roomFrame.setVisible(true);
    }
    
    private static void showRooms(JFrame parentFrame, String type, String[] rooms, LocalDate startDate, LocalDate endDate, String timeSlot) {
        parentFrame.dispose(); // Use this to close the previous frame

        // Create the frame for room selection
        JFrame roomListFrame = new JFrame(type + " List");
        roomListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roomListFrame.setSize(400, 300);

        // Use a JPanel with GridLayout for room layout
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columns, 10px gap between items

        // Loop through rooms to create a panel for each room
        for (String room : rooms) {
            JPanel roomPanelItem = new JPanel();
            roomPanelItem.setPreferredSize(new Dimension(150, 100)); // Room panel size

            // Check if the room is booked
            boolean isBookedForSlot = isRoomBooked(startDate, timeSlot, room);
            
            if (isBookedForSlot) {
                roomPanelItem.setBackground(Color.RED); // Room already booked (red)
            } else {
                roomPanelItem.setBackground(Color.GREEN); // Available room (green)
            }

            // Create label for the room
            JLabel roomLabel = new JLabel(room, JLabel.CENTER);
            roomLabel.setForeground(Color.WHITE);
            roomLabel.setPreferredSize(new Dimension(140, 90)); // Label size to fit within the panel

            // Add label to the room panel
            roomPanelItem.add(roomLabel);

            // Add action when room is clicked
            roomPanelItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Only change color if booking is not done
                    if (!isBookedForSlot) {
                        // Toggle room booking status (Green to Red and vice versa)
                        if (roomPanelItem.getBackground() == Color.GREEN) {
                            roomPanelItem.setBackground(Color.RED); // Book the room (red)
                            roomLabel.setText(roomLabel.getText() + " (Booked)"); // Update label text

                            // Add this booking to the bookedRooms list
                            bookedRooms.add(new RoomBooking(startDate, timeSlot, room, true));
                        } else {
                            roomPanelItem.setBackground(Color.GREEN); // Unbook the room (green)
                            roomLabel.setText(roomLabel.getText().replace(" (Booked)", "")); // Update label text

                            // Remove the booking from the list
                            removeBooking(startDate, timeSlot, room);
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "Booking Finalized");
                    }
                }
            });

            // Add the room panel item to the main room panel
            roomPanel.add(roomPanelItem);
        }

        // Create a panel for the button at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // "Done Booking" button
        JButton doneButton = new JButton("Done Booking");
        doneButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(parentFrame, "Booking finished.");
            doneButton.setEnabled(false); // Disable the button after pressing
        });

        buttonPanel.add(doneButton);

        // Add components to the frame
        roomListFrame.add(roomPanel, BorderLayout.CENTER);
        roomListFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Make the frame visible
        roomListFrame.setVisible(true);
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
