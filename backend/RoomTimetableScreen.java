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

public class RoomTimetableScreen extends JFrame {
    public JTextField roomIdField;
    public JButton searchButton;
    public JTable timetableTable;

    public RoomTimetableScreen() {
        setTitle("Room Timetable");
        setSize(500, 300);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        roomIdField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchTimetable);

        searchPanel.add(new JLabel("Room ID:"));
        searchPanel.add(roomIdField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        timetableTable = new JTable();
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
    }

    private void searchTimetable(ActionEvent e) {
        String roomId = roomIdField.getText();
        fetchAndDisplayTimetable(roomId);
    }

    private void fetchAndDisplayTimetable(String roomId) {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Title");
        columnNames.add("Start Time");
        columnNames.add("End Time");
        columnNames.add("Type");

        Vector<Vector<Object>> data = new Vector<>();

        String sql = "SELECT title, start_time, end_time, type FROM ScheduledActivities WHERE room_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("title"));
                    row.add(rs.getString("start_time"));
                    row.add(rs.getString("end_time"));
                    row.add(rs.getString("type")); 
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
        SwingUtilities.invokeLater(() -> new RoomTimetableScreen().setVisible(true));
    }
}
