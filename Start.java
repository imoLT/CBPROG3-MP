import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Start implements ActionListener {

    private static JButton loginButton, signUpButton;
    private static JFrame frame;

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();  // Get the button that was clicked
        String buttonName = temp.getName();  // Get the name of the clicked button
        
        switch (buttonName) {
            case "Login": 
                Login.main(new String[]{});  // Open Login screen
                frame.dispose();  // Close the current Start screen
                break;
            case "Sign Up": 
                SignUpMVC.main(new String[]{});  // Call the SignUpMVC
                frame.dispose();  // Close the frame after clicking "Sign Up"
                break;
        }
    }

    public static void main(String[] args) {
        // Create the main frame
        frame = new JFrame();
        JPanel panel = new JPanel();

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        // Use GridBagLayout to center the components
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Initialize login button and sign up button
        loginButton = new JButton("Login");
        loginButton.setName("Login");
        loginButton.addActionListener(new Start());  // Attach action listener to Login button
        
        signUpButton = new JButton("Sign Up");
        signUpButton.setName("Sign Up");
        signUpButton.addActionListener(new Start());  // Attach action listener to Sign Up button

        // Set constraints to center the buttons
        gbc.gridx = 0;  // Column position
        gbc.gridy = 0;  // Row position
        gbc.insets = new Insets(10, 10, 10, 10);  // Padding around buttons
        panel.add(loginButton, gbc);  // Add Login button to panel

        // Set constraints for the sign-up button
        gbc.gridy = 1;  // Move to the next row for the Sign Up button
        gbc.insets = new Insets(10, 10, 20, 10);  // More padding at the bottom for the second button
        panel.add(signUpButton, gbc);  // Add Sign Up button to panel


        frame.add(panel);		
		frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
