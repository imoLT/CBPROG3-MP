import java.sql.*;

public class DatabaseConnection {
    public static void main(String[] args) {
        // Declare the database connection details inside the method
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
        String dbUser = "root";  // Your MySQL username
        String dbPassword = "Vianca";  // Your MySQL password

        try {
            // Establish connection
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connection established!");

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
