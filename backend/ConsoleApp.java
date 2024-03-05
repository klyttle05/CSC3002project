import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            try {
                System.out.println("\nUniversity Management System");
                System.out.println("1. Add Course");
                System.out.println("2. Add Department");
                System.out.println("3. Add Event");
                System.out.println("4. Add Exam");
                System.out.println("5. Add Lesson");
                System.out.println("6. Add Module");
                System.out.println("7. Add Room");
                System.out.println("8. Add Schedule");
                System.out.println("9. Add Staff Member");
                System.out.println("10. Add Student");
                System.out.println("11. Show All Classes");
                System.out.println("X. Exit");

                System.out.print("Enter option: ");
                String option = scanner.nextLine();

                switch (option) {
                    case "1": addCourse(); break;
                    case "2": addDepartment(); break;
                    case "3": addEvent(); break;
                    case "4": addExam(); break;
                    case "5": addLesson(); break;
                    case "6": addModule(); break;
                    case "7": addRoom(); break;
                    case "8": addSchedule(); break;
                    case "9": addStaffMember(); break;
                    case "10": addStudent(); break;
                    case "11": printAll(); break;
                    case "X":
                    case "x": exit = true; break;
                    default: System.out.println("Invalid option, please try again.");
                }
                
                //Exception Handling
            } catch (InputMismatchException e) {
                System.out.println("Input was not in the correct format. Please try again.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void addCourse() {
        System.out.println("Enter Course Details:");
    
        System.out.print("Course Name: ");
        String name = scanner.nextLine();
    
        System.out.print("Course Code: ");
        String code = scanner.nextLine();
    
        System.out.print("Course Description: ");
        String description = scanner.nextLine();
    
        // Since we don't have an actual Department class or a way to select it, let's skip it for now
        // In a complete implementation, you would retrieve or create a Department object here
        Department department = null; // Placeholder for department
    
        Course course = new Course(name, code, description, department);
        System.out.println("Created Course: " + course);
    }
    

    private static void addDepartment() {
        System.out.println("Enter Department Details:");
    
        System.out.print("Department Name: ");
        String name = scanner.nextLine();
    
        Department department = new Department(name);
        // Note: Courses and staff members would typically be managed separately
    
        System.out.println("Created Department: " + department.toString());
    }
    

    private static void addEvent() {
        System.out.println("Enter Event Details:");
    
        System.out.print("Event Title: ");
        String title = scanner.nextLine();
    
        System.out.print("Start Time (e.g., 2023-01-01T10:00): ");
        LocalDateTime startTime = readDateTime();
    
        System.out.print("End Time (e.g., 2023-01-01T12:00): ");
        LocalDateTime endTime = readDateTime();
    
        // Assuming a simple string for location. In a full application, this would be a Room object
        System.out.print("Location: ");
        String location = scanner.nextLine(); // Placeholder for location
    
        System.out.print("Description: ");
        String description = scanner.nextLine();
    
        Event event = new Event(title, startTime, endTime, null, description); // Location is null for now
        System.out.println("Created Event: " + event);
    }
    
    private static LocalDateTime readDateTime() {
        while (true) {
            try {
                String dateTimeInput = scanner.nextLine();
                return LocalDateTime.parse(dateTimeInput, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                System.out.print("Invalid format. Please enter date and time in format YYYY-MM-DDTHH:MM: ");
            }
        }
    }

    private static void addExam() {
        System.out.println("Enter Exam Details:");
    
        System.out.print("Exam Title: ");
        String title = scanner.nextLine();
    
        System.out.print("Start Time (e.g., 2023-01-01T09:00): ");
        LocalDateTime startTime = readDateTime();
    
        System.out.print("End Time (e.g., 2023-01-01T11:00): ");
        LocalDateTime endTime = readDateTime();
    
        // For simplicity, using a string as a placeholder for module.
        // In a full application, this would be a Module object.
        System.out.print("Module Name: ");
        String moduleName = scanner.nextLine(); // Placeholder for module
        Module module = new Module(null,moduleName,null, null); // Example instantiation
    
        System.out.print("Duration in Minutes: ");
        int durationMinutes = scanner.nextInt();
        scanner.nextLine(); // Consume the newline left by nextInt
    
        // Assuming location is a simple string. In a full application, this would be a Room object.
        System.out.print("Location: ");
        String location = scanner.nextLine(); // Placeholder for location
    
        Exam exam = new Exam(title, startTime, endTime, null, module, durationMinutes); // Location is null for now
        System.out.println("Created Exam: " + exam);
    }

    private static void addLesson() {
        System.out.println("Enter Lesson Details:");
    
        System.out.print("Lesson Title: ");
        String title = scanner.nextLine();
    
        System.out.print("Start Time (e.g., 2023-01-01T10:00): ");
        LocalDateTime startTime = readDateTime();
    
        System.out.print("End Time (e.g., 2023-01-01T11:00): ");
        LocalDateTime endTime = readDateTime();
    
        // Placeholder for Module
        System.out.print("Module Name: ");
        String moduleName = scanner.nextLine();
        //Module module = new Module(moduleName); // Example instantiation
    
        // Placeholder for Staff (Instructor)
        System.out.print("Instructor Name: ");
        String instructorName = scanner.nextLine();
        //Staff instructor = new Staff(instructorName); // Example instantiation
    
        // Assuming location is a simple string for now
        System.out.print("Location: ");
        String location = scanner.nextLine(); // Placeholder for location
    
        Lesson lesson = new Lesson(title, startTime, endTime, null, null, null); // Location is null for now
        System.out.println("Created Lesson: " + lesson);
    }
      

    private static void addModule() {
        System.out.println("Enter Module Details:");
    
        System.out.print("Module Code: ");
        String code = scanner.nextLine();
    
        System.out.print("Module Name: ");
        String name = scanner.nextLine();
    
        System.out.print("Module Description: ");
        String description = scanner.nextLine();
    
        // Placeholder for Course
        // In a full application, you would likely select an existing course or create a new one
        System.out.print("Associated Course Name: ");
        String courseName = scanner.nextLine();
        //Course course = new Course(courseName); // Example instantiation
    
        Module module = new Module(code, name, description, null);
        System.out.println("Created Module: " + module);
    }
    

    private static void addRoom() {
        System.out.println("Enter Room Details:");
    
        System.out.print("Room Name: ");
        String name = scanner.nextLine();
    
        System.out.print("Room Capacity: ");
        int capacity;
        while (true) {
            try {
                capacity = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number for capacity: ");
            }
        }
    
        System.out.print("Room Location: ");
        String location = scanner.nextLine();
    
        Room room = new Room(name, capacity, location);
        System.out.println("Created Room: " + room);
    }
    

    private static void addSchedule() {
        System.out.println("Enter Student Details for Schedule:");
    
        System.out.print("Student Number: ");
        String studentNumber = scanner.nextLine();
    
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
    
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
    
        System.out.print("Email: ");
        String email = scanner.nextLine();
    
        System.out.print("Date of Birth (e.g., 2000-01-01): ");
        Date dateOfBirth = readDate();
    
        Student student = new Student(studentNumber, firstName, lastName, email, dateOfBirth);
    
        Schedule schedule = new Schedule(student);
        System.out.println("Created Schedule: " + schedule);
    }
    
    private static Date readDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        while (true) {
            try {
                return dateFormat.parse(scanner.nextLine());
            } catch (ParseException e) {
                System.out.print("Invalid format. Please enter date in format YYYY-MM-DD: ");
            }
        }
    }

    private static void addStaffMember() {
        System.out.println("Enter Staff Member Details:");
    
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
    
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
    
        System.out.print("Email: ");
        String email = scanner.nextLine();
    
        // Placeholder for Department
        // In a full application, you would likely select an existing department or create a new one
        System.out.print("Department Name: ");
        String departmentName = scanner.nextLine();
        Department department = new Department(departmentName); // Example instantiation
    
        Staff staffMember = new Staff(firstName, lastName, email, department);
        System.out.println("Created Staff Member: " + staffMember);
    }
    

    private static void addStudent() {
        System.out.println("Enter Student Details:");
    
        System.out.print("Student Number: ");
        String studentNumber = scanner.nextLine();
    
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
    
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
    
        System.out.print("Email: ");
        String email = scanner.nextLine();
    
        System.out.print("Date of Birth (e.g., 2000-01-01): ");
        Date dateOfBirth = readDate();
    
        Student student = new Student(studentNumber, firstName, lastName, email, dateOfBirth);
        System.out.println("Created Student: " + student);
    }

    private static void printAll() {
        List<Class<?>> classes = new ArrayList<>();
        
        classes.add(Course.class);
        classes.add(Department.class);
        classes.add(Event.class);
        classes.add(Lesson.class);
        classes.add(Module.class);
        classes.add(Room.class);
        classes.add(Schedule.class);
        classes.add(ScheduledActivity.class);
        classes.add(Staff.class);
        classes.add(Exam.class);
        classes.add(Student.class);

        for (Class<?> cls : classes) {
            System.out.println("Class: " + cls.getSimpleName());
            System.out.println("Properties:");
            for (Field field : cls.getDeclaredFields()) {
                System.out.println(" - " + field.getType().getSimpleName() + " " + field.getName());
            }
            System.out.println();
        }
    }
}


