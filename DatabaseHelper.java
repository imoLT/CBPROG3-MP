import java.sql.*;
import java.time.LocalDate;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/room_db";
    private static final String DB_USERNAME = "root"; // Your MySQL username
    private static final String DB_PASSWORD = "cbinfom"; // Your MySQL password

    // Initialize the database and ensure the table exists
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            if (conn != null) {
                String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS bookings (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        selected_date DATE NOT NULL,
                        time_slot VARCHAR(255) NOT NULL,
                        room_name VARCHAR(255) NOT NULL
                    )
                """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableQuery);
                    System.out.println("Database initialized successfully.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert a booking into the database
    public static void insertBooking(LocalDate date, String timeSlot, String roomName) {
        String insertQuery = "INSERT INTO bookings (selected_date, time_slot, room_name) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setString(2, timeSlot);
            pstmt.setString(3, roomName);
            pstmt.executeUpdate();
            System.out.println("Booking added to database: " + date + ", " + timeSlot + ", " + roomName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
