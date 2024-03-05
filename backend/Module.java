import java.util.Set;

//@Entity
//@Table(name = "modules")
public class Module {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "code", nullable = false, unique = true)
    private String code;

    //@Column(name = "name", nullable = false)
    private String name;

   // @Column(name = "description")
    private String description;

    //@ManyToOne
    //@JoinColumn(name = "course_id", nullable = false)
    private Course course;

   // @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Lesson> lessons;

   // @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Exam> exams;

    // Default constructor
    public Module() {
    }

    // Constructor with fields
    public Module(String code, String name, String description, Course course) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.course = course;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Set<Exam> getExams() {
        return exams;
    }

    public void setExams(Set<Exam> exams) {
        this.exams = exams;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Module{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", course=" + (course != null ? course.getName() : "None") +
               '}';
    }
}
