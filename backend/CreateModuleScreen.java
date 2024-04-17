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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    private JComboBox<String> staffDropdown, roomDropdown, dayOfWeekDropdown, secondDayOfWeekDropdown;
    private JList<Student> studentList;
    private DefaultListModel<Student> studentListModel;
    private JButton submitButton;
    private JSpinner startTimeSpinner, endTimeSpinner, secondStartTimeSpinner, secondEndTimeSpinner;
    private JCheckBox secondDayCheckBox;
    private JLabel statusLabel;

    public CreateModuleScreen() {
        initializeUI();
        setSize(700, 500);
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

        secondDayCheckBox = new JCheckBox("Add Second Day");
        add(secondDayCheckBox);

        secondDayOfWeekDropdown = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        secondDayOfWeekDropdown.setVisible(false);
        add(new JLabel("Second Day of Week:"));
        add(secondDayOfWeekDropdown);

        secondStartTimeSpinner = new JSpinner(new SpinnerDateModel());
        secondStartTimeSpinner.setEditor(new JSpinner.DateEditor(secondStartTimeSpinner, "HH:mm"));
        secondStartTimeSpinner.setVisible(false);
        add(new JLabel("Second Start Time:"));
        add(secondStartTimeSpinner);

        secondEndTimeSpinner = new JSpinner(new SpinnerDateModel());
        secondEndTimeSpinner.setEditor(new JSpinner.DateEditor(secondEndTimeSpinner, "HH:mm"));
        secondEndTimeSpinner.setVisible(false);
        add(new JLabel("Second End Time:"));
        add(secondEndTimeSpinner);

        submitButton = new JButton("Create Module");
        submitButton.addActionListener(this::createModuleAndScheduleLessons);
        add(submitButton);

        statusLabel = new JLabel("");
        add(statusLabel);

        secondDayCheckBox.addActionListener(e -> toggleSecondDay(secondDayCheckBox.isSelected()));
    }

    private void toggleSecondDay(boolean show) {
        secondDayOfWeekDropdown.setVisible(show);
        secondStartTimeSpinner.setVisible(show);
        secondEndTimeSpinner.setVisible(show);
    }

    public void loadStaffMembers() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, CONCAT(first_name, ' ', last_name) AS name FROM Staff")) {
            while (rs.next()) {
                String staffMember = rs.getInt("id") + " - " + rs.getString("name");
                staffDropdown.addItem(staffMember);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load staff members.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void loadRooms() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_id, name FROM Rooms")) {
            while (rs.next()) {
                String room = rs.getInt("room_id") + " - " + rs.getString("name");
                roomDropdown.addItem(room);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void loadStudents() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, CONCAT(first_name, ' ', last_name) AS name FROM Students")) {
            while (rs.next()) {
                studentListModel.addElement(new Student(rs.getInt("id"), rs.getString("name")));
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
        
        java.util.Date startTimeUtilDate = (java.util.Date) startTimeSpinner.getValue();
        java.util.Date endTimeUtilDate = (java.util.Date) endTimeSpinner.getValue();
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startTimeUtilDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(endTimeUtilDate.toInstant(), ZoneId.systemDefault());
        
        boolean secondDayEnabled = secondDayCheckBox.isSelected();
        DayOfWeek secondDayOfWeek = null;
        LocalDateTime secondStartDateTime = null, secondEndDateTime = null;
        
        if (secondDayEnabled) {
            secondDayOfWeek = DayOfWeek.valueOf(((String) secondDayOfWeekDropdown.getSelectedItem()).toUpperCase());
            java.util.Date secondStartTimeUtilDate = (java.util.Date) secondStartTimeSpinner.getValue();
            java.util.Date secondEndTimeUtilDate = (java.util.Date) secondEndTimeSpinner.getValue();
            secondStartDateTime = LocalDateTime.ofInstant(secondStartTimeUtilDate.toInstant(), ZoneId.systemDefault());
            secondEndDateTime = LocalDateTime.ofInstant(secondEndTimeUtilDate.toInstant(), ZoneId.systemDefault());
        }
    
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            long moduleId = insertModule(conn, moduleName, staffId);
            List<Long> activityIds = new ArrayList<>();
            
            activityIds.addAll(scheduleActivities(conn, moduleId, staffId, roomId, dayOfWeek, startDateTime, endDateTime));
            if (secondDayEnabled) {
                activityIds.addAll(scheduleActivities(conn, moduleId, staffId, roomId, secondDayOfWeek, secondStartDateTime, secondEndDateTime));
            }
    
            registerStudentsToModule(conn, moduleId);
            registerStudentsToActivities(conn, activityIds);
            
            conn.commit();
            JOptionPane.showMessageDialog(this, "Module created and scheduled lessons added successfully for both days.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException exRollback) {
                    exRollback.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException exClose) {
                    exClose.printStackTrace();
                }
            }
        }
    }
    
    private List<Long> scheduleActivities(Connection conn, long moduleId, int staffId, int roomId, DayOfWeek dayOfWeek, LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        String insertScheduledActivitySql = "INSERT INTO ScheduledActivities (type, title, start_time, end_time, location, module_id, staff_id, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Long> activityIds = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            LocalDate lessonDate = LocalDate.now().plusWeeks(i).with(TemporalAdjusters.nextOrSame(dayOfWeek));
            Timestamp startTimestamp = Timestamp.valueOf(lessonDate.atTime(startDateTime.toLocalTime()));
            Timestamp endTimestamp = Timestamp.valueOf(lessonDate.atTime(endDateTime.toLocalTime()));
    
            try (PreparedStatement pstmt = conn.prepareStatement(insertScheduledActivitySql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Lesson");
                pstmt.setString(2, moduleName + " Lesson");
                pstmt.setTimestamp(3, startTimestamp);
                pstmt.setTimestamp(4, endTimestamp);
                pstmt.setString(5, "Specified Location");
                pstmt.setLong(6, moduleId);
                pstmt.setInt(7, staffId);
                pstmt.setInt(8, roomId);
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        activityIds.add(rs.getLong(1));
                    }
                }
            }
        }
        return activityIds;
    }
    
    private void registerStudentsToModule(Connection conn, long moduleId) throws SQLException {
        String insertStudentModuleRegistrationSql = "INSERT INTO StudentModuleRegistrations (student_id, module_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertStudentModuleRegistrationSql)) {
            for (Student student : studentList.getSelectedValuesList()) {
                pstmt.setInt(1, student.id);
                pstmt.setLong(2, moduleId);
                pstmt.executeUpdate();
            }
        }
    }
    
    private void registerStudentsToActivities(Connection conn, List<Long> activityIds) throws SQLException {
        String insertEventParticipantSql = "INSERT INTO EventParticipants (activity_id, student_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertEventParticipantSql)) {
            for (Long activityId : activityIds) {
                for (Student student : studentList.getSelectedValuesList()) {
                    pstmt.setLong(1, activityId);
                    pstmt.setInt(2, student.id);
                    pstmt.executeUpdate();
                }
            }
        }
    }
    

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateModuleScreen().setVisible(true));
    }

    public class Student {
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
