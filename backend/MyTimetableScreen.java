import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MyTimetableScreen extends JFrame {
    public JTable timetable;
    public JButton previousWeekButton, nextWeekButton;
    public JLabel weekLabel;
    public LocalDate currentWeekStart;
    public int studentId;

    public MyTimetableScreen(int studentId) {
        this.studentId = studentId;
        initializeUI();
        loadActivities(LocalDate.now().with(java.time.DayOfWeek.MONDAY));
    }

    private void initializeUI() {
        setTitle("My Timetable");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        previousWeekButton = new JButton("Previous Week");
        nextWeekButton = new JButton("Next Week");
        weekLabel = new JLabel("", SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(previousWeekButton);
        topPanel.add(weekLabel);
        topPanel.add(nextWeekButton);

        add(topPanel, BorderLayout.NORTH);

        timetable = new JTable();
        timetable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Day", "Start Time", "End Time", "Activity", "Location"}));
        JScrollPane scrollPane = new JScrollPane(timetable);
        add(scrollPane, BorderLayout.CENTER);

        previousWeekButton.addActionListener(e -> loadActivities(currentWeekStart.minusWeeks(1)));
        nextWeekButton.addActionListener(e -> loadActivities(currentWeekStart.plusWeeks(1)));
    }

    private void loadActivities(LocalDate weekStart) {
        currentWeekStart = weekStart;
        weekLabel.setText("Week of " + weekStart.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        DefaultTableModel model = (DefaultTableModel) timetable.getModel();
        model.setRowCount(0); // Clear existing data
    
        // Adjusted SQL for MySQL, using DAYNAME() to get the weekday name directly
        String sql = "SELECT DAYNAME(sa.start_time) as day, TIME(sa.start_time) as start_time, TIME(sa.end_time) as end_time, sa.title, sa.location " +
                     "FROM ScheduledActivities sa " +
                     "JOIN EventParticipants ep ON sa.activity_id = ep.activity_id " +
                     "WHERE ep.student_id = ? AND " +
                     "sa.start_time >= ? AND sa.start_time < ? " +
                     "ORDER BY sa.start_time";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setDate(2, java.sql.Date.valueOf(weekStart));
            pstmt.setDate(3, java.sql.Date.valueOf(weekStart.plusWeeks(1)));
    
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("day");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                String title = rs.getString("title");
                String location = rs.getString("location");
                model.addRow(new Object[]{day, startTime, endTime, title, location});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading activities: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyTimetableScreen(1).setVisible(true));
    }
}
