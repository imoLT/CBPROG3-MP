public class User {
    private String idNumber;
    private String username;
    private String password;
    private String department;
    private String role;

    // Constructor
    public User(String idNumber, String password, String role) {
        this.idNumber = idNumber;
        this.password = password;
        this.role = role;
    }

    // Validation method
    public boolean isInputValid() {
        return idNumber != null && !idNumber.isEmpty() && 
               password != null && idNumber.length() == 8 &&
               role != null && !role.equals("Option");
    }

    // Getters and Setters
    public String getIdNumber() {
        return idNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

}
