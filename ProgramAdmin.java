import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProgramAdmin implements ActionListener {
    private static JFrame frame;
    private static JButton acctButton, bookButton, roomButton, logoutButton;

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        String buttonName = temp.getName();
        switch (buttonName) {
            case "acctButton":
                new DisplayNonApprovedAccounts();
                break;
            case "bookButton":
                JOptionPane.showMessageDialog(null, "Approve rooms here!");
                break;
            case "roomButton":
                JOptionPane.showMessageDialog(null, "Room menu here!");
                break;
            case "logoutButton":
				frame.dispose();
                Start.main(new String[]{});
                break;
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Program Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);  // Increase the frame size to accommodate the buttons
        frame.setLocationRelativeTo(null);  // Center the frame

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components
        gbc.anchor = GridBagConstraints.CENTER; // Center components in the grid

        // acctButton
        gbc.gridx = 0;
        gbc.gridy = 0;  // First row
        acctButton = new JButton("View Requests for Account Approval");
        acctButton.addActionListener(new ProgramAdmin());  // Add action listener
        acctButton.setName("acctButton");
        panel.add(acctButton, gbc);

        // bookButton
        gbc.gridx = 0;
        gbc.gridy = 1;  // Second row
        bookButton = new JButton("View Requests for Room Reservation");
        bookButton.addActionListener(new ProgramAdmin());  // Add action listener
        bookButton.setName("bookButton");
        panel.add(bookButton, gbc);

        // roomButton
        gbc.gridx = 0;
        gbc.gridy = 2;  // Third row
        roomButton = new JButton("View Room Availability");
        roomButton.addActionListener(new ProgramAdmin());  // Add action listener
        roomButton.setName("roomButton");
        panel.add(roomButton, gbc);

        // logoutButton
        gbc.gridx = 0;
        gbc.gridy = 3;  // Fourth row (fixed overlap issue by assigning a unique row)
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ProgramAdmin());  // Add action listener
        logoutButton.setName("logoutButton");
        panel.add(logoutButton, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }
}
