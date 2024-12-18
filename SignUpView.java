import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SignUpView {
    private JFrame frame;
    private JTextField idNumField;
    private JPasswordField passwordField; // Changed to JPasswordField
    private JComboBox<String> departmentBox;
    private JComboBox<String> roleBox;
    private JButton finishButton;

    public SignUpView() {
        // Initialize the main frame
        frame = new JFrame("Sign-Up");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        // Create a panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        // Configure layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Add ID Number label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Number:"), gbc);

        gbc.gridx = 1;
        idNumField = new JTextField(15); // Set preferred column size
        panel.add(idNumField, gbc);

        // Add Password label and text field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15); // Use JPasswordField for hidden input
        panel.add(passwordField, gbc);

        // Add Academic Department label and dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Academic Department:"), gbc);

        gbc.gridx = 1;
        departmentBox = new JComboBox<>(new String[]{
            "Option", "CCS", "COS", "BAGCED", "GCOE", "CLA", "RVRCOB", "SOE", "SHS", "Administration"
        });
        panel.add(departmentBox, gbc);

        // Add Program Role label and dropdown
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Program Role:"), gbc);

        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{
            "Option", "Program Admin", "Professor", "Campus Administration", "ITS", "Security Office"
        });
        panel.add(roleBox, gbc);

        // Add Finish button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span the button across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        finishButton = new JButton("Finish");
        panel.add(finishButton, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Getters for form inputs
    public String getIdNumber() {
        return idNumField.getText();
    }

    public String getPassword() {
        // Convert char[] from JPasswordField to String
        return new String(passwordField.getPassword());
    }

    public String getSelectedDepartment() {
        return (String) departmentBox.getSelectedItem();
    }

    public String getSelectedRole() {
        return (String) roleBox.getSelectedItem();
    }

    // Method to add an ActionListener to the Finish button
    public void addFinishButtonListener(ActionListener listener) {
        finishButton.addActionListener(listener);
    }

    // Method to display a message using JOptionPane
    public void showMessage(String message, boolean isFinal) {
        // Use JOptionPane for simplicity
        int messageType = isFinal ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(frame, message, "Message", messageType);

        // Close the frame if it's the final message
        if (isFinal) {
            frame.dispose();
            Start.main(new String[]{}); // Start application again
        }
    }
}
