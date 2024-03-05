import java.time.LocalDateTime;

//@Entity
//@Table(name = "events")
public class Event extends ScheduledActivity {

    //@Column(name = "description")
    private String description;

    // Additional attributes or relationships can be added here

    // Default constructor
    public Event() {
        super();
    }

    // Constructor with fields
    public Event(String title, LocalDateTime startTime, LocalDateTime endTime, Room location, String description) {
        super(title, startTime, endTime, location);
        this.description = description;
    }

    // Getters and Setters for Event-specific fields

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Event{" +
               "id=" + getId() +
               ", title='" + getTitle() + '\'' +
               ", startTime=" + getStartTime() +
               ", endTime=" + getEndTime() +
               ", location=" + (getLocation() != null ? getLocation().getName() : "None") +
               ", description='" + description + '\'' +
               '}';
    }
}
