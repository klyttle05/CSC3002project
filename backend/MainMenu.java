import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainMenu extends JFrame {
    private String userType;
    private String userId;

    public MainMenu(String userType, String userId) {
        this.userType = userType;
        this.userId = userId;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Main Menu - " + userType);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        setLayout(new GridLayout(0, 1));
        JLabel welcomeLabel = new JLabel("Welcome, " + userType + ": " + userId + "!", JLabel.CENTER);
        add(welcomeLabel);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        if ("Admin".equals(userType)) {
            addButton(panel, "Timetable for Staff Member", e -> openStaffTimetable());
            addButton(panel, "Timetable for Room", e -> openRoomTimetable());
            addButton(panel, "Timetable for Module", e -> openModuleTimetable());
            addButton(panel, "Book Activity", e -> bookActivity());
            addButton(panel, "Delete Activity/Record", e -> deleteActivity());
            addButton(panel, "Search Student Timetable", e -> searchStudentTimetable());
            addButton(panel, "Create Staff User", e -> openCreateStaffScreen());
            addButton(panel, "Create Student User", e -> openCreateStudentScreen());
        } else if ("Student".equals(userType)) {
            addButton(panel, "Your Teachers", e -> showTeachers());
            addButton(panel, "Your Modules", e -> showModules());
            addButton(panel, "Show Student Timetable", e -> showStudentTimetable());
        }

        add(panel);
    }

    private void addButton(JPanel panel, String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        panel.add(button);
    }

    // Implemented action methods for admin and student functionalities
    private void openStaffTimetable() {
        StaffTimetableScreen staffTimetableScreen = new StaffTimetableScreen();
        staffTimetableScreen.setVisible(true);
    }

    private void openRoomTimetable() {
        RoomTimetableScreen roomTimetableScreen = new RoomTimetableScreen();
        roomTimetableScreen.setVisible(true);
    }

    private void openModuleTimetable() {
        ModuleTimetableScreen moduleTimetableScreen = new ModuleTimetableScreen();
        moduleTimetableScreen.setVisible(true);
    }

    private void bookActivity() {
        BookActivityScreen bookActivityScreen = new BookActivityScreen();
        bookActivityScreen.setVisible(true);
    }

    private void deleteActivity() {
        DeleteActivityScreen deleteActivityScreen = new DeleteActivityScreen();
        deleteActivityScreen.setVisible(true);
    }

    private void searchStudentTimetable() {
        StudentTimetableScreen studentTimetableScreen = new StudentTimetableScreen();
        studentTimetableScreen.setVisible(true);
    }

    private void openCreateStaffScreen() {
        CreateStaffScreen createStaffScreen = new CreateStaffScreen();
        createStaffScreen.setVisible(true);
    }

    private void openCreateStudentScreen() {
        CreateStudentScreen createStudentScreen = new CreateStudentScreen();
        createStudentScreen.setVisible(true);
    }

    // Method to display the teachers associated with the logged-in student
    private void showTeachers() {
        MyTeachersScreen myTeachersScreen = new MyTeachersScreen(userId);
        myTeachersScreen.setVisible(true);
    }

    // Method to display the modules registered by the logged-in student
    private void showModules() {
        MyModulesScreen myModulesScreen = new MyModulesScreen(userId);
        myModulesScreen.setVisible(true);
    }

    // Method to display the timetable for the logged-in student
    private void showStudentTimetable() {
        int userIDint = Integer.parseInt(userId);
        MyTimetableScreen myTimetableScreen = new MyTimetableScreen(userIDint);
        myTimetableScreen.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu("Admin", "A1234567").setVisible(true));
    }
}
