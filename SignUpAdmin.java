import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUpAdmin implements ActionListener, SignUpInterface {
    private static JFrame frame;
    private static JTextField idNumField;
    private static JPasswordField passwordField;
    private static JButton finishButton;

    public void signUpMain() { 
        frame = new JFrame("Sign-Up a New Admin");
        JPanel panel = new JPanel(new GridBagLayout());
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add ID Number field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Number:"), gbc);
        gbc.gridx = 1;
        idNumField = new JTextField(15);
        panel.add(idNumField, gbc);

        // Add Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Add Finish button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        finishButton = new JButton("Finish");
        panel.add(finishButton, gbc);

        finishButton.addActionListener(this);  

        frame.setVisible(true);
    }

    public boolean isInputValid() {
        return !idNumField.getText().equals("") &&
               passwordField.getPassword().length > 0 &&
               idNumField.getText().length() == 8;
    }

    public void actionPerformed(ActionEvent e) {
        char[] password = passwordField.getPassword();

        if (isInputValid()) {
            System.out.println("Inputs are valid. Proceeding to insert into database...");

            try (Connection connection = DatabaseHelper.getConnection()) {
                // SQL query to insert the user with "admin" role
                String sql = "INSERT INTO users (idnumber, password, role) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, idNumField.getText());
                statement.setString(2, new String(password));
                
                // Automatically set the role as "admin"
                statement.setString(3, "Program Admin"); // Set the role to "admin"

                int rowsInserted = statement.executeUpdate();
                System.out.println("Rows inserted: " + rowsInserted);
                
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "New admin account created successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error connecting to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in all the fields correctly.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
