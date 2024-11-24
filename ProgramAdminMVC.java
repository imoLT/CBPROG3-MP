import java.util.ArrayList;

public class ProgramAdminMVC {
    // Array to hold accounts pending approval
    private static ArrayList<SignUpModel> nonApprovedAccounts;

    // Arrays for approved accounts by type
	//Change <SugnUpModel> to the programAdminModel, professorAcctModel, etc. later
    private static ArrayList<SignUpModel> programAdminAcct;
    private ArrayList<SignUpModel> professorAcct;
    private ArrayList<SignUpModel> itsAcct;
    private ArrayList<SignUpModel> securityAcct;
    private ArrayList<SignUpModel> campusAdminAcct;

    // Constructor to initialize the lists
    public ProgramAdminMVC() {
        programAdminAcct = new ArrayList<>();
        professorAcct = new ArrayList<>();
        itsAcct = new ArrayList<>();
        securityAcct = new ArrayList<>();
        campusAdminAcct = new ArrayList<>();
    }

    // Static method to set the non-approved accounts list
    public static void setAccountToApprove(ArrayList<SignUpModel> toApprove) {
        nonApprovedAccounts = toApprove;
    }
	
	public static ArrayList<SignUpModel> getNonApprovedAccounts() {
        return nonApprovedAccounts;
    }
	
	public static ArrayList<SignUpModel> getAdminAccts() {
        return programAdminAcct;
    }

    // Instance method to approve accounts
    public void approvePendingAccounts() {
        ProgramAdminModel model = new ProgramAdminModel("admin", "12345678", "password", "Program Admin");
        model.approveAccount(nonApprovedAccounts, programAdminAcct, professorAcct, itsAcct, securityAcct, campusAdminAcct);
    }

    public static void main(String[] args) {
        // Initialize the model, view, and controller
        ProgramAdminModel model = new ProgramAdminModel("admin", "12345678", "password", "Program Admin");
        ProgramAdminView view = new ProgramAdminView();

        // Instantiate the ProgramAdminMVC to manage the accounts
        ProgramAdminMVC programAdminMVC = new ProgramAdminMVC();

        // Set up the controller and pass in the lists
        new ProgramAdminController(view, model, nonApprovedAccounts, programAdminMVC.programAdminAcct, programAdminMVC.professorAcct, 
                                    programAdminMVC.itsAcct, programAdminMVC.securityAcct, programAdminMVC.campusAdminAcct);

        // Approve pending accounts
        programAdminMVC.approvePendingAccounts();
    }
}
