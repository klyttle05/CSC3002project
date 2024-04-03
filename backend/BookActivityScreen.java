import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

public class BookActivityScreen extends JFrame {
    private JTextField activityNameField, durationField, moduleField;
    private JComboBox<String> activityTypeDropdown, roomDropdown;
    private JSpinner startTimeSpinner;
    private JButton submitButton;
    private JRadioButton noModuleRadio, onlineRadio;

    public BookActivityScreen() {
        setTitle("Book Activity");
        setSize(400, 400);
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
        roomDropdown = new JComboBox<>(new String[]{"101", "102", "Online"}); // Example room numbers
        add(roomDropdown);

        noModuleRadio = new JRadioButton("No Module", true);
        onlineRadio = new JRadioButton("Online", false);
        ButtonGroup group = new ButtonGroup();
        group.add(noModuleRadio);
        group.add(onlineRadio);
        add(noModuleRadio);
        add(onlineRadio);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this::bookActivity);
        add(submitButton);
    }

    private void bookActivity(ActionEvent e) {
        String activityName = activityNameField.getText();
        String activityType = (String) activityTypeDropdown.getSelectedItem();
        Date startTime = (Date) startTimeSpinner.getValue();
        String duration = durationField.getText(); // Only used for Exams
        String moduleId = moduleField.getText(); // Used for Exams and Lessons, if applicable
        String room = (String) roomDropdown.getSelectedItem();
        
        // Assuming getConnection() is a method that returns a Connection object to your database
        try (Connection conn = getConnection()) {
            String sql = "";
            switch (activityType) {
                case "Event":
                    sql = "INSERT INTO Event (title, start_time, end_time, location_id) VALUES (?, ?, ?, ?)";
                    break;
                case "Exam":
                    sql = "INSERT INTO Exam (title, duration_minutes, start_time, end_time, location_id, module_id) VALUES (?, ?, ?, ?, ?, ?)";
                    break;
                case "Lesson":
                    sql = "INSERT INTO Lesson (title, start_time, end_time, location_id, module_id, instructor_id) VALUES (?, ?, ?, ?, ?, ?)";
                    // Instructor ID needs to be handled, example placeholder added
                    break;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, activityName);
                pstmt.setTimestamp(2, new java.sql.Timestamp(startTime.getTime()));
                // End time is not specified by the user; placeholder logic needed
                pstmt.setTimestamp(3, new java.sql.Timestamp(startTime.getTime() + 3600000)); // +1 hour as placeholder
                
                if ("Exam".equals(activityType)) {
                    pstmt.setInt(4, Integer.parseInt(duration));
                    pstmt.setString(5, room); // Location ID needs matching with actual IDs
                    pstmt.setString(6, moduleId);
                } else if ("Lesson".equals(activityType)) {
                    pstmt.setString(4, room); // Location ID
                    pstmt.setString(5, moduleId);
                    pstmt.setInt(6, 1); // Placeholder instructor ID
                } else {
                    pstmt.setString(4, room); // For Event, assuming 'Online' can be handled
                }
                
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Activity booked successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to book the activity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error booking the activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Placeholder for the getConnection() method
    private Connection getConnection() {
        // Implement your database connection here
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookActivityScreen().setVisible(true));
    }
}

