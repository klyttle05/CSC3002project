import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class StudentTimetableScreen extends JFrame {
    public JTextField studentIdField;
    public JButton searchButton;
    public JTable timetableTable;

    public StudentTimetableScreen() {
        setTitle("Student Timetable");
        setSize(600, 400);
        initializeUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        studentIdField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchTimetable);

        searchPanel.add(new JLabel("Student ID:"));
        searchPanel.add(studentIdField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        timetableTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void searchTimetable(ActionEvent e) {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Title");
        columnNames.add("Type");
        columnNames.add("Start Time");
        columnNames.add("End Time");
        columnNames.add("Location/Module");

        Vector<Vector<Object>> data = new Vector<>();
        
        String sql = "SELECT sa.activity_id, sa.title, sa.type, sa.start_time, sa.end_time, " +
                     "COALESCE(r.name, 'Online') AS location, m.name AS moduleName " +
                     "FROM ScheduledActivities sa " +
                     "LEFT JOIN Rooms r ON sa.room_id = r.room_id " +
                     "LEFT JOIN Modules m ON sa.module_id = m.module_id " +
                     "JOIN StudentModuleRegistrations smr ON m.module_id = smr.module_id " +
                     "WHERE smr.student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, Integer.parseInt(studentIdField.getText()));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("activity_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("type"));
                row.add(rs.getTimestamp("start_time").toString());
                row.add(rs.getTimestamp("end_time").toString());
                String location = rs.getString("location") != null ? rs.getString("location") : "Online";
                row.add(location + "/" + rs.getString("moduleName"));
                data.add(row);
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
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentTimetableScreen().setVisible(true));
    }
}
