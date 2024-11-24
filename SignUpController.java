import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SignUpController {
    private SignUpModel model;
    private SignUpView view;
    private UserDatabase userDatabase;

    public SignUpController() {
        model = new SignUpModel("Admin", "12345678", "password", "Program Admin");
        view = new SignUpView();
        userDatabase = new UserDatabase();

        // Set the listener for the finish button
        view.addFinishButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
    }

    private void handleSignUp() {
        model.setIdNumber(view.getIdNumber());
        model.setPassword(view.getPassword());
        model.setDepartment(view.getSelectedDepartment());
        model.setRole(view.getSelectedRole());

        // Input validation
        if (!model.isInputValid()) {
            if (model.getIdNumber().isEmpty() && model.getPassword().isEmpty()) {
                view.showMessage("ID number and password field is empty. Please try again!", false);
            } else if (model.getIdNumber().isEmpty()) {
                view.showMessage("ID number field is empty. Please try again!", false);
            } else if (model.getPassword().isEmpty()) {
                view.showMessage("Password field is empty. Please enter a password and try again!", false);
            } else if (model.getDepartment().equals("Option")) {
                view.showMessage("Please choose your department.", false);
            } else if (model.getRole().equals("Option")) {
                view.showMessage("Please choose your role.", false);
            } else if (model.getIdNumber().length() != 8) {
                view.showMessage("Please check your ID number.", false);
            }
        } else {
            // If the user is a Program Admin, launch the Program Admin interface
            if (model.getRole().equals("Program Admin")) {
                ProgramAdminMVC.setAccountToApprove(userDatabase.getNonApprovedAccounts());
                ProgramAdminMVC.main(new String[]{});
            } else {
                // Add non-admin accounts for approval
                userDatabase.addUser(model);
                view.showMessage("Thank you! Please come back later to see if your account has been approved.", true);
            }
        }
    }
}
