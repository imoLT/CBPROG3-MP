public class SignUpMVC {
    public static void main(String[] args) {
        SignUpModel model = new SignUpModel();
        SignUpView view = new SignUpView();
        new SignUpController(model, view);
    }
}
