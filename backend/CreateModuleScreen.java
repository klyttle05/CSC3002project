import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

public class CreateModuleScreen extends JFrame {
    private JTextField moduleNameField;
    private JComboBox<String> staffDropdown; // Assume staff IDs are loaded here
    private JList<Student> studentList;
    private DefaultListModel<Student> studentListModel;
    private JButton submitButton;
    private JComboBox<String> dayOfWeekDropdown;
    private JSpinner startTimeSpinner, endTimeSpinner;

    public CreateModuleScreen() {
        initializeUI();
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Module Name:"));
        moduleNameField = new JTextField();
        add(moduleNameField);

        add(new JLabel("Assign Staff:"));
        staffDropdown = new JComboBox<>();
        add(staffDropdown);

        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        JScrollPane scrollPane = new JScrollPane(studentList);
        add(new JLabel("Select Students:"));
        add(scrollPane);

        add(new JLabel("Day of Week:"));
        dayOfWeekDropdown = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        add(dayOfWeekDropdown);

        add(new JLabel("Start Time:"));
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        add(startTimeSpinner);

        add(new JLabel("End Time:"));
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        add(endTimeSpinner);

        submitButton = new JButton("Create Module");
        add(submitButton);
        submitButton.addActionListener(this::createModuleAndScheduleLessons);
    }

    private void createModuleAndScheduleLessons(ActionEvent e) {
        String moduleName = moduleNameField.getText();
        String selectedStaff = (String) staffDropdown.getSelectedItem();
        int staffId = Integer.parseInt(selectedStaff.split(" - ")[0]); // Assuming the format "ID - Name"
        LocalDate startDate = LocalDate.now(); // This could be a chosen date instead of 'now'
        LocalTime startTime = LocalTime.parse((String) startTimeSpinner.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse((String) endTimeSpinner.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
        String dayOfWeek = (String) dayOfWeekDropdown.getSelectedItem();
    
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
    
            // Insert the module
            String insertModuleSql = "INSERT INTO Modules (name, staff_id) VALUES (?, ?)";
            long moduleId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertModuleSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, moduleName);
                pstmt.setInt(2, staffId);
                pstmt.executeUpdate();
    
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    moduleId = rs.getLong(1);
                } else {
                    throw new SQLException("Failed to insert module, no ID obtained.");
                }
            }
    
            // Schedule lessons for the next 8 weeks
            String insertActivitySql = "INSERT INTO ScheduledActivities (module_id, start_time, end_time) VALUES (?, ?, ?)";
            for (int i = 0; i < 8; i++) { // for each week
                LocalDate lessonDate = startDate.plusWeeks(i).with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(dayOfWeek.toUpperCase())));
                Timestamp startTimestamp = Timestamp.valueOf(lessonDate.atTime(startTime));
                Timestamp endTimestamp = Timestamp.valueOf(lessonDate.atTime(endTime));
    
                try (PreparedStatement pstmt = conn.prepareStatement(insertActivitySql)) {
                    pstmt.setLong(1, moduleId);
                    pstmt.setTimestamp(2, startTimestamp);
                    pstmt.setTimestamp(3, endTimestamp);
                    pstmt.executeUpdate();
                }
            }
    
            // Register selected students to the module
            List<Student> selectedStudents = studentList.getSelectedValuesList();
            String insertRegistrationSql = "INSERT INTO EventParticipants (module_id, student_id) VALUES (?, ?)";
            for (Student student : selectedStudents) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertRegistrationSql)) {
                    pstmt.setLong(1, moduleId);
                    pstmt.setInt(2, student.id);
                    pstmt.executeUpdate();
                }
            }
    
            conn.commit();
            JOptionPane.showMessageDialog(this, "Module created and lessons scheduled successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException exRollback) {
                exRollback.printStackTrace();
            }
        }
    }
    

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateModuleScreen().setVisible(true));
    }

    // Inner class for Student model
    class Student {
        int id;
        String name;

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (" + id + ")";
        }
    }
}
