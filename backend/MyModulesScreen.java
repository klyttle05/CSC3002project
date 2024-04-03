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

public class MyModulesScreen extends JFrame {
    private String studentId;

    public MyModulesScreen(String studentId) {
        this.studentId = studentId;
        setTitle("My Modules");
        setSize(600, 300);
        initializeUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Module ID");
        columnNames.add("Module Name");
        columnNames.add("Description");
        columnNames.add("Department");

        Vector<Vector<Object>> data = new Vector<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT m.id, m.name, m.description, d.name AS department " +
                 "FROM Module m JOIN StudentModule sm ON m.id = sm.module_id " +
                 "JOIN Department d ON m.department_id = d.id " +
                 "WHERE sm.student_id = ?")) {
            
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("description"));
                    row.add(rs.getString("department"));
                    data.add(row);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching module information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable modulesTable = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(modulesTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Connection getConnection() {
        // Implement database connection logic here
        // This is a placeholder for actual database connection
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyModulesScreen("YourStudentIdHere").setVisible(true));
    }
}
