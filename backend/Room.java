import java.util.Set;

//@Entity
//@Table(name = "rooms")
public class Room {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "name", nullable = false)
    private String name;

    //@Column(name = "capacity")
    private int capacity;

    //@Column(name = "location")
    private String location;

    //@OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScheduledActivity> scheduledActivities;

    // Default constructor
    public Room() {
    }

    // Constructor with fields
    public Room(String name, int capacity, String location) {
        this.name = name;
        this.capacity = capacity;
        this.location = location;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<ScheduledActivity> getScheduledActivities() {
        return scheduledActivities;
    }

    public void setScheduledActivities(Set<ScheduledActivity> scheduledActivities) {
        this.scheduledActivities = scheduledActivities;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Room{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", capacity=" + capacity +
               ", location='" + location + '\'' +
               '}';
    }
}
