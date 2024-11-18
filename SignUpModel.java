public class SignUpModel {
    private String idNumber;
    private String password;
    private String department;
    private String role;

    public boolean isInputValid() {
        return idNumber != null && !idNumber.isEmpty() &&
               password != null && !password.isEmpty() &&
               department != null && !department.equals("Option") &&
               role != null && !role.equals("Option");
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getIdNumber() {
        return idNumber;
    }

    public String getPassword() {
        return password;
    }

 
    public String getDepartment() {
        return department;
    }


    public String getRole() {
        return role;
    }

 
}
