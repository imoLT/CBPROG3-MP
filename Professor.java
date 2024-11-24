import javax.swing.*;
import java.awt.event.*;

public class Professor extends SignUpModel {
    public Professor(String username, String idNum, String password, String department) {
        super(username, idNum, password, "Professor");
        setDepartment(department);
    }
}