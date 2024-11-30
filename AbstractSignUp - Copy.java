import javax.swing.*;
import java.sql.*;

abstract class AbstractSignUp {
    public abstract void signUpMain();        
    public abstract boolean isInputValid(); 

    // Check if ID exists in users or nonApprovedUsers (ID as an int)
    protected boolean idExist(int idNumber) { 
        try (Connection connection = DatabaseHelper.getConnection()) {
            // Check if the ID exists in the 'users' table
            String query1 = "SELECT COUNT(*) FROM users WHERE idNumber = ?";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, idNumber);  // Use setInt() since the parameter is an int
            ResultSet resultSet1 = statement1.executeQuery();
            
            // Check if the ID exists in the 'nonApprovedUsers' table
            String query2 = "SELECT COUNT(*) FROM nonApprovedUsers WHERE id_number = ?";
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setInt(1, idNumber);  // Use setInt() for the int parameter
            ResultSet resultSet2 = statement2.executeQuery();
            
            // Retrieve results for both queries
            int countUsers = 0;
            int countNonApprovedUsers = 0;

            if (resultSet1.next()) {
                countUsers = resultSet1.getInt(1);  // Result from 'users' table
            }
            
            if (resultSet2.next()) {
                countNonApprovedUsers = resultSet2.getInt(1);  // Result from 'nonApprovedUsers' table
            }
            
            if (countUsers != 0) {  // ID exists in 'users' table
                JOptionPane.showMessageDialog(null, "This ID number is already registered! Please try logging in.", "Error", JOptionPane.WARNING_MESSAGE);
                return true;
            } else if (countNonApprovedUsers != 0) {  // ID exists in 'nonApprovedUsers' table
                JOptionPane.showMessageDialog(null, "This ID number is already up for processing.", "Error", JOptionPane.WARNING_MESSAGE);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking ID existence: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;  // Return false if ID does not exist in either table
    }
}
