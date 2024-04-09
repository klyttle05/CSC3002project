import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
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
        int duration = !durationField.getText().isEmpty() ? Integer.parseInt(durationField.getText()) : 0; // For exams
        String moduleId = noModuleRadio.isSelected() ? null : moduleField.getText();
        String room = onlineRadio.isSelected() ? null : (String) roomDropdown.getSelectedItem();
    
        String sql = "INSERT INTO ScheduledActivities (title, start_time, end_time, type, room_id, module_id) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, activityName);
            pstmt.setTimestamp(2, new java.sql.Timestamp(startTime.getTime()));
            // For simplicity, assuming all activities last 1 hour if duration not specified
            pstmt.setTimestamp(3, new java.sql.Timestamp(startTime.getTime() + (duration > 0 ? duration * 60000 : 3600000)));
            pstmt.setString(4, activityType);
            
            // Handling "Online" option or specific room
            if (room == null) {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                // Assuming roomDropdown contains room_id as value. If it contains room names, a lookup query is required.
                pstmt.setInt(5, Integer.parseInt(room));
            }
    
            // Handling "No Module" option
            if (moduleId == null) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(6, Integer.parseInt(moduleId));
            }
    
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Activity booked successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book the activity.", "Error", JOptionPane.ERROR_MESSAGE);
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
}

