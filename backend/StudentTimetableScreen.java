import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
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
    private JTextField studentIdField;
    private JButton searchButton;
    private JTable timetableTable;

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
        // Placeholder: fetch timetable data for the given student ID from your database
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Title");
        columnNames.add("Type");
        columnNames.add("Start Time");
        columnNames.add("End Time");
        columnNames.add("Location/Module");

        Vector<Vector<Object>> data = new Vector<>();
        
        // Replace with actual database connection details and query logic
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT * FROM ScheduledActivities WHERE student_id = ?")) {
             
            pstmt.setString(1, studentIdField.getText());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("type"));
                row.add(rs.getTimestamp("start_time").toString());
                row.add(rs.getTimestamp("end_time").toString());
                row.add(rs.getString("location_module"));
                data.add(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching timetable: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        timetableTable.setModel(model);
    }

    private Connection getConnection() {
        // Implement database connection logic here
        // This is a placeholder for actual database connection
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentTimetableScreen().setVisible(true));
    }
}

