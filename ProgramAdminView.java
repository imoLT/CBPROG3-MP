import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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
}
