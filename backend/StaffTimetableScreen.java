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

public class StaffTimetableScreen extends JFrame {
    private JTextField staffIdField;
    private JButton searchButton;
    private JTable timetableTable;

    public StaffTimetableScreen() {
        setTitle("Staff Timetable");
        setSize(500, 300);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        staffIdField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchTimetable);
        
        searchPanel.add(new JLabel("Staff ID:"));
        searchPanel.add(staffIdField);
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.NORTH);

        timetableTable = new JTable();
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
    }

    private void searchTimetable(ActionEvent e) {
        String staffId = staffIdField.getText();
        fetchAndDisplayTimetable(staffId);
    }

    private void fetchAndDisplayTimetable(String staffId) {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Title");
        columnNames.add("Start Time");
        columnNames.add("End Time");
        columnNames.add("Location");

        Vector<Vector<Object>> data = new Vector<>();

        String sql = "SELECT sa.title, sa.start_time, sa.end_time, r.name AS location FROM ScheduledActivities sa " +
                     "LEFT JOIN Rooms r ON sa.room_id = r.room_id " +
                     "WHERE sa.staff_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("title"));
                    row.add(rs.getString("start_time"));
                    row.add(rs.getString("end_time"));
                    row.add(rs.getString("location")); 
                    data.add(row);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching timetable: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        timetableTable.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffTimetableScreen().setVisible(true));
    }
}
