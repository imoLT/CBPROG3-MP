import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Start implements ActionListener {

    private static JButton loginButton, signUpButton;
    private static JFrame frame;

    // Constructor is no longer required since we're using the same Start instance
    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();  // Get the button that was clicked
        String buttonName = temp.getText();  // Use button text instead of setName (for readability)

        switch (buttonName) {
            case "Login":
                Login.main(new String[]{});  // Open Login screen
                frame.dispose();  // Close the current Start screen
                break;
            case "Sign Up":
                SignUp.main(new String[]{});  // Open SignUp screen
                frame.dispose();  // Close the frame after clicking "Sign Up"
                break;
        }
    }

    public static void main(String[] args) {
        // Create the main frame
        frame = new JFrame("Welcome");
        JPanel panel = new JPanel();

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Initialize the action listener (reused for both buttons)
        Start startInstance = new Start(); 

        // Initialize login button and sign-up button
        loginButton = new JButton("Login");
        loginButton.addActionListener(startInstance);  // Attach the same action listener to both buttons

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(startInstance);  // Same action listener for the Sign Up button

        // Set constraints for Login button (centered on the panel)
        gbc.gridx = 0;  // Column position
        gbc.gridy = 0;  // Row position
        gbc.insets = new Insets(10, 10, 10, 10);  // Padding around buttons
        panel.add(loginButton, gbc);

        // Set constraints for the Sign Up button
        gbc.gridy = 1;  // Move to the next row for the Sign Up button
        gbc.insets = new Insets(10, 10, 20, 10);  // Padding for the second button
        panel.add(signUpButton, gbc);

        frame.setLocationRelativeTo(null);  // Center the frame on screen
        frame.setVisible(true);  // Make the frame visible
    }
}
