import java.time.LocalDateTime;

public class Exam extends ScheduledActivity {

    //@ManyToOne
    //@JoinColumn(name = "module_id", nullable = false)
    private Module module;

    //@Column(name = "duration_minutes")
    private int durationMinutes;

    // Additional exam-specific attributes can be added here
    // Default constructor
    public Exam() {
        super();
    }

    // Constructor with fields
    public Exam(String title, LocalDateTime startTime, LocalDateTime endTime, Room location, Module module, int durationMinutes) {
        super(title, startTime, endTime, location);
        this.module = module;
        this.durationMinutes = durationMinutes;
    }

    // Getters and Setters for Exam-specific fields

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Exam{" +
               "id=" + getId() +
               ", title='" + getTitle() + '\'' +
               ", startTime=" + getStartTime() +
               ", endTime=" + getEndTime() +
               ", location=" + (getLocation() != null ? getLocation().getName() : "None") +
               ", module=" + (module != null ? module.getName() : "None") +
               ", durationMinutes=" + durationMinutes +
               '}';
    }
}
