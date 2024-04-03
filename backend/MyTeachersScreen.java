import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MyTeachersScreen extends JFrame {
    private String studentId;

    public MyTeachersScreen(String studentId) {
        this.studentId = studentId;
        setTitle("My Teachers");
        setSize(600, 300);
        initializeUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Staff ID");
        columnNames.add("Name");
        columnNames.add("Email");
        columnNames.add("Department");

        Vector<Vector<Object>> data = new Vector<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT DISTINCT s.id, s.first_name || ' ' || s.last_name AS name, s.email, d.name AS department " +
                 "FROM Staff s JOIN ModuleStaff ms ON s.id = ms.staff_id " +
                 "JOIN Module m ON ms.module_id = m.id " +
                 "JOIN StudentModule sm ON m.id = sm.module_id " +
                 "LEFT JOIN Event e ON s.id = e.staff_id " +
                 "JOIN StudentEvent se ON e.id = se.event_id AND se.student_id = ? " +
                 "JOIN Department d ON s.department_id = d.id " +
                 "WHERE sm.student_id = ?")) {
            
            pstmt.setString(1, studentId);
            pstmt.setString(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("email"));
                    row.add(rs.getString("department"));
                    data.add(row);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching teacher information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable teachersTable = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(teachersTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Connection getConnection() {
        // Implement database connection logic here
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyTeachersScreen("YourStudentIdHere").setVisible(true));
    }
}
