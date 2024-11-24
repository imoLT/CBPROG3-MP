import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ProgramAdminController {
    private ProgramAdminView view;
    private ProgramAdminModel model;
    private ArrayList<SignUpModel> nonApprovedAccounts;

    private ArrayList<SignUpModel> approvedProgramAdminAccounts;
    private ArrayList<SignUpModel> approvedProfessorAccounts;
    private ArrayList<SignUpModel> approvedItsAccounts;
    private ArrayList<SignUpModel> approvedSecurityAccounts;
    private ArrayList<SignUpModel> approvedCampusAdminAccounts;

    public ProgramAdminController(ProgramAdminView view, ProgramAdminModel model, 
                                  ArrayList<SignUpModel> nonApprovedAccounts, 
                                  ArrayList<SignUpModel> approvedProgramAdminAccounts, 
                                  ArrayList<SignUpModel> approvedProfessorAccounts, 
                                  ArrayList<SignUpModel> approvedItsAccounts, 
                                  ArrayList<SignUpModel> approvedSecurityAccounts, 
                                  ArrayList<SignUpModel> approvedCampusAdminAccounts) {
        this.view = view;
        this.model = model;
        this.nonApprovedAccounts = nonApprovedAccounts;

        this.approvedProgramAdminAccounts = approvedProgramAdminAccounts;
        this.approvedProfessorAccounts = approvedProfessorAccounts;
        this.approvedItsAccounts = approvedItsAccounts;
        this.approvedSecurityAccounts = approvedSecurityAccounts;
        this.approvedCampusAdminAccounts = approvedCampusAdminAccounts;

        // Register listeners for buttons in the view
        this.view.addAcctButtonListener(new AcctButtonListener());
        this.view.addBookButtonListener(new BookButtonListener());
        this.view.addRoomButtonListener(new RoomButtonListener());
    }

    private class AcctButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
			try{
				view.displayAccounts(nonApprovedAccounts);
					
				// Approve accounts and update the approved lists
				model.approveAccount(nonApprovedAccounts, approvedProgramAdminAccounts, approvedProfessorAccounts, 
										  approvedItsAccounts, approvedSecurityAccounts, approvedCampusAdminAccounts);
					
				// Refresh the view with updated approved accounts
				view.updateApprovedAccounts(approvedProgramAdminAccounts, approvedProfessorAccounts, 
				approvedItsAccounts, approvedSecurityAccounts, approvedCampusAdminAccounts);
			}
			catch(NullPointerException f){
				view.showMessage("There are no accounts to be approved right now.");
			}
        }
    }
    

    // Listener for the "Book" button
    private class BookButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Display the number of rooms pending approval (can be extended to show room details)
            System.out.println("The following rooms are pending for approval: " + model.getApproveRoom());
        }
    }

    // Listener for the "Room" button (it seems related to booking functionality)
    private class RoomButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // If the UniversityRoomBooking feature needs to be integrated, uncomment the following:
            // UniversityRoomBooking.main(new String[]{});  // Launch the room booking feature

            // If not needed, leave the method empty or remove the listener.
        }
    }
}
