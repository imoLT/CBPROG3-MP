import java.util.ArrayList;

//this is the class where the login and signup would interact

public class UserDatabase {
    private ArrayList<SignUpModel> nonApprovedAccounts;
    private ArrayList<SignUpModel> programAdminAcct;
    private ArrayList<SignUpModel> professorAcct;
    private ArrayList<SignUpModel> itsAcct;
    private ArrayList<SignUpModel> securityAcct;
    private ArrayList<SignUpModel> campusAdminAcct;

    public UserDatabase() {
        nonApprovedAccounts = new ArrayList<>();
        programAdminAcct = new ArrayList<>();
        professorAcct = new ArrayList<>();
        itsAcct = new ArrayList<>();
        securityAcct = new ArrayList<>();
        campusAdminAcct = new ArrayList<>();
    }

    public ArrayList<SignUpModel> getNonApprovedAccounts() {
        return nonApprovedAccounts;
    }

    public ArrayList<SignUpModel> getProgramAdminAcct() {
        return programAdminAcct;
    }

    public ArrayList<SignUpModel> getProfessorAcct() {
        return professorAcct;
    }

    public ArrayList<SignUpModel> getItsAcct() {
        return itsAcct;
    }

    public ArrayList<SignUpModel> getSecurityAcct() {
        return securityAcct;
    }

    public ArrayList<SignUpModel> getCampusAdminAcct() {
        return campusAdminAcct;
    }

    public void addUser(SignUpModel user) {
        switch (user.getRole()) {
            case "Program Admin":
                programAdminAcct.add(user);
                break;
            case "Professor":
                professorAcct.add(user);
                break;
            case "ITS":
                itsAcct.add(user);
                break;
            case "Security Office":
                securityAcct.add(user);
                break;
            case "Campus Administration":
                campusAdminAcct.add(user);
                break;
            default:
                nonApprovedAccounts.add(user);
                break;
        }
    }

    // Additional methods to approve accounts, etc.
    public void approveAccount(SignUpModel user) {
        nonApprovedAccounts.remove(user);
        addUser(user);
    }
}
