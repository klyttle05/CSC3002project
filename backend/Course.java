import java.util.Set;

//@Entity
//@Table(name = "courses")
public class Course {

    ///@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "name", nullable = false)
    private String name;

    //@Column(name = "code", nullable = false, unique = true)
    private String code;

    //@Column(name = "description")
    private String description;

    //@ManyToOne
   //@JoinColumn(name = "department_id")
    private Department department;

    //@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Module> modules;

    //@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Exam> exams;

    // Default constructor
    public Course() {
    }

    // Constructor with fields
    public Course(String name, String code, String description, Department department) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.department = department;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
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
        return "Course{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", code='" + code + '\'' +
               ", description='" + description + '\'' +
               ", department=" + (department != null ? department.getName() : "None") +
               '}';
    }
}
