public class SignUpModel {
    private String idNumber;
    private String password;
    private String department;
    private String role;
	public static boolean adminAvailable = false;

    public boolean isInputValid() {
        return idNumber != null && !idNumber.isEmpty() &&
               password != null && !password.isEmpty() &&
               department != null && !department.equals("Option") &&
               role != null && !role.equals("Option");
    }

    // Getters and Setters
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
