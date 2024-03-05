import java.util.Set;

//@Entity
//@Table(name = "departments")
public class Department {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "name", nullable = false)
    private String name;

    //@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Course> courses;

    //@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Staff> staffMembers;

    // Default constructor
    public Department() {
    }

    // Constructor with fields
    public Department(String name) {
        this.name = name;
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

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public Set<Staff> getStaffMembers() {
        return staffMembers;
    }

    public void setStaffMembers(Set<Staff> staffMembers) {
        this.staffMembers = staffMembers;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Department{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
