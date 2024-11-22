import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CalendarWithRoomSelection {
    private static LocalDate startDate = null;
    private static LocalDate endDate = null;

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
        parentFrame.dispose(); // Close the previous frame
    
        JFrame roomListFrame = new JFrame(type + " List");
        roomListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roomListFrame.setSize(300, 200);
    
        JLabel titleLabel = new JLabel(type + " Available");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
    
        for (String room : rooms) {
            JButton roomButton = new JButton(room);
            roomButton.addActionListener(e -> {
                // Show final confirmation dialog
                JOptionPane.showMessageDialog(roomListFrame,
                        "You have selected:\n" +
                                "Timeframe: " + startDate + " to " + endDate + "\n" +
                                "Time Slot: " + timeSlot + "\n" +
                                "Room: " + room,
                        "Selection Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
                roomListFrame.dispose(); // Close after confirmation
            });
            roomPanel.add(roomButton);
        }
    
        JScrollPane scrollPane = new JScrollPane(roomPanel);
    
        roomListFrame.setLayout(new BorderLayout());
        roomListFrame.add(titleLabel, BorderLayout.NORTH);
        roomListFrame.add(scrollPane, BorderLayout.CENTER);
        roomListFrame.setVisible(true);
    }
}    