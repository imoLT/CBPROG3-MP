import java.sql.*;
import java.time.LocalDate;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
    private static final String DB_USER = "root";  // MySQL username
    private static final String DB_PASSWORD = "Vianca";  // MySQL password
	
	public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to database.");
        }
    }
	
	// Method to get a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}

