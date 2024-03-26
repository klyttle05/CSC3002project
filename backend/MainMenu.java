import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainMenu extends JFrame {
    private String userType;

    private String userId; // This replaces studentId and can represent any user

    public MainMenu(String userType, String userId) {
        this.userType = userType;
        this.userId = userId; // Generic user ID
        initializeUI();
    }
    

    private void initializeUI() {
        setTitle("Main Menu - " + userType);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        setLayout(new GridLayout(0, 1));
        JLabel welcomeLabel = new JLabel("Welcome, " + userType + "!", JLabel.CENTER);
        add(welcomeLabel);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
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
            MainMenu.this.setVisible(false); //hide the main menu
        });
        panel.add(button);
    }

    private void addButton(JPanel panel, String text, Function<String, JFrame> screenSupplier) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            JFrame screen = screenSupplier.apply(this.userId);
            screen.setVisible(true);
            MainMenu.this.setVisible(false); //hide the main menu
        });
    panel.add(button);
    }

    class StaffTimetableScreen extends JFrame { /* Implementation */ }
    class RoomTimetableScreen extends JFrame { /* Implementation */ }
    class ModuleTimetableScreen extends JFrame { /* Implementation */ }
    class BookActivityScreen extends JFrame { /* Implementation */ }
    class DeleteActivityScreen extends JFrame { /* Implementation */ }
    class AddActivityScreen extends JFrame { /* Implementation */ }
    class SearchStudentTimetableScreen extends JFrame { /* Implementation */ }
    class YourTeachersScreen extends JFrame {
        public YourTeachersScreen(String userId) {
            // Implementation that uses userId to fetch and display information
        }
    }
    
    class YourModulesScreen extends JFrame {
        public YourModulesScreen(String userId) {
            // Implementation that uses userId to fetch and display information
        }
    }
    
    // Similar adjustments for admin-specific screens if needed
    
    class ShowStudentTimetableScreen extends JFrame { /* Implementation */ }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu("Student", "S1234567").setVisible(true)); // Example for a student
        // or
        SwingUtilities.invokeLater(() -> new MainMenu("Admin", "A1234567").setVisible(true)); // Example for an admin
    }
    
}
