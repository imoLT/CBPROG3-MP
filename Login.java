import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login implements ActionListener {
    private static JLabel idNumLabel, passLabel;
    private static JTextField idNum;
    private static JPasswordField password;
    private static JButton login, cancelButton;
    private static JFrame frame;

    public void actionPerformed(ActionEvent e) {
		String enteredPassword = new String(password.getPassword());

		if (idNum.getText().equals("") || enteredPassword.equals("")) {
			if (idNum.getText().equals("") && enteredPassword.equals("")) {
				JOptionPane.showMessageDialog(null, "Please enter your ID number and password.");
			} else if (idNum.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "ID number field is empty. Please try again!");
			} else {
				JOptionPane.showMessageDialog(null, "Password field is empty. Please enter a password and try again!");
			}
		} else {
			
			DatabaseConnectionWrapper connWrapper = new DatabaseConnectionWrapper("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");

			try (Connection conn = connWrapper.getConnection()) {
				String sql = "SELECT * FROM users WHERE idnumber = ? AND password = ?";
				try (PreparedStatement pst = conn.prepareStatement(sql)) {
					pst.setString(1, idNum.getText());
					pst.setString(2, enteredPassword);

					ResultSet rs = pst.executeQuery();
					if (rs.next()) {
						String role = rs.getString("role");

						if (role.equals("Program Admin")) {
							frame.dispose();
							ProgramAdmin admin = new ProgramAdmin(Integer.parseInt(idNum.getText()));
							admin.adminMenu();
						} else if (role.equals("Professor")) {
							frame.dispose();
							Professor professor = new Professor(Integer.parseInt(idNum.getText()));
						} else if (role.equals("ITS")) {
							frame.dispose();
							ITS its = new ITS(connWrapper, Integer.parseInt(idNum.getText()));
							its.showMainPanel();
						} else if (role.equals("Security Office")) {
							frame.dispose();
							Security security = new Security(connWrapper, Integer.parseInt(idNum.getText()));
							security.showMainPanel(); 
						}
					} else {
						String nonApprovedSql = "SELECT * FROM nonApprovedUsers WHERE id_number = ? AND password = ?";
						try (PreparedStatement pstNonApproved = conn.prepareStatement(nonApprovedSql)) {
							pstNonApproved.setString(1, idNum.getText());
							pstNonApproved.setString(2, enteredPassword);

							ResultSet rsNonApproved = pstNonApproved.executeQuery();
							if (rsNonApproved.next()) {
								JOptionPane.showMessageDialog(null, "Your account is still pending for approval! Please try again later.");
							} else {
								JOptionPane.showMessageDialog(null, "Invalid ID or password. Please try again.");
							}
						}
					}
				}
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null, "Error connecting to database: " + ex.getMessage());
			}
		}
	}


    public static void main(String[] args) {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocation(430, 100);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;  
        gbc.anchor = GridBagConstraints.WEST; 
        gbc.insets = new Insets(5, 5, 5, 5);  

		// id label and textfield
        idNumLabel = new JLabel("ID Number: ");
        panel.add(idNumLabel, gbc);
        gbc.gridx = 1;  
        idNum = new JTextField(10);
        panel.add(idNum, gbc);


        gbc.gridy = 1;
        gbc.gridx = 0;

        // Password label and textfield
        passLabel = new JLabel("Password: ");
        panel.add(passLabel, gbc);

        gbc.gridx = 1; 
        password = new JPasswordField(10);
        panel.add(password, gbc);

        // Login button
        gbc.gridy = 2;

        login = new JButton("Login");
        login.addActionListener(new Login());
        panel.add(login, gbc);
		
		gbc.gridy = 4;
		
		// Cancel button
        cancelButton = new JButton("Cancel");
        panel.add(cancelButton, gbc);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();  
                Start.main(new String[]{}); 
            }
        });		

        // Make the frame visible
        frame.setVisible(true);
    }
}
