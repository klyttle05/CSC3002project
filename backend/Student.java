import java.util.Date;
import java.util.Set;

//@Entity
//@Table(name = "students")
public class Student {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

   // @Column(name = "first_name", nullable = false)
    private String firstName;

    //@Column(name = "last_name", nullable = false)
    private String lastName;

    //@Column(nullable = false, unique = true)
    private String email;

    //@Column(name = "date_of_birth")
    //@Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    //@OneToOne(mappedBy = "student")
    private Schedule schedule;

    //@ManyToMany
    //@JoinTable(
    //    name = "student_course_enrollment",
    //    joinColumns = @JoinColumn(name = "student_id"),
    //    inverseJoinColumns = @JoinColumn(name = "course_id")
    //)
    private Set<Course> enrolledCourses;

    // Default constructor
    public Student() {
    }

    // Constructor with fields
    public Student(String studentNumber, String firstName, String lastName, String email, Date dateOfBirth) {
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Set<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(Set<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Student{" +
               "id=" + id +
               ", studentNumber='" + studentNumber + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", dateOfBirth=" + dateOfBirth +
               '}';
    }
}
