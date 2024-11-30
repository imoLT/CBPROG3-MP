public class Schedule {
    private String day;
    private String timeSlot;
    private String roomName;

    // Constructor
    public Schedule(String day, String timeSlot, String roomName) {
        this.day = day;
        this.timeSlot = timeSlot;
        this.roomName = roomName;
    }

    // Getter for Day
    public String getDay() {
        return day;
    }

    // Getter for Time Slot
    public String getTimeSlot() {
        return timeSlot;
    }

    // Getter for Room Name
    public String getRoomName() {
        return roomName;
    }

    // Optional: Override toString method for easier debugging/printing
    @Override
    public String toString() {
        return "Day: " + day + ", Time Slot: " + timeSlot + ", Room: " + roomName;
    }
}
