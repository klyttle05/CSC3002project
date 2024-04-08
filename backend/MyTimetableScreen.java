import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;


public class MyTimetableScreen extends JFrame {
    private final String studentId;
    private LocalDate currentDate;
    private final JTable timetableTable;

    public MyTimetableScreen(String studentId) {
        this.studentId = studentId;
        currentDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        setTitle("My Timetable");
        setSize(800, 600);
        timetableTable = new JTable();
        initializeUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel navigationPanel = new JPanel();
        JButton prevWeekButton = new JButton("Previous Week");
        JButton nextWeekButton = new JButton("Next Week");

        prevWeekButton.addActionListener(e -> updateTimetable(-1));
        nextWeekButton.addActionListener(e -> updateTimetable(1));

        navigationPanel.add(prevWeekButton);
        navigationPanel.add(nextWeekButton);

        add(navigationPanel, BorderLayout.NORTH);
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        displayWeeksTimetable();
    }

    private void updateTimetable(int weekDelta) {
        currentDate = currentDate.plusWeeks(weekDelta);
        displayWeeksTimetable();
    }

    private void displayWeeksTimetable() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Date & Time");
        columnNames.add("Activity");
        columnNames.add("Module");
        columnNames.add("Location");
    
        Vector<Vector<Object>> data = new Vector<>();
        
        // Define start and end of the current viewing week
        LocalDateTime weekStart = LocalDateTime.of(currentDate, LocalTime.of(8, 0));
        LocalDateTime weekEnd = LocalDateTime.of(currentDate.plusDays(4), LocalTime.of(18, 0)); // From Monday 8am to Friday 6pm
    
        String sql = "SELECT sa.start_time, sa.end_time, m.name AS moduleName, IFNULL(r.name, 'Online') AS location, sa.activity_name " +
                     "FROM ScheduledActivities sa " +
                     "JOIN Modules m ON sa.module_id = m.module_id " +
                     "LEFT JOIN Rooms r ON sa.room_id = r.room_id " +
                     "JOIN StudentModuleRegistrations smr ON m.module_id = smr.module_id " +
                     "WHERE smr.student_id = ? AND sa.start_time BETWEEN ? AND ? " +
                     "ORDER BY sa.start_time";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            pstmt.setTimestamp(2, Timestamp.valueOf(weekStart));
            pstmt.setTimestamp(3, Timestamp.valueOf(weekEnd));
    
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<Object> vector = new Vector<>();
                vector.add(rs.getTimestamp("start_time").toString() + " - " + rs.getTimestamp("end_time").toString());
                vector.add(rs.getString("activity_name"));
                vector.add(rs.getString("moduleName"));
                vector.add(rs.getString("location"));
                data.add(vector);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching timetable: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable.setModel(model);
    }
    

    private Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/universitymanagementsystem";
            String user = "root";
            String password = "root";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyTimetableScreen("studentId").setVisible(true));
    }
}
