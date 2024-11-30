import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUp extends AbstractSignUp implements ActionListener {
    private static JFrame frame;
    private static JTextField idNumField;
    private static JPasswordField passwordField;
    private static JComboBox<String> roleBox;
    private static JButton finishButton;

    @Override
    public void signUpMain() {  // Removed static modifier here
        frame = new JFrame("Sign-Up");
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

        // Add Role dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"Option", "Professor", "ITS", "Security"});
        panel.add(roleBox, gbc);

        // Add Finish button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        finishButton = new JButton("Finish");
        panel.add(finishButton, gbc);

        // Add action listener to the Finish button
        finishButton.addActionListener(this);  // Changed to `this` instead of `new SignUp()`

        frame.setVisible(true);
    }

    public boolean isInputValid() {
        return !idNumField.getText().equals("") &&
               passwordField.getPassword().length > 0 &&
               idNumField.getText().length() == 8 &&
               !roleBox.getSelectedItem().equals("Option");
    }

    // ActionPerformed method with correct signature
    @Override
    public void actionPerformed(ActionEvent e) {
        char[] password = passwordField.getPassword();

        if (isInputValid()) {
            System.out.println("Inputs are valid. Proceeding to insert into database...");

            try (Connection connection = DatabaseHelper.getConnection()) {
                String sql = "INSERT INTO nonApprovedUsers (id_number, password, role) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, idNumField.getText());
                statement.setString(2, new String(password));
                statement.setString(3, (String) roleBox.getSelectedItem());

                int rowsInserted = statement.executeUpdate();
                System.out.println("Rows inserted: " + rowsInserted);
                
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Thank you! Your account is being processed.", "Message", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    Start.main(new String[]{});
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