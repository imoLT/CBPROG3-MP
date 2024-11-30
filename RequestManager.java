import java.util.List;

public interface RequestManager {

    public class Request {
        private int requestId;
        private String location;
        private String issueDescription;
        private String status;
        private int acceptedBy;

        public Request(int requestId, String location, String issueDescription, String status, int acceptedBy) {
            this.requestId = requestId;
            this.location = location;
            this.issueDescription = issueDescription;
            this.status = status;
            this.acceptedBy = acceptedBy;
        }

        public int getRequestId() {
            return requestId;
        }

        public String getLocation() {
            return location;
        }

        public String getIssueDescription() {
            return issueDescription;
        }

        public String getStatus() {
            return status;
        }

        public int getAcceptedBy() {
            return acceptedBy;
        }
    }

    List<Request> getPendingRequests(int itsUserId);
    void acceptRequest(int requestId, int itsUserId);
    void declineRequest(int requestId);
    void completeRequest(int requestId, int itsUserId);
}
