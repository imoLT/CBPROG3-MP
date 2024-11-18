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
            }
        } else {
            view.showMessage("Thank you! Please come and check again later to see if your account has been approved.", true);
        }
    }
}
