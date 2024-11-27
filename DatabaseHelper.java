import java.sql.*;

public class DatabaseHelper {
    // JDBC connection URL to the ProgMP database
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
    private static final String DB_USER = "root";  // MySQL username
    private static final String DB_PASSWORD = "Vianca";  // MySQL password

    // Method to get a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
