import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private final int studentId; // Assuming studentId should be an integer.

    public MyModulesScreen(int studentId) {
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

        Vector<Vector<Object>> data = new Vector<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT m.id, m.name, m.description " +
                 "FROM Modules m JOIN StudentModuleRegistrations smr ON m.module_id = smr.module_id " +
                 "WHERE smr.student_id = ?")) {
            
            pstmt.setInt(1, studentId); // Corrected for integer studentId
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("description"));
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
        try {
            String url = "jdbc:mysql://localhost:3306/universitymanagementsystem"; // Corrected database name
            String user = "root";
            String password = "root";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyModulesScreen(1).setVisible(true)); // Example with a hardcoded studentId
    }
}
