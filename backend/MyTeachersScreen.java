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

public class MyTeachersScreen extends JFrame {
    private final String studentId;

    public MyTeachersScreen(String studentId) {
        this.studentId = studentId;
        setTitle("My Teachers");
        setSize(600, 400);
        initializeUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Teacher Name");
        columnNames.add("Module");
        columnNames.add("Email");

        Vector<Vector<Object>> data = new Vector<>();

        String sql = "SELECT DISTINCT s.last_name AS TeacherName, m.name AS Module, s.email " +
                     "FROM Staff s " +
                     "JOIN Modules m ON m.staff_id = s.id " + 
                     "JOIN StudentModuleRegistrations smr ON smr.module_id = m.module_id " +
                     "WHERE smr.student_id = ? " +
                     "UNION " +
                     "SELECT DISTINCT s.last_name, '', s.email " + 
                     "FROM ScheduledActivities sa " +
                     "JOIN Staff s ON sa.staff_id = s.id " +
                     "WHERE EXISTS (SELECT 1 FROM StudentModuleRegistrations smr WHERE smr.student_id = ? AND smr.module_id = sa.module_id)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            pstmt.setString(2, studentId); 
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("TeacherName"));
                row.add(rs.getString("Module")); 
                row.add(rs.getString("email"));
                data.add(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching teacher information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable teachersTable = new JTable(model);
        add(new JScrollPane(teachersTable), BorderLayout.CENTER);
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
        SwingUtilities.invokeLater(() -> new MyTeachersScreen("studentId").setVisible(true));
    }
}
