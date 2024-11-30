import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecurityModel {
    private Connection conn;

    public SecurityModel(Connection conn) {
        this.conn = conn;
    }

    public List<RequestManager.Request> getPendingRequests(int securityUserId) {
        List<RequestManager.Request> requests = new ArrayList<>();
        String query = "SELECT * FROM security_requests WHERE status = 'Pending'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("request_id");
                String room = rs.getString("room");
                String issue = rs.getString("issue_description");
                String status = rs.getString("status");

                // Assuming the RequestManager.Request constructor takes these parameters
                RequestManager.Request request = new RequestManager.Request(id, room, issue, status, securityUserId);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public void acceptRequest(int requestId, int securityUserId) {
        String sql = "UPDATE security_requests SET status = 'Processing', accepted_by = ?, updated_at = NOW() WHERE request_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, securityUserId);
            pst.setInt(2, requestId);
            int rowsAffected = pst.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);  // Debugging output
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void declineRequest(int requestId) {
        String sql = "UPDATE security_requests SET status = 'Declined', updated_at = NOW() WHERE request_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, requestId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void completeRequest(int requestId, int securityUserId) {
        String sql = "UPDATE security_requests SET status = 'Completed', completed_by = ?, updated_at = NOW() WHERE request_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, securityUserId);
            pst.setInt(2, requestId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
