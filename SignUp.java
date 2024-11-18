import javax.swing.*;
import java.awt.event.*;
import java.awt.Component; 
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class SignUp implements ActionListener {
    
    private static JLabel idNumLabel, passLabel, deptLabel, roleLabel, result;
    private static JTextField idNum, password;
    private static JButton finishAcct;

    // Method to create a new panel with a result label
    public void createPanel(JLabel resultLabel, boolean created){
        // Create the frame and panel
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        frame.setSize(400, 400);
		if (!created)
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		else
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        // Set the text of the result label
        resultLabel.setBounds(10, 10, 400, 300);
        panel.add(resultLabel);

        // Make the frame visible
        frame.setVisible(true);
    }

    // ActionListener for handling events
    public void actionPerformed(ActionEvent e){
        if (idNum.getText().equals("") || password.getText().equals("")) {
			
			if(idNum.getText().equals("") && password.getText().equals("")){
                result = new JLabel("ID number and password field is empty. Please try again!");
                createPanel(result, false);
            }
			
            else if(idNum.getText().equals("")){
                result = new JLabel("ID number field is empty. Please try again!");
                createPanel(result, false);
            }
			
            else {
                JLabel result = new JLabel("Password field is empty. Please enter a password and try again!");
                createPanel(result, false);
            }
        }
        else {
            try {
                if (e.getSource() == finishAcct) {
                    result = new JLabel("Thank you! Please come and check again later to see if your account has been approved.");
                    createPanel(result, true);
                }
            } catch (Exception f) {
                System.out.println("Invalid.");
            }
        }
    }

    public static void main(String[] args) {

        // Create main frame
        JFrame frame = new JFrame("Sign-Up");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Change to DISPOSE_ON_CLOSE
        frame.setSize(500, 500);
        frame.setLocation(430, 100);

        // Create panel and set layout to GridBagLayout
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());  // Use GridBagLayout for precise control

        frame.add(panel);
        
        // Create a GridBagConstraints instance for controlling layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;  // Start from the first column
        gbc.gridy = 0;  // Start from the first row
        gbc.anchor = GridBagConstraints.WEST;  // Align components to the left
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);  // Add some space between components

        // ID Number label and textfield
        idNumLabel = new JLabel("ID Number: ");
        panel.add(idNumLabel, gbc);  // Add label to the grid

        gbc.gridx = 1;  // Move to the next column for the textfield
        idNum = new JTextField(10);
        panel.add(idNum, gbc);  // Add textfield next to the label

        // Move to the next row
        gbc.gridy = 1;
        gbc.gridx = 0;
        
        // Password label and textfield
        passLabel = new JLabel("Password: ");
        panel.add(passLabel, gbc);

        gbc.gridx = 1;  // Move to the next column for the password field
        password = new JTextField(10);
        panel.add(password, gbc);

        // Move to the next row
        gbc.gridy = 2;
        gbc.gridx = 0;

        // Academic department label and dropdown
        deptLabel = new JLabel("Academic Department: ");
        panel.add(deptLabel, gbc);

        gbc.gridx = 1;  // Move to the next column for the combo box
        String[] deptChoices = { "CCS", "COS", "BAGCED", "GCOE", "CLA", "RVRCOB", "SOE", "SHS" };
        final JComboBox<String> dc = new JComboBox<String>(deptChoices);
        panel.add(dc, gbc);

        // Move to the next row
        gbc.gridy = 3;
        gbc.gridx = 0;

        // Program role label and dropdown
        roleLabel = new JLabel("Program Role: ");
        panel.add(roleLabel, gbc);

        gbc.gridx = 1;  // Move to the next column for the combo box
        String[] roleChoices = { "Professor", "Campus Administration", "ITS", "Security Office" };
        final JComboBox<String> rc = new JComboBox<String>(roleChoices);
        panel.add(rc, gbc);

        // Move to the next row for the button
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;  // Make the button span across both columns

        // Button to finalize
        finishAcct = new JButton("Finish");
        finishAcct.addActionListener(new SignUp());
        finishAcct.setName("finishAcct");
        finishAcct.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(finishAcct, gbc);  // Add button to the panel

        frame.setVisible(true);  // Make the frame visible
    }
}
