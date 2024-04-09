import java.awt.GridLayout;
import java.awt.List;
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
    private JComboBox<String> staffDropdown, roomDropdown, dayOfWeekDropdown;
    private JList<Student> studentList;
    private DefaultListModel<Student> studentListModel;
    private JButton submitButton;
    private JSpinner startTimeSpinner, endTimeSpinner;

    public CreateModuleScreen() {
        initializeUI();
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadStaffMembers();
        loadRooms();
        loadStudents();
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

        add(new JLabel("Room:"));
        roomDropdown = new JComboBox<>();
        add(roomDropdown);

        submitButton = new JButton("Create Module");
        add(submitButton);
        submitButton.addActionListener(this::createModuleAndScheduleLessons);
    }

    private void loadStaffMembers() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT staff_id, CONCAT(first_name, ' ', last_name) AS name FROM Staff")) {
            while (rs.next()) {
                String staffMember = rs.getInt("staff_id") + " - " + rs.getString("name");
                staffDropdown.addItem(staffMember);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load staff members.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadRooms() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_id, room_name FROM Rooms")) {
            while (rs.next()) {
                String room = rs.getInt("room_id") + " - " + rs.getString("room_name");
                roomDropdown.addItem(room);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, CONCAT(first_name, ' ', last_name) AS name FROM Students")) {
            while (rs.next()) {
                studentListModel.addElement(new Student(rs.getInt("student_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load students.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void createModuleAndScheduleLessons(ActionEvent e) {
        String moduleName = moduleNameField.getText().trim();
        int staffId = Integer.parseInt(((String) staffDropdown.getSelectedItem()).split(" - ")[0]);
        int roomId = Integer.parseInt(((String) roomDropdown.getSelectedItem()).split(" - ")[0]);
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(((String) dayOfWeekDropdown.getSelectedItem()).toUpperCase());
        LocalTime startTime = LocalTime.parse((String) startTimeSpinner.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse((String) endTimeSpinner.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
        List<Student> selectedStudents = studentList.getSelectedValuesList();
    
        String insertModuleSql = "INSERT INTO Modules (name, staff_id) VALUES (?, ?)";
        String insertScheduledActivitySql = "INSERT INTO ScheduledActivities (type, title, start_time, end_time, location, module_id, staff_id, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertStudentModuleRegistrationSql = "INSERT INTO StudentModuleRegistrations (student_id, module_id) VALUES (?, ?)";
        String insertEventParticipantSql = "INSERT INTO EventParticipants (activity_id, student_id) VALUES (?, ?)";
    
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
    
            // Insert module
            long moduleId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertModuleSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, moduleName);
                pstmt.setInt(2, staffId);
                pstmt.executeUpdate();
    
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        moduleId = rs.getLong(1);
                    } else {
                        throw new SQLException("Failed to insert module, no ID obtained.");
                    }
                }
            }
    
            // Register selected students to the module
            try (PreparedStatement pstmt = conn.prepareStatement(insertStudentModuleRegistrationSql)) {
                for (Student student : selectedStudents) {
                    pstmt.setInt(1, student.id);
                    pstmt.setLong(2, moduleId);
                    pstmt.executeUpdate();
                }
            }
    
            // Insert scheduled activities and register students as participants
            for (int i = 0; i < 8; i++) {
                LocalDate lessonDate = LocalDate.now().plusWeeks(i).with(TemporalAdjusters.nextOrSame(dayOfWeek));
                Timestamp startTimestamp = Timestamp.valueOf(lessonDate.atTime(startTime));
                Timestamp endTimestamp = Timestamp.valueOf(lessonDate.atTime(endTime));
    
                long activityId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertScheduledActivitySql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, "Lesson");
                    pstmt.setString(2, moduleName + " Lesson");
                    pstmt.setTimestamp(3, startTimestamp);
                    pstmt.setTimestamp(4, endTimestamp);
                    pstmt.setString(5, roomId == -1 ? "Online" : String.valueOf(roomId)); // Assuming -1 or similar for online
                    pstmt.setLong(6, moduleId);
                    pstmt.setInt(7, staffId);
                    pstmt.setInt(8, roomId);
                    pstmt.executeUpdate();
    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            activityId = rs.getLong(1);
                        } else {
                            continue; // Skip if we can't get the activity ID
                        }
                    }
                }
    
                // Register each selected student as a participant for the activity
                try (PreparedStatement pstmt = conn.prepareStatement(insertEventParticipantSql)) {
                    for (Student student : selectedStudents) {
                        pstmt.setLong(1, activityId);
                        pstmt.setInt(2, student.id);
                        pstmt.executeUpdate();
                    }
                }
            }
    
            conn.commit();
            JOptionPane.showMessageDialog(this, "Module created and scheduled lessons added successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    class Student {
        int id;
        String name;

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (" + id + ")";
        }
    }
}
