public class Security extends SignUpModel implements campusAdministrationModel {
    public Security(String username, String idNum, String password, String department) {
        super(username, idNum, password, "Professor");
        setDepartment(department);
    }
}