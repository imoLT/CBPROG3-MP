import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgramAdminController {
    private ProgramAdminView view;
    private ProgramAdminModel model;

    public ProgramAdminController(ProgramAdminView view, ProgramAdminModel model) {
        this.view = view;
        this.model = model;

        this.view.addAcctButtonListener(new AcctButtonListener());
        this.view.addBookButtonListener(new BookButtonListener());
        this.view.addRoomButtonListener(new RoomButtonListener()); // Room button listener
    }

    private class AcctButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("The following users are pending for approval: " + model.getApproveAcct());
        }
    }

    private class BookButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("The following rooms are pending for approval: " + model.getApproveRoom());
        }
    }

    private class RoomButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Open the UniversityRoomBooking when the Room button is clicked
            UniversityRoomBooking.main(new String[]{});  // Pass an empty string array to main method
        }
    }
}
