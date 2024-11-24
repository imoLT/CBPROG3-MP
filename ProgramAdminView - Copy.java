import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ProgramAdminView {
    private JFrame frame;
    private JButton acctButton;
    private JButton bookButton;
    private JButton roomButton; // Button to view room availability

    public ProgramAdminView() {
        frame = new JFrame("Program Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // acctButton
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        acctButton = new JButton("View Requests for Account Approval");
        panel.add(acctButton, gbc);

        // bookButton
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        bookButton = new JButton("View Requests for Room Reservation");
        panel.add(bookButton, gbc);

        // roomButton - Add action listener here
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        roomButton = new JButton("View Room Availability");
        panel.add(roomButton, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Methods to add listeners
    public void addAcctButtonListener(ActionListener listener) {
        acctButton.addActionListener(listener);
    }

    public void addBookButtonListener(ActionListener listener) {
        bookButton.addActionListener(listener);
    }

    public void addRoomButtonListener(ActionListener listener) {
        roomButton.addActionListener(listener); // Listen for the Room button
    }

    // Method to display pending account details
    public void displayAccounts(ArrayList<SignUpModel> accounts) {
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No pending accounts for approval.", "Accounts", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Build the account details string
            StringBuilder accountDetails = new StringBuilder("Pending Accounts for Approval:\n\n");
            for (SignUpModel account : accounts) {
                accountDetails.append("ID: ").append(account.getIdNumber())
                        .append("\nPassword: ").append(account.getPassword())
                        .append("\nDepartment: ").append(account.getDepartment())
                        .append("\nRole: ").append(account.getRole())
                        .append("\n----------------------------------\n");
            }

            // Display the details in a scrollable dialog
            JTextArea textArea = new JTextArea(accountDetails.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(frame, scrollPane, "Pending Accounts", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Method to display approved accounts
    public void updateApprovedAccounts(ArrayList<SignUpModel> approvedProgramAdmin, ArrayList<SignUpModel> approvedProfessor, ArrayList<SignUpModel> approvedIts, ArrayList<SignUpModel> approvedSecurity, ArrayList<SignUpModel> approvedCampusAdmin) {
        // Build the approved account details string
        StringBuilder approvedDetails = new StringBuilder("Approved Accounts:\n\n");
		
		for (Object account : approvedProgramAdmin) {
            approvedDetails.append(account.toString()); // Assuming each approved object has a meaningful toString().append("\n----------------------------------\n");
		}
		
		for (Object account : approvedProfessor) {
            approvedDetails.append(account.toString()); // Assuming each approved object has a meaningful toString().append("\n----------------------------------\n");
		}
		
		for (Object account : approvedIts) {
            approvedDetails.append(account.toString()); // Assuming each approved object has a meaningful toString().append("\n----------------------------------\n");
		}
		
		for (Object account : approvedSecurity) {
            approvedDetails.append(account.toString()); // Assuming each approved object has a meaningful toString().append("\n----------------------------------\n");
		}
		
		for (Object account : approvedCampusAdmin) {
            approvedDetails.append(account.toString()); // Assuming each approved object has a meaningful toString().append("\n----------------------------------\n");
		}

		// Display the details in a scrollable dialog
        JTextArea textArea = new JTextArea(approvedDetails.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(frame, scrollPane, "Approved Accounts", JOptionPane.INFORMATION_MESSAGE);
       }
    
	
	public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}
