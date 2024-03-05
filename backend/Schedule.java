import java.util.Set;

//@Entity
//@Table(name = "schedules")
public class Schedule {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@OneToOne
    //@JoinColumn(name = "student_id", nullable = false)
    private Student student;

    //OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Lesson> lessons;

    // Additional fields, like startDate and endDate, can be added if needed

    // Default constructor
    public Schedule() {
    }

    // Constructor with fields
    public Schedule(Student student) {
        this.student = student;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    // toString method for debugging
    //@Override
    public String toString() {
        return "Schedule{" +
               "id=" + id +
               ", student=" + (student != null ? student.getFirstName() + student.getLastName(): "None") +
               '}';
    }
}
