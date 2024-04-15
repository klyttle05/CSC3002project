import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;


public class DeleteActivityScreen extends JFrame {
    private JSpinner dateSpinner;
    public JTable activitiesTable;
    public JButton deleteButton, deleteAllButton;
    public JTextField deleteModuleField, deleteRoomField;
    public String statuslabel;

    
    public DeleteActivityScreen() {
        setTitle("Delete Scheduled Activity");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel topPanel = new JPanel();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        topPanel.add(new JLabel("Select Date:"));
        topPanel.add(dateSpinner);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchActivities);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        activitiesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel deletePanel = new JPanel();
        deleteButton = new JButton("Delete Selected Activity");
        deleteButton.addActionListener(this::deleteSelectedActivity);
        deleteAllButton = new JButton("Delete All Activities for");
        deleteModuleField = new JTextField(10);
        deleteRoomField = new JTextField(10);
        deletePanel.add(deleteButton);
        deletePanel.add(deleteAllButton);
        deletePanel.add(new JLabel("Module ID:"));
        deletePanel.add(deleteModuleField);
        deletePanel.add(new JLabel("OR Room ID:"));
        deletePanel.add(deleteRoomField);
        deleteAllButton.addActionListener(this::deleteAllActivities);
        add(deletePanel, BorderLayout.SOUTH);
    }

    private void searchActivities(ActionEvent e) {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Type");
        columnNames.add("Title");
        columnNames.add("Start Time");
        columnNames.add("End Time");
        columnNames.add("Location/Online");
    
        Vector<Vector<Object>> data = new Vector<>();
    
        // Format the date to match SQL DATE format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(((SpinnerDateModel)dateSpinner.getModel()).getDate());
    
        String sql = "SELECT sa.activity_id, sa.type, sa.title, sa.start_time, sa.end_time, IFNULL(r.name, 'Online') AS location " +
                     "FROM ScheduledActivities sa " +
                     "LEFT JOIN Rooms r ON sa.room_id = r.room_id " +
                     "WHERE DATE(sa.start_time) = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, formattedDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vector<Object> vector = new Vector<>();
                vector.add(rs.getInt("activity_id"));
                vector.add(rs.getString("type"));
                vector.add(rs.getString("title"));
                vector.add(rs.getTimestamp("start_time").toString());
                vector.add(rs.getTimestamp("end_time").toString());
                vector.add(rs.getString("location"));
                data.add(vector);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching activities: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make table cells non-editable
                return false;
            }
        };
        activitiesTable.setModel(model);
    }
    

    private void deleteSelectedActivity(ActionEvent e) {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an activity to delete.", "No Activity Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int activityId = (Integer) activitiesTable.getModel().getValueAt(selectedRow, 0);
        deleteActivity(activityId);
    }

    private void deleteActivity(int activityId) {
        String sqlParticipants = "DELETE FROM EventParticipants WHERE activity_id = ?";
        String sqlActivities = "DELETE FROM ScheduledActivities WHERE activity_id = ?";
        Connection conn = null;
        
        try {
            conn = getConnection();
             PreparedStatement pstmtParticipants = conn.prepareStatement(sqlParticipants);
             PreparedStatement pstmtActivities = conn.prepareStatement(sqlActivities);

            conn.setAutoCommit(false);

            pstmtParticipants.setInt(1, activityId);
            pstmtParticipants.executeUpdate();

            pstmtActivities.setInt(1, activityId);
            pstmtActivities.executeUpdate();

            conn.commit();
            statuslabel = "Activity  deleted successfully.";
            JOptionPane.showMessageDialog(this, "Activity  deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            searchActivities(null);  // Refresh the table after deletion

        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException exRollback) {
                JOptionPane.showMessageDialog(this, "Rollback failed: " + exRollback.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(this, "Error deleting the activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void deleteAllActivities(ActionEvent e) {
        String moduleId = deleteModuleField.getText().trim();
        String roomId = deleteRoomField.getText().trim();
    
        if (moduleId.isEmpty() && roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Module ID or Room ID.", "No ID Provided", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String sqlDeleteParticipants = "";
        String sqlDeleteActivities = "";
    
        if (!moduleId.isEmpty()) {
            sqlDeleteParticipants = "DELETE FROM EventParticipants WHERE activity_id IN (SELECT activity_id FROM ScheduledActivities WHERE module_id = ?)";
            sqlDeleteActivities = "DELETE FROM ScheduledActivities WHERE module_id = ?";
        } else {
            sqlDeleteParticipants = "DELETE FROM EventParticipants WHERE activity_id IN (SELECT activity_id FROM ScheduledActivities WHERE room_id = ?)";
            sqlDeleteActivities = "DELETE FROM ScheduledActivities WHERE room_id = ?";
        }
    
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
    
            // Delete from EventParticipants first
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteParticipants)) {
                pstmt.setInt(1, Integer.parseInt(moduleId.isEmpty() ? roomId : moduleId));
                pstmt.executeUpdate();
            }
    
            // Then delete the activities
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteActivities)) {
                pstmt.setInt(1, Integer.parseInt(moduleId.isEmpty() ? roomId : moduleId));
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    JOptionPane.showMessageDialog(this, "No activities found for deletion.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
    
            conn.commit(); // Commit transaction
            statuslabel = "Activity deleted successfully.";
            JOptionPane.showMessageDialog(this, "Activity deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            searchActivities(null); // Refresh the table after deletion
    
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Module ID or Room ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException exRollback) {
                JOptionPane.showMessageDialog(this, "Rollback failed: " + exRollback.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(this, "Error deleting activities: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error setting auto-commit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        SwingUtilities.invokeLater(() -> new DeleteActivityScreen().setVisible(true));
    }
}
