import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ClashDetectionScreen extends JFrame {
    private int userId;
    private boolean isStaff;
    public JTable studentClashesTable, staffClashesTable;

    public ClashDetectionScreen(int userId, boolean isStaff) {
        this.userId = userId;
        this.isStaff = isStaff;
        initializeUI();
        loadStudentClashes();
        if (isStaff) {
            loadStaffClashes();
        }
    }

    private void initializeUI() {
        setTitle("Scheduling Clashes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1)); // Layout to accommodate two tables

        studentClashesTable = new JTable(new DefaultTableModel(new Object[]{"Student ID", "Activity 1 ID", "Activity 2 ID", "Overlap Period"}, 0));
        staffClashesTable = new JTable(new DefaultTableModel(new Object[]{"Staff ID", "Activity 1 ID", "Activity 2 ID", "Overlap Period"}, 0));
        
        add(new JScrollPane(studentClashesTable));
        add(new JScrollPane(staffClashesTable));

        // Only display staff clashes table to staff members
        staffClashesTable.setVisible(isStaff); 
    }

    public void loadStudentClashes() {
        String studentClashesQuery;
        if (isStaff) {
            // Staff member query retrieves all student clashes
            studentClashesQuery = """
                SELECT ep.student_id, sa1.activity_id AS activity1_id, sa2.activity_id AS activity2_id, 
                       CONCAT(sa1.start_time, ' to ', sa2.end_time) AS overlap_period
                FROM EventParticipants ep
                JOIN ScheduledActivities sa1 ON ep.activity_id = sa1.activity_id
                JOIN ScheduledActivities sa2 ON sa1.activity_id != sa2.activity_id
                WHERE sa1.end_time > sa2.start_time AND sa1.start_time < sa2.end_time
                GROUP BY ep.student_id, sa1.activity_id, sa2.activity_id
                ORDER BY ep.student_id, overlap_period;
                """;
        } else {
            // Student user query retrieves only their clashes
            studentClashesQuery = """
                SELECT ep.student_id, sa1.activity_id AS activity1_id, sa2.activity_id AS activity2_id, 
                       CONCAT(sa1.start_time, ' to ', sa2.end_time) AS overlap_period
                FROM EventParticipants ep
                JOIN ScheduledActivities sa1 ON ep.activity_id = sa1.activity_id
                JOIN ScheduledActivities sa2 ON sa1.activity_id != sa2.activity_id
                WHERE sa1.end_time > sa2.start_time AND sa1.start_time < sa2.end_time
                AND ep.student_id = ?
                GROUP BY ep.student_id, sa1.activity_id, sa2.activity_id
                ORDER BY ep.student_id, overlap_period;
                """;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(studentClashesQuery)) {
             
            if (!isStaff) {
                pstmt.setInt(1, userId); // Set the student's userID for the query
            }

            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) studentClashesTable.getModel();
            model.setRowCount(0); // Clear previous data

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("activity1_id"),
                    rs.getString("activity2_id"),
                    rs.getString("overlap_period")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadStaffClashes() {
        // Similar to loadStudentClashes but for staff
        // This requires a different SQL query that checks for staff member's activity overlaps
        String query = getStaffClashesQuery(); // This query now needs to target staff-specific clashes
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) staffClashesTable.getModel();
            model.setRowCount(0); // Clear previous data

            while (rs.next()) {
                int staffId = rs.getInt("staff_id");
                String activity1Id = rs.getString("activity1_id");
                String activity2Id = rs.getString("activity2_id");
                String overlapPeriod = rs.getString("overlap_period");

                model.addRow(new Object[]{staffId, activity1Id, activity2Id, overlapPeriod});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getStaffClashesQuery() {
        return "SELECT DISTINCT sa1.staff_id AS staff_id, sa1.activity_id AS activity1_id, sa2.activity_id AS activity2_id, " +
               "'Overlap Detected' AS overlap_period " +
               "FROM ScheduledActivities sa1 " +
               "JOIN ScheduledActivities sa2 ON sa1.end_time > sa2.start_time AND sa1.start_time < sa2.end_time AND sa1.activity_id != sa2.activity_id " +
               "WHERE sa1.staff_id = sa2.staff_id AND sa1.staff_id IS NOT NULL " +
               "ORDER BY sa1.staff_id";
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
    }


    public static void main(String[] args) {
        // Example usage
        new ClashDetectionScreen(1, true); // Assuming '1' is a staff ID in this case
    }
}
