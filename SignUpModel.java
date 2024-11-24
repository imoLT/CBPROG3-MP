public class SignUpModel {
    private String idNumber;
    private String username;
    private String password;
    private String department;
    private String role;

    // Constructor
    public SignUpModel(String username, String idNumber, String password, String role) {
        this.username = username;
        this.idNumber = idNumber;
        this.password = password;
        this.role = role;
    }

    // Validation method
    public boolean isInputValid() {
        return idNumber != null && !idNumber.isEmpty() && 
               password != null && idNumber.length() == 8 &&
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
