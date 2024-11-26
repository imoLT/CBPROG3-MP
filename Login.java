import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login implements ActionListener {
    private static JLabel idNumLabel, passLabel;
    private static JTextField idNum;
    private static JPasswordField password;
    private static JButton login;
    private static JFrame frame;

    public Login() {}

    public void actionPerformed(ActionEvent e) {
        String enteredId = idNum.getText();
        String enteredPassword = new String(password.getPassword());

        // Validate if fields are empty
        if (enteredId.equals("") || enteredPassword.equals("")) {
            if (enteredId.equals("") && enteredPassword.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter your ID number and password.");
            } else if (enteredId.equals("")) {
                JOptionPane.showMessageDialog(null, "ID number field is empty. Please try again!");
            } else {
                JOptionPane.showMessageDialog(null, "Password field is empty. Please enter a password and try again!");
            }
        } else {
            // Query the database for the user using the DatabaseHelper class
            try (Connection conn = DatabaseHelper.getConnection()) {
                String sql = "SELECT * FROM nonApprovedUsers WHERE id_number = ? AND password = ?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, enteredId);
                    pst.setString(2, enteredPassword);

                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        // If user found, log them in
                        JOptionPane.showMessageDialog(null, "You are now logged in.");
                        frame.dispose(); // Close the login window
                        // Additional login actions can be added here based on the user role, etc.
                    } else {
                        // If no user found with matching credentials
                        JOptionPane.showMessageDialog(null, "Invalid ID or password. Please try again.");
                    }
                }
            } catch (SQLException ex) {
                // Handle SQL exception if connection fails
                JOptionPane.showMessageDialog(null, "Error connecting to database: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Create the main frame for the login window
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocation(430, 100);

        // Create panel and set layout to GridBagLayout
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        frame.add(panel);

        // Create a GridBagConstraints instance for controlling layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;  // Start from the first column
        gbc.gridy = 0;  // Start from the first row
        gbc.anchor = GridBagConstraints.WEST;  // Align components to the left
        gbc.insets = new Insets(5, 5, 5, 5);  // Add some space between components

        // ID Number label and textfield
        idNumLabel = new JLabel("ID Number: ");
        panel.add(idNumLabel, gbc);

        gbc.gridx = 1;  // Move to the next column for the textfield
        idNum = new JTextField(10);
        panel.add(idNum, gbc);

        // Move to the next row
        gbc.gridy = 1;
        gbc.gridx = 0;

        // Password label and textfield
        passLabel = new JLabel("Password: ");
        panel.add(passLabel, gbc);

        gbc.gridx = 1;  // Move to the next column for the password field
        password = new JPasswordField(10);
        panel.add(password, gbc);

        // Move to the next row for the login button
        gbc.gridy = 2;

        // Button to finalize login
        login = new JButton("Login");
        login.addActionListener(new Login());  // Attach the Login listener
        panel.add(login, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }
}
