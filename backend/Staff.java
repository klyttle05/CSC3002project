import java.util.Set;

//@Entity
//@Table(name = "staff")
public class Staff {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "first_name", nullable = false)
    private String firstName;

    //@Column(name = "last_name", nullable = false)
    private String lastName;

    //@Column(nullable = false, unique = true)
    private String email;

    //@ManyToOne
    //@JoinColumn(name = "department_id")
    private Department department;

    //@OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Lesson> lessons;

    // Default constructor
    public Staff() {
    }

    // Constructor with fields
    public Staff(String firstName, String lastName, String email, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Staff{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", department=" + (department != null ? department.getName() : "None") +
               '}';
    }
}
