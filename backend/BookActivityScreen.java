import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
    private JTextField activityNameField;
    private JComboBox<String> activityTypeDropdown, staffDropdown;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private JButton submitButton;
    private DefaultListModel<Student> studentListModel;
    private JList<Student> studentList;

    public BookActivityScreen() {
        initializeUI();
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Activity Name:"));
        activityNameField = new JTextField();
        add(activityNameField);

        add(new JLabel("Activity Type:"));
        activityTypeDropdown = new JComboBox<>(new String[]{"Event", "Exam", "Lesson"});
        add(activityTypeDropdown);

        add(new JLabel("Start Time:"));
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "yyyy-MM-dd HH:mm:ss"));
        add(startTimeSpinner);

        add(new JLabel("End Time:"));
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "yyyy-MM-dd HH:mm:ss"));
        add(endTimeSpinner);

        add(new JLabel("Staff ID:"));
        staffDropdown = new JComboBox<>();
        loadStaffMembers();
        add(staffDropdown);

        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(studentList);
        add(new JLabel("Select Students:"));
        add(scrollPane);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this::bookActivity);
        add(submitButton);

        loadStudents();
    }

    private void loadStudents() {
        String sql = "SELECT id, first_name, last_name FROM Students ORDER BY last_name, first_name";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                studentListModel.addElement(new Student(id, firstName + " " + lastName));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadStaffMembers() {
        String sql = "SELECT id, first_name, last_name FROM Staff ORDER BY last_name, first_name";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                staffDropdown.addItem(id + " - " + firstName + " " + lastName);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch staff members: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private Connection getConnection() {
        // Example connection method, adjust with your actual database credentials
        try {
            String url = "jdbc:mysql://localhost:3306/universitymanagementsystem";
            String user = "root";
            String password = "root";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
    

    private void bookActivity(ActionEvent e) {
        String activityName = activityNameField.getText().trim();
        String activityType = (String) activityTypeDropdown.getSelectedItem();
        Timestamp startTime = new Timestamp(((Date) startTimeSpinner.getValue()).getTime());
        Timestamp endTime = new Timestamp(((Date) endTimeSpinner.getValue()).getTime());
        String staffIdStr = ((String) staffDropdown.getSelectedItem()).split(" - ")[0];
        int staffId = Integer.parseInt(staffIdStr);
    
        // SQL to insert the new activity
        String sqlInsertActivity = "INSERT INTO ScheduledActivities (name, type, start_time, end_time, staff_id) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection()) {
            // Insert the activity
            try (PreparedStatement pstmtActivity = conn.prepareStatement(sqlInsertActivity, Statement.RETURN_GENERATED_KEYS)) {
                pstmtActivity.setString(1, activityName);
                pstmtActivity.setString(2, activityType);
                pstmtActivity.setTimestamp(3, startTime);
                pstmtActivity.setTimestamp(4, endTime);
                pstmtActivity.setInt(5, staffId);
    
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
                        if (!selectedStudents.isEmpty()) {
                            insertEventParticipants(conn, activityId, selectedStudents);
                        }
    
                        JOptionPane.showMessageDialog(this, "Activity booked successfully.");
                    } else {
                        throw new SQLException("Creating activity failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking the activity: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void insertEventParticipants(Connection conn, long activityId, List<Student> selectedStudents) throws SQLException {
        String sqlInsertParticipant = "INSERT INTO EventParticipants (activity_id, student_id) VALUES (?, ?)";
        try (PreparedStatement pstmtParticipant = conn.prepareStatement(sqlInsertParticipant)) {
            for (Student student : selectedStudents) {
                pstmtParticipant.setLong(1, activityId);
                pstmtParticipant.setInt(2, student.id);
                pstmtParticipant.executeUpdate();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookActivityScreen().setVisible(true));
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
