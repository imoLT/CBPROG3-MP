import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Start implements ActionListener {

    private static JButton loginButton, signUpButton;
    private static JFrame frame;

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();  
        String buttonName = temp.getText(); 

        switch (buttonName) {
            case "Login":
                Login.main(new String[]{});
                frame.dispose();  
                break;
            case "Sign Up":
                SignUpUser signUpUser = new SignUpUser();
                signUpUser.signUpMain();  
                frame.dispose(); 
                break;
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Welcome");
        JPanel panel = new JPanel();

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Start start = new Start(); 

        loginButton = new JButton("Login");
        loginButton.addActionListener(start); 

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(start); 

        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.insets = new Insets(10, 10, 10, 10); 
        panel.add(loginButton, gbc);

        gbc.gridy = 1;  
        gbc.insets = new Insets(10, 10, 20, 10);  
        panel.add(signUpButton, gbc);

        frame.setLocationRelativeTo(null);  
        frame.setVisible(true);  
    }
}
