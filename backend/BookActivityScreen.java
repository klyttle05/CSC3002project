import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
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
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

public class BookActivityScreen extends JFrame {
    private JTextField activityNameField, durationField, moduleField;
    private JComboBox<String> activityTypeDropdown, roomDropdown;
    private JSpinner startTimeSpinner;
    private JButton submitButton;
    private JList<Student> studentList;
    private DefaultListModel<Student> studentListModel;

    public BookActivityScreen() {
        setTitle("Book Activity");
        setSize(400, 600);
        initUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Activity Name:"));
        activityNameField = new JTextField();
        add(activityNameField);

        add(new JLabel("Activity Type:"));
        activityTypeDropdown = new JComboBox<>(new String[]{"Event", "Exam", "Lesson"});
        add(activityTypeDropdown);

        add(new JLabel("Start Time:"));
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(startTimeSpinner, "yyyy-MM-dd HH:mm:ss");
        startTimeSpinner.setEditor(dateEditor);
        add(startTimeSpinner);

        add(new JLabel("Duration (in minutes, for Exams):"));
        durationField = new JTextField();
        add(durationField);

        add(new JLabel("Module ID (for Exams and Lessons):"));
        moduleField = new JTextField();
        add(moduleField);

        add(new JLabel("Room:"));
        roomDropdown = new JComboBox<>(new String[]{"101", "102", "Online"});
        add(roomDropdown);

        // Student selection setup
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fetchStudents();
        JScrollPane listScrollPane = new JScrollPane(studentList);
        add(new JLabel("Select Students:"));
        add(listScrollPane);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this::bookActivity);
        add(submitButton);
    }

    private void fetchStudents() {
        // Populate the studentListModel with student data from database
        String sql = "SELECT student_id, first_name, last_name FROM Students ORDER BY last_name, first_name";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                int id = rs.getInt("student_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                studentListModel.addElement(new Student(id, firstName + " " + lastName));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void bookActivity(ActionEvent e) {
        // Retrieve input values
        String activityName = activityNameField.getText().trim();
        String activityType = (String) activityTypeDropdown.getSelectedItem();
        Date startTime = (Date) startTimeSpinner.getValue();
        String durationStr = durationField.getText().trim();
        int duration = durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr);
        String moduleIdStr = moduleField.getText().trim();
        Integer moduleId = moduleIdStr.isEmpty() ? null : Integer.parseInt(moduleIdStr);
        String room = (String) roomDropdown.getSelectedItem();
    
        // SQL for inserting the new activity
        String sqlInsertActivity = "INSERT INTO ScheduledActivities (name, type, start_time, duration, module_id, room) VALUES (?, ?, ?, ?, ?, ?)";
    
        // SQL for linking students to the activity
        String sqlInsertParticipant = "INSERT INTO EventParticipants (activity_id, student_id) VALUES (?, ?)";
    
        try (Connection conn = getConnection()) {
            // Disable auto-commit for transaction
            conn.setAutoCommit(false);
    
            // Insert the new activity
            try (PreparedStatement pstmtActivity = conn.prepareStatement(sqlInsertActivity, Statement.RETURN_GENERATED_KEYS)) {
                pstmtActivity.setString(1, activityName);
                pstmtActivity.setString(2, activityType);
                pstmtActivity.setTimestamp(3, new java.sql.Timestamp(startTime.getTime()));
                pstmtActivity.setInt(4, duration);
                if (moduleId != null) {
                    pstmtActivity.setInt(5, moduleId);
                } else {
                    pstmtActivity.setNull(5, java.sql.Types.INTEGER);
                }
                pstmtActivity.setString(6, room.equals("Online") ? null : room); // Handle "Online" as null or specific logic
                
                int affectedRows = pstmtActivity.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating activity failed, no rows affected.");
                }
    
                // Retrieve the generated activity ID
                try (ResultSet generatedKeys = pstmtActivity.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long activityId = generatedKeys.getLong(1);
    
                        // Insert selected students into EventParticipants
                        List<Student> selectedStudents = studentList.getSelectedValuesList();
                        try (PreparedStatement pstmtParticipant = conn.prepareStatement(sqlInsertParticipant)) {
                            for (Student student : selectedStudents) {
                                pstmtParticipant.setLong(1, activityId);
                                pstmtParticipant.setInt(2, student.id);
                                pstmtParticipant.addBatch();
                            }
                            pstmtParticipant.executeBatch();
                        }
                    } else {
                        throw new SQLException("Creating activity failed, no ID obtained.");
                    }
                }
    
                // Commit transaction
                conn.commit();
                JOptionPane.showMessageDialog(this, "Activity and participants added successfully!");
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error booking the activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    

    private Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/universitymanagementsystem";
            String user = "root";
            String password = "root";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookActivityScreen().setVisible(true));
    }

    // Inner class to represent students
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
