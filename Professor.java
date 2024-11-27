import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Professor extends UniversityRoomBooking implements ActionListener {
    private JButton bookClassBtn, cancelBookBtn, checkBookBtn, callITBtn, callSecBtn;  // Removed static
    private JFrame frame;
    private int idNum;  // Instance variable

    public Professor(int idNum) {
        this.idNum = idNum;
        professorMenu(); // Initialize the professor menu
    }

    public void professorMenu() {  // No static keyword here
        frame = new JFrame("Professor Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        // Initialize buttons
        bookClassBtn = new JButton("Book Room");
        bookClassBtn.setName("bookClassBtn");
        bookClassBtn.addActionListener(this);
		frame.add(bookClassBtn);

        cancelBookBtn = new JButton("Cancel Booking");
        cancelBookBtn.setName("cancelBookBtn");
        cancelBookBtn.addActionListener(this);
		frame.add(cancelBookBtn);

        checkBookBtn = new JButton("Check Room Status");
        checkBookBtn.setName("checkBookBtn");
        checkBookBtn.addActionListener(this);
		frame.add(checkBookBtn);

        callITBtn = new JButton("Call ITS");
        callITBtn.setName("callITBtn");
        callITBtn.addActionListener(this);
		frame.add(callITBtn);

        callSecBtn = new JButton("Call Security");
        callSecBtn.setName("callSecBtn");
        callSecBtn.addActionListener(this);
		frame.add(callSecBtn);

        frame.setVisible(true);
    }

    // Action performed when a button is clicked
    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        String buttonName = temp.getName();
        switch (buttonName) {
            case "bookClassBtn":
                bookRoom();
                break;
            case "cancelBookBtn":
                cancelBooking();
                break;
            case "checkBookBtn":
                checkRoomStatus();
                break;
            case "callITBtn":
                callITS();
                break;
            case "callSecBtn":
                callSecurity();
                break;
        }
    }

    // Handle room booking logic
    private void bookRoom() {
        // Call UniversityRoomBooking's roomBookMain method with the professor's ID
        UniversityRoomBooking.roomBookMain(idNum);
    }

    // Handle booking cancellation (you can implement this further based on your logic)
    private void cancelBooking() {
        JOptionPane.showMessageDialog(frame, "Booking cancellation feature is not yet implemented.");
    }

    // Check the current room status (you can implement this further based on your logic)
    private void checkRoomStatus() {
        JOptionPane.showMessageDialog(frame, "Room status check feature is not yet implemented.");
    }

    // Call ITS support
    private void callITS() {
        JOptionPane.showMessageDialog(frame, "ITS has been called.");
    }

    // Call Security
    private void callSecurity() {
        JOptionPane.showMessageDialog(frame, "Security has been called.");
    }
}
