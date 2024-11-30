import javax.swing.*;
import java.sql.Connection;

public interface DepartmentRequests {
    void showMainPanel();          // Shows the main panel with options (See Requests, My Requests, Log Out)
    void openRequestsPanel();      // Opens the "See Requests" panel
    void openMyRequestsPanel();    // Opens the "My Requests" panel
    void updateRequestStatus(int requestId, String status);  // Updates the request status
    void updateResponder(int requestId, String responder);   // Updates the responder for a request
}
