import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ITS implements DepartmentRequests {
    private JFrame frame;
    private DatabaseConnectionWrapper connWrapper;
    private int userId;

    public ITS(DatabaseConnectionWrapper connWrapper, int userId) {
        this.connWrapper = connWrapper;
        this.userId = userId;
    }
	
	public int getUserId(){
		return this.userId;
	}

    @Override
    public void showMainPanel() {
        frame = new JFrame("ITS Request Manager");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton seeRequestsButton = new JButton("See Requests");
        JButton myRequestsButton = new JButton("My Requests");
        JButton logOutButton = new JButton("Log Out");

        seeRequestsButton.addActionListener(e -> openRequestsPanel());
        myRequestsButton.addActionListener(e -> openMyRequestsPanel());
        logOutButton.addActionListener(e -> {
            frame.dispose();
            Start.main(new String[]{}); // Reopen the login screen
        });

        buttonPanel.add(seeRequestsButton);
        buttonPanel.add(myRequestsButton);
        buttonPanel.add(logOutButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

	public void openRequestsPanel() {
		frame.getContentPane().removeAll();
		JPanel requestPanel = new JPanel(new BorderLayout());

		JTable requestTable = new JTable();
		DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Request ID", "Room", "Issue", "Status", "Responder"}, 0);
		requestTable.setModel(tableModel);

		// SQL query to get request details and responder's name 
		String query = "SELECT r.request_id, r.room, r.issue_description, r.status, " +
					   "CONCAT(a.firstName, ' ', a.lastName) AS responder " +  
					   "FROM its_requests r " +
					   "LEFT JOIN users a ON r.accepted_by = a.idNumber " +
					   "WHERE r.status = 'Pending'";

		try (Connection conn = connWrapper.getConnection();
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery()) {

			// Loop through the result set and populate the table
			while (rs.next()) {
				int requestId = rs.getInt("request_id");
				String room = rs.getString("room");  // Get the room value here
				String issue = rs.getString("issue_description");
				String status = rs.getString("status");

				// Get responder's full name (which is concatenated in the query)
				String responder = rs.getString("responder");

				// Add a row to the table model
				tableModel.addRow(new Object[]{requestId, room, issue, status, responder});
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(frame, "Error loading requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Add the table to the panel with a scroll pane
		requestPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

		// action buttons 
		JPanel buttonPanel = createActionButtons(requestTable, tableModel);
		requestPanel.add(buttonPanel, BorderLayout.SOUTH);

		// Add the request panel to the frame and refresh the table
		frame.add(requestPanel);
		frame.revalidate();
		frame.repaint();
	}

    @Override
    public void openMyRequestsPanel() {
        frame.getContentPane().removeAll();
        JPanel myRequestsPanel = new JPanel(new BorderLayout());

        JTable myRequestsTable = new JTable();
        DefaultTableModel myTableModel = new DefaultTableModel(new String[]{"ID", "Room", "Issue", "Status"}, 0);
        myRequestsTable.setModel(myTableModel);

        try (Connection conn = connWrapper.getConnection()) {
            String query = "SELECT * FROM its_requests WHERE accepted_by = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int requestId = rs.getInt("request_id");
                        String room = rs.getString("room");
                        String issue = rs.getString("issue_description");
                        String status = rs.getString("status");
                        myTableModel.addRow(new Object[]{requestId, room, issue, status});
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading your requests: " + ex.getMessage());
        }

        myRequestsPanel.add(new JScrollPane(myRequestsTable), BorderLayout.CENTER);
        JPanel buttonPanel = createCompletionButtons(myRequestsTable, myTableModel);
        myRequestsPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(myRequestsPanel);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void updateRequestStatus(int requestId, String status) {
        try (Connection conn = connWrapper.getConnection()) {
            String query = "UPDATE its_requests SET status = ? WHERE request_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, status);
                pst.setInt(2, requestId);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating request status: " + ex.getMessage());
        }
    }

	private String getCurrentUserName() {
		String fullName = "none"; //initialize
		String query = "SELECT firstName, lastName FROM users WHERE idNumber = ?";

		try (Connection conn = connWrapper.getConnection();
			 PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setInt(1, userId);  // Use the current user ID
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					String firstName = rs.getString("firstName");
					String lastName = rs.getString("lastName");
					fullName = firstName + " " + lastName;  // Concatenate first and last name
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(frame, "Error fetching user name: " + ex.getMessage());
		}

		return fullName;
	}

    @Override
    public void updateResponder(int requestId, String responder) {
        try (Connection conn = connWrapper.getConnection()) {
            String query = "UPDATE its_requests SET accepted_by = ? WHERE request_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, responder);
                pst.setInt(2, requestId);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating responder: " + ex.getMessage());
        }
    }

    private JPanel createActionButtons(JTable requestTable, DefaultTableModel tableModel) {
    JPanel buttonPanel = new JPanel();
    JButton acceptButton = new JButton("Accept");
    JButton declineButton = new JButton("Decline");
    JButton closeButton = new JButton("Close");

    acceptButton.addActionListener(e -> {
		int selectedRow = requestTable.getSelectedRow();
		if (selectedRow != -1) {
			// Get the request ID of the selected row
			int requestId = (int) tableModel.getValueAt(selectedRow, 0);

			// Update the request status to "Processing"
			updateRequestStatus(requestId, "Processing");

			// Update the database to set the current user as the responder
			updateResponder(requestId, String.valueOf(userId));

			// Fetch the current user's full name
			String currentUserName = getCurrentUserName();

			// Update the table with the new responder's name
			tableModel.setValueAt("Processing", selectedRow, 3);  // Update status to "Processing"
			tableModel.setValueAt(currentUserName, selectedRow, 4);  // Update responder with current user's name
		} else {
			JOptionPane.showMessageDialog(frame, "Please select a request to accept.");
		}
	});


    declineButton.addActionListener(e -> {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow != -1) {
            int requestId = (int) tableModel.getValueAt(selectedRow, 0);
            updateRequestStatus(requestId, "Declined");
            tableModel.setValueAt("Declined", selectedRow, 3);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a request to decline.");
        }
    });

    closeButton.addActionListener(e -> {
        frame.dispose(); 
        showMainPanel(); 
    });

    buttonPanel.add(acceptButton);
    buttonPanel.add(declineButton);
    buttonPanel.add(closeButton);

    return buttonPanel;
}

	private JPanel createCompletionButtons(JTable myRequestsTable, DefaultTableModel myTableModel) {
		JPanel buttonPanel = new JPanel();
		JButton completeButton = new JButton("Complete");
		JButton closeButton = new JButton("Close");

		completeButton.addActionListener(e -> {
			int selectedRow = myRequestsTable.getSelectedRow();
			if (selectedRow != -1) {
				int requestId = (int) myTableModel.getValueAt(selectedRow, 0);
				String currentStatus = (String) myTableModel.getValueAt(selectedRow, 3);

				if ("Processing".equalsIgnoreCase(currentStatus)) {
					updateRequestStatus(requestId, "Completed");
					myTableModel.setValueAt("Completed", selectedRow, 3);
				} else {
					JOptionPane.showMessageDialog(frame, "Only requests in 'Processing' status can be completed.");
				}
			} else {
				JOptionPane.showMessageDialog(frame, "Please select a request to complete.");
			}
		});

		closeButton.addActionListener(e -> {
			frame.dispose(); 
			showMainPanel(); 
		});

		buttonPanel.add(completeButton);
		buttonPanel.add(closeButton);

		return buttonPanel;
	}

}
