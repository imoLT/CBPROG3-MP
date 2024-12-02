import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUpAdmin extends AbstractSignUp implements ActionListener {
    private static JFrame frame;
    private static JTextField idNumField, firstNameField, lastNameField;
    private static JPasswordField passwordField;
    private static JButton finishButton, cancelButton; 
	private static JLabel idNumb, fName, lName, pass;

    public void signUpMain() { 
        frame = new JFrame("Sign-Up a New Admin");
        JPanel panel = new JPanel(new GridBagLayout());
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

		// Add ID Number field
        gbc.gridx = 0;
        gbc.gridy = 0;
		idNumb = new JLabel("ID Number:");
        panel.add(idNumb, gbc);
        gbc.gridx = 1;
        idNumField = new JTextField(15);
        panel.add(idNumField, gbc);
		
        // Add First Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
		fName =new JLabel("First Name:");
        panel.add(fName, gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        panel.add(firstNameField, gbc);

        // Add Last Name field
        gbc.gridx = 0;
        gbc.gridy = 2;
		lName = new JLabel("Last Name:");
        panel.add(lName, gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        panel.add(lastNameField, gbc);

        // Add Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
		pass = new JLabel("Password:");
        panel.add(pass, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Add Finish button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        finishButton = new JButton("Finish");
        panel.add(finishButton, gbc);

        // Add Cancel button
        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        panel.add(cancelButton, gbc);

        finishButton.addActionListener(this);  

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();  
            }
        });

        frame.setVisible(true);
    }

    public boolean isInputValid() {
        return !idNumField.getText().equals("") &&
               !firstNameField.getText().equals("") &&
               !lastNameField.getText().equals("") &&
               passwordField.getPassword().length > 0 &&
               idNumField.getText().length() == 8;
    }

    public void actionPerformed(ActionEvent e){
        char[] password = passwordField.getPassword();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (isInputValid() && !idExist(Integer.parseInt(idNumField.getText()))) { //input is valid and the id does not exist anywhere
            if (!idExist(Integer.parseInt(idNumField.getText()))){
                try (Connection connection = DatabaseHelper.getConnection()) {
                    // SQL query to insert the user with "Program Admin" role
                    String sql = "INSERT INTO users (idNumber, firstName, lastName, password, role) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, idNumField.getText());
                    statement.setString(2, firstName);
                    statement.setString(3, lastName);
                    statement.setString(4, new String(password));

                    // Set the role as "Program Admin"
                    statement.setString(5, "Program Admin");

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "New admin account created successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error connecting to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } 
        }
        
        else {
            if (idNumField.getText().equals("") || idNumField.getText().length() != 8)
                JOptionPane.showMessageDialog(null, "Please make sure to enter exactly 8 digits for your ID.", "Error", JOptionPane.WARNING_MESSAGE);
            if (firstNameField.getText().equals(""))
                JOptionPane.showMessageDialog(null, "Please enter your first name.", "Error", JOptionPane.WARNING_MESSAGE);
            if (lastNameField.getText().equals(""))
                JOptionPane.showMessageDialog(null, "Please enter your last name.", "Error", JOptionPane.WARNING_MESSAGE);
            if (passwordField.getPassword().length == 0)
                JOptionPane.showMessageDialog(null, "Please enter a password.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
