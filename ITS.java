public class ITSModel extends SignUpModel implements campusAdministrationModel {
    public ITS(String username, String idNum, String password, String department) {
        super(username, idNum, password, "Professor");
        setDepartment(department);
    }
}