public class ProgramAdminMVC {
    public static void main(String[] args) {
        ProgramAdminModel model = new ProgramAdminModel("admin", "password", 0);
        ProgramAdminView view = new ProgramAdminView();
        new ProgramAdminController(view, model); // Pass the view and model to the controller
    }
}
