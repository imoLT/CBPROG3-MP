import java.util.ArrayList;

public class ProgramAdminModel extends SignUpModel {
    private int nApproveAcct = 0;
    private int nApproveRoom = 0;

    // Constructor
    public ProgramAdminModel(String username, String idNum, String password, String role) {
        super(username, idNum, password, role);
    }

    // Increment the number of approved accounts
    public void addApproveAcct() {
        nApproveAcct++;
    }

    // Increment the number of approved rooms
    public void addApproveRoom() {
        nApproveRoom++;
    }

    // Getters
    public int getNApproveAcct() {
        return nApproveAcct;
    }

    public int getApproveRoom() {
        return nApproveRoom;
    }

    /**
     * Approves accounts from the pending accounts list and moves them to the corresponding approved list.
     * If approved, creates an instance of the appropriate role (Professor, ITS, or Security).
     * 
     * @param pendingAccounts List of accounts waiting for approval
     * @param programAdminAcct Approved accounts for Program Admin
     * @param professorAcct Approved accounts for Professor
     * @param itsAcct Approved accounts for ITS
     * @param securityAcct Approved accounts for Security
     * @param campusAdminAcct Approved accounts for Campus Admin
     */
    public void approveAccount(ArrayList<SignUpModel> pendingAccounts, ArrayList<SignUpModel> programAdminAcct,
                               ArrayList<SignUpModel> professorAcct, ArrayList<SignUpModel> itsAcct, 
                               ArrayList<SignUpModel> securityAcct, ArrayList<SignUpModel> campusAdminAcct) {

		if (pendingAccounts == null) {
            throw new NullPointerException("There are no accounts pending for approval right now");
        }
		
        else {
			for (int i = 0; i < pendingAccounts.size(); i++) {
				SignUpModel account = pendingAccounts.get(i);

				switch (account.getRole()) {
					case "Program Admin":
						programAdminAcct.add(account);
						addApproveAcct();
						break;

					case "Professor":
						professorAcct.add(account);
						addApproveAcct();
						break;

					case "ITS":
						itsAcct.add(account);
						addApproveAcct();
						break;

					case "Security":
						securityAcct.add(account);
						addApproveAcct();
						break;

					case "Campus Admin":
						campusAdminAcct.add(account);
						addApproveAcct();
						break;
				}
				
				pendingAccounts.remove(i);
				System.out.println("Account with ID " + account.getIdNumber() + " approved as " + account.getRole());
				break;
			}
		}
    }
}
