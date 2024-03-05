import java.time.LocalDateTime;

//@Entity
//@Table(name = "lessons")
public class Lesson extends ScheduledActivity {

    //@ManyToOne
    //@JoinColumn(name = "module_id", nullable = false)
    private Module module;

   //@ManyToOne
    //@JoinColumn(name = "instructor_id", nullable = false)
    private Staff instructor;

    // Additional lesson-specific attributes can be added here

    // Default constructor
    public Lesson() {
        super();
    }

    // Constructor with fields
    public Lesson(String title, LocalDateTime startTime, LocalDateTime endTime, Room location, Module module, Staff instructor) {
        super(title, startTime, endTime, location);
        this.module = module;
        this.instructor = instructor;
    }

    // Getters and Setters for Lesson-specific fields

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Staff getInstructor() {
        return instructor;
    }

    public void setInstructor(Staff instructor) {
        this.instructor = instructor;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Lesson{" +
               "id=" + getId() +
               ", title='" + getTitle() + '\'' +
               ", startTime=" + getStartTime() +
               ", endTime=" + getEndTime() +
               ", location=" + (getLocation() != null ? getLocation().getName() : "None") +
               ", module=" + (module != null ? module.getName() : "None") +
               ", instructor=" + (instructor != null ? instructor.getFirstName() + instructor.getLastName() : "None") +
               '}';
    }
}
