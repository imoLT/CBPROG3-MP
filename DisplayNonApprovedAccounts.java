import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayNonApprovedAccounts {

    private static JLabel userLabel;
    private static JTextField enterUser;
    private static JButton approveBtn;

    public DisplayNonApprovedAccounts() {
        JFrame frame = new JFrame("Non-Approved Users");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Number");
        model.addColumn("Role"); // Removed password column

        // Create JTable
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Query to select all data from the nonApprovedUsers table
        String sql = "SELECT * FROM nonApprovedUsers";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false", "root", "Vianca");
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Fetch and add data to table (exclude password)
            while (rs.next()) {
                String idNumber = rs.getString("id_number");
                String role = rs.getString("role");
                model.addRow(new Object[]{idNumber, role}); // Only add ID and Role
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add a text field and button to approve users by entering ID
        JPanel panel = new JPanel();
        userLabel = new JLabel("Enter ID Number to Approve:");
        enterUser = new JTextField(15);
        approveBtn = new JButton("Approve User");

        approveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (enterUser.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter an ID number!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int idNum = Integer.parseInt(enterUser.getText());
                    approveUser(idNum);  // Call method to approve user
                }
            }
        });

        panel.add(userLabel);
        panel.add(enterUser);
        panel.add(approveBtn);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Approve the user and move them from nonApprovedUsers to users
    public static void approveUser(int idNumber) {
		String dbUrl = "jdbc:mysql://127.0.0.1:3306/ProgMP?useSSL=false";
		String dbUser = "root";
		String dbPassword = "Vianca";
		
		// SQL queries
		String selectSql = "SELECT * FROM nonApprovedUsers WHERE id_number = ?";
		String deleteSql = "DELETE FROM nonApprovedUsers WHERE id_number = ?";
		String insertSql = "INSERT INTO users (idNumber, password, role) SELECT id_number, password, role FROM nonApprovedUsers WHERE id_number = ?";

		try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
			// Check if the user exists in the nonApprovedUsers table
			try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
				selectStmt.setInt(1, idNumber);
				ResultSet rs = selectStmt.executeQuery();

				if (rs.next()) {
					// If found, move them to users table
					try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
						insertStmt.setInt(1, idNumber);
						insertStmt.executeUpdate();
					}

					// Remove the user from nonApprovedUsers
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
						deleteStmt.setInt(1, idNumber);
						deleteStmt.executeUpdate();
					}

					JOptionPane.showMessageDialog(null, "User approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "User not found in the non-approved list.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error approving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
