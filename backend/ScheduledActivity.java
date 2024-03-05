
import java.time.LocalDateTime;

//@MappedSuperclass
public abstract class ScheduledActivity {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

   //@Column(name = "title", nullable = false)
    protected String title;

    //@Column(name = "start_time", nullable = false)
    protected LocalDateTime startTime;

    //@Column(name = "end_time", nullable = false)
    protected LocalDateTime endTime;

   // @ManyToOne
   // @JoinColumn(name = "room_id")
    protected Room location;

    // Standard Constructors, Getters, and Setters

    public ScheduledActivity() {
    }

    public ScheduledActivity(String title, LocalDateTime startTime, LocalDateTime endTime, Room location) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Room getLocation() {
        return location;
    }

    public void setLocation(Room location) {
        this.location = location;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "ScheduledActivity{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", location=" + (location != null ? location.getName() : "None") +
               '}';
    }
}
