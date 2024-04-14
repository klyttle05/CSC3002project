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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

public class BookActivityScreen extends JFrame {
    public JTextField activityNameField;
    public JComboBox<String> activityTypeDropdown, staffDropdown, roomDropdown;
    public JSpinner startTimeSpinner, endTimeSpinner;
    public JButton submitButton, checkAvailabilityButton;
    public DefaultListModel<Student> studentListModel;
    public JList<Student> studentList;
    public JRadioButton onlineRadioButton;

    public BookActivityScreen() {
        initializeUI();
        setSize(400, 700);
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

        add(new JLabel("Room:"));
        roomDropdown = new JComboBox<>();
        loadRooms();
        add(roomDropdown);

        onlineRadioButton = new JRadioButton("Online Activity");
        onlineRadioButton.addActionListener(e -> {
            if (onlineRadioButton.isSelected()) {
                roomDropdown.setEnabled(false);
                roomDropdown.setSelectedItem("Room 1");
            } else {
                roomDropdown.setEnabled(true);
            }
        });
        add(onlineRadioButton);

        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(studentList);
        add(new JLabel("Select Students:"));
        add(scrollPane);

        checkAvailabilityButton = new JButton("Check Room Availability");
        checkAvailabilityButton.addActionListener(this::checkRoomAvailability);
        add(checkAvailabilityButton);

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
    
    private void loadRooms() {
        String sql = "SELECT room_id, name FROM Rooms ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            
            while (rs.next()) {
                int roomId = rs.getInt("room_id");
                String roomName = rs.getString("name");
                roomDropdown.addItem(roomId + " - " + roomName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private Connection getConnection() {
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
        Date startTime = (Date) startTimeSpinner.getValue();
        Date endTime = (Date) endTimeSpinner.getValue();
        int staffId = Integer.parseInt(((String) staffDropdown.getSelectedItem()).split(" - ")[0]);
        boolean isOnline = onlineRadioButton.isSelected();
        int roomId = isOnline ? 1 : Integer.parseInt(((String) roomDropdown.getSelectedItem()).split(" - ")[0]);
        
        Timestamp startTimestamp = new Timestamp(startTime.getTime());
        Timestamp endTimestamp = new Timestamp(endTime.getTime());
    
        if (!isOnline && !isRoomAvailable(roomId, startTimestamp, endTimestamp)) {
            JOptionPane.showMessageDialog(this, "Selected room is not available at the specified time.", "Room Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Proceed with booking the activity
        String sql = "INSERT INTO ScheduledActivities (type, title, start_time, end_time, staff_id, room_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, activityType);
            pstmt.setString(2, activityName);
            pstmt.setTimestamp(3, startTimestamp);
            pstmt.setTimestamp(4, endTimestamp);
            pstmt.setInt(5, staffId);
            pstmt.setInt(6, roomId);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long activityId = rs.getLong(1);
                insertEventParticipants(activityId); // Call to insert event participants
            }
    
            JOptionPane.showMessageDialog(this, "Activity booked successfully.", "Booking Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error booking activity: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void insertEventParticipants(long activityId) throws SQLException {
        String sql = "INSERT INTO EventParticipants (activity_id, student_id) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Student student : studentList.getSelectedValuesList()) {
                pstmt.setLong(1, activityId);
                pstmt.setInt(2, student.id); 
                pstmt.executeUpdate();
            }
        }
        // Note: The Connection and PreparedStatement are auto-closed thanks to try-with-resources.
    }
    
    private boolean isRoomAvailable(int roomId, Timestamp desiredStartTime, Timestamp desiredEndTime) {
        String query = "SELECT COUNT(*) FROM ScheduledActivities " +
                       "WHERE room_id = ? AND NOT ((end_time <= ?) OR (start_time >= ?))";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setInt(1, roomId);
            pstmt.setTimestamp(2, desiredStartTime);
            pstmt.setTimestamp(3, desiredEndTime);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If count is 0, there are no overlapping bookings, so the room is available.
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking room availability: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // Default to false if there's an error or if the room is not available.
    }
    
    private void checkRoomAvailability(ActionEvent e) {
        // Check if the activity is online and bypass room availability check
        if (onlineRadioButton.isSelected()) {
            JOptionPane.showMessageDialog(this, "The activity is online. Room availability check is not required.", "Online Activity", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        // Extract information from UI components
        int roomId = Integer.parseInt(((String) roomDropdown.getSelectedItem()).split(" - ")[0]);
        Timestamp desiredStartTime = new Timestamp(((Date) startTimeSpinner.getValue()).getTime());
        Timestamp desiredEndTime = new Timestamp(((Date) endTimeSpinner.getValue()).getTime());
    
        // SQL query to check for overlapping bookings for the selected room
        String query = "SELECT COUNT(*) FROM ScheduledActivities " +
                       "WHERE room_id = ? AND NOT ((end_time <= ?) OR (start_time >= ?))";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            pstmt.setTimestamp(2, desiredStartTime);
            pstmt.setTimestamp(3, desiredEndTime);
    
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int overlappingBookings = rs.getInt(1);
                if (overlappingBookings == 0) {
                    // If there are no overlapping bookings, the room is available
                    JOptionPane.showMessageDialog(this, "The selected room is available for the desired time.", "Room Available", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // If there are overlapping bookings, the room is not available
                    JOptionPane.showMessageDialog(this, "The selected room is not available for the desired time.", "Room Not Available", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking room availability: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
