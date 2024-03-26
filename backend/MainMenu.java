import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    private String userType;

    public MainMenu(String userType) {
        this.userType = userType;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Main Menu - " + userType);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        setLayout(new GridLayout(0, 1)); // Adjust grid layout rows and columns as needed
        JLabel welcomeLabel = new JLabel("Welcome, " + userType + "!", JLabel.CENTER);
        add(welcomeLabel);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5)); // Adjust grid layout for better UI
        if ("Admin".equals(userType)) {
            addButton(panel, "Timetable for Staff member", new StaffTimetableScreen());
            addButton(panel, "Timetable for Room", new RoomTimetableScreen());
            addButton(panel, "Timetable for Module", new ModuleTimetableScreen());
            addButton(panel, "Book Activity", new BookActivityScreen());
            addButton(panel, "Delete Activity/Record", new DeleteActivityScreen());
            addButton(panel, "Add Activity/Record", new AddActivityScreen());
            addButton(panel, "Search Student Timetable", new SearchStudentTimetableScreen());
        } else if ("Student".equals(userType)) {
            addButton(panel, "Your Teachers", new YourTeachersScreen());
            addButton(panel, "Your Modules", new YourModulesScreen());
            addButton(panel, "Show Student Timetable", new ShowStudentTimetableScreen());
        }

        add(panel);
    }

    private void addButton(JPanel panel, String text, JFrame screen) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            screen.setVisible(true);
            MainMenu.this.setVisible(false); // Optionally hide the main menu
        });
        panel.add(button);
    }

    // Placeholder classes representing different screens
    // You would replace these with actual class implementations
    class StaffTimetableScreen extends JFrame { /* Implementation */ }
    class RoomTimetableScreen extends JFrame { /* Implementation */ }
    class ModuleTimetableScreen extends JFrame { /* Implementation */ }
    class BookActivityScreen extends JFrame { /* Implementation */ }
    class DeleteActivityScreen extends JFrame { /* Implementation */ }
    class AddActivityScreen extends JFrame { /* Implementation */ }
    class SearchStudentTimetableScreen extends JFrame { /* Implementation */ }
    class YourTeachersScreen extends JFrame { /* Implementation */ }
    class YourModulesScreen extends JFrame { /* Implementation */ }
    class ShowStudentTimetableScreen extends JFrame { /* Implementation */ }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu("Admin").setVisible(true));
        // Replace "Admin" with "Student" to test the student view.
    }
}
