import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpController {
    private SignUpModel model;
    private SignUpView view;

    public SignUpController(SignUpModel model, SignUpView view) {
        this.model = model;
        this.view = view;

        this.view.addFinishButtonListener(new ActionListener() {
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
		
		//If there is already a program admin in-charge, inform the user to choose another role instead
		if(SignUpModel.adminAvailable && model.getRole().equals("Program Admin")){
			view.showMessage("Please choose another role!", false);
		}

        // Check if user is an admin and set adminAvailable boolean to true 
        else if (model.getRole().equals("Program Admin") && !SignUpModel.adminAvailable) {
            SignUpModel.adminAvailable = true;
        }
		
		//If the user is a non-program admin trying to sign-up for the program, inform them that there are no available program admin yet
        else if (!SignUpModel.adminAvailable) {
            view.showMessage("No admin is available to approve your request. Please try again later!", false);
		
        } 
		
		else if (!model.isInputValid()) {
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
            }
        } 
		
		if(model.getRole().equals("Program Admin") && SignUpModel.adminAvailable){
			ProgramAdminMVC.main(new String[]{});
		}
		
		else if(model.isInputValid()) {
            view.showMessage("Thank you! Please come and check again later to see if your account has been approved.", true);
        }
    }
}
