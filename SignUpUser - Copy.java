import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUpUser extends AbstractSignUp implements ActionListener {
    private static JFrame frame;
    private static JTextField idNumField, lastNameField, firstNameField;
    private static JPasswordField passwordField;
    private static JComboBox<String> roleBox;
    private static JButton finishButton, cancelButton;

    public void signUpMain() {
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

        // Add First Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        panel.add(firstNameField, gbc);

        // Add Last Name field
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        panel.add(lastNameField, gbc);

        // Add Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Add Role dropdown
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"Option", "Professor", "ITS", "Security Office"});
        panel.add(roleBox, gbc);

        // Add Finish button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;  // Make sure Finish button does not span multiple columns
        gbc.anchor = GridBagConstraints.CENTER;
        finishButton = new JButton("Finish");
        panel.add(finishButton, gbc);
		
		// Action listeners for buttons
        finishButton.addActionListener(this);

        // Add Cancel button
        gbc.gridx = 1; 
        cancelButton = new JButton("Cancel");
        panel.add(cancelButton, gbc);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();  
                Start.main(new String[]{});  // Navigate to main screen
            }
        });

        frame.setVisible(true);
    }

    public boolean isInputValid() {
        return !idNumField.getText().equals("") &&
               !firstNameField.getText().equals("") &&
               !lastNameField.getText().equals("") &&
               passwordField.getPassword().length > 0 &&
               idNumField.getText().length() == 8 &&
               !roleBox.getSelectedItem().equals("Option");
    }

    public void actionPerformed(ActionEvent e) {
        String firstName = firstNameField.getText(); 
        String lastName = lastNameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (isInputValid()) {
            try {
                // Convert ID number from String to int
                int idNumber = Integer.parseInt(idNumField.getText()); // Parse the ID number as int

                // Check if id exists
				boolean check = idExist(idNumber);
				
				if (!check){
					try (Connection connection = DatabaseHelper.getConnection()) {

						// SQL query to insert data into the nonApprovedUsers table
						String sql = "INSERT INTO nonApprovedUsers (id_number, firstName, lastName, password, role) VALUES (?, ?, ?, ?, ?)";
						PreparedStatement statement = connection.prepareStatement(sql);

						// Set parameters for the SQL query
						statement.setInt(1, idNumber);  // ID number as an integer
						statement.setString(2, firstName); // First name as a string
						statement.setString(3, lastName);  // Last name as a string
						statement.setString(4, password);  // Password as a string
						statement.setString(5, role);  // Role as a string

						// Execute the query
						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							JOptionPane.showMessageDialog(null, "Thank you! Your account is being processed.", "Message", JOptionPane.INFORMATION_MESSAGE);
							frame.dispose();
							Start.main(new String[]{}); // Continue to the next screen
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error connecting to the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter numbers only!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } 
		
		else {
			if (idNumField.getText().equals("") || idNumField.getText().length() != 8)
				JOptionPane.showMessageDialog(null, "Please your 8-digit id number!", "Error", JOptionPane.WARNING_MESSAGE);
			if (firstNameField.getText().equals(""))
				JOptionPane.showMessageDialog(null, "Please enter a first name!", "Error", JOptionPane.WARNING_MESSAGE);
			if (lastNameField.getText().equals(""))
				JOptionPane.showMessageDialog(null, "Please enter a last name!", "Error", JOptionPane.WARNING_MESSAGE);
            if (passwordField.getPassword().length > 0)
				JOptionPane.showMessageDialog(null, "Please enter a password!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
