import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DisplayNonApprovedAccounts {

    public static void main(String[] args) {
        // Declare the database connection details inside the method
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
        String dbUser = "root";  // Your MySQL username
        String dbPassword = "Vianca";  // Your MySQL password

        // Create frame for displaying data
        JFrame frame = new JFrame("Non-Approved Users");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Number");
        model.addColumn("Password");
        model.addColumn("Role");

        // Create JTable
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true); 
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Query to select all data from the nonApprovedUsers table
        String sql = "SELECT * FROM nonApprovedUsers";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Fetch and add data to table
            while (rs.next()) {
                String idNumber = rs.getString("id_number");
                String password = rs.getString("password");
                String role = rs.getString("role");
                model.addRow(new Object[]{idNumber, password, role});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Make the frame visible
        frame.setVisible(true);
    }
}
