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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class DeleteActivityScreen extends JFrame {
    private JSpinner dateSpinner;
    private JTable activitiesTable;
    private JButton deleteButton, deleteAllButton;
    private JTextField deleteModuleField, deleteRoomField;

    public DeleteActivityScreen() {
        setTitle("Delete Scheduled Activity");
        setSize(600, 400);
        setLayout(new BorderLayout());
        initializeComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
    }

    private void initializeComponents() {
        // Top panel with date spinner and search button
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

        // Activities table
        activitiesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with delete options
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
        columnNames.add("Location/Online"); // Assuming you want to distinguish between online and location-based activities
    
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
    
        // Assuming the first column contains the activity ID
        int activityId = (Integer) activitiesTable.getModel().getValueAt(selectedRow, 0);
    
        String sql = "DELETE FROM ScheduledActivities WHERE activity_id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, activityId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Activity deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Optionally, refresh the activities table to reflect the deletion
                searchActivities(null);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the selected activity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting the activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    

    private void deleteAllActivities(ActionEvent e) {
        // Retrieve the module ID and room ID from the input fields
        String moduleId = deleteModuleField.getText().trim();
        String room = deleteRoomField.getText().trim();
    
        // Initialize SQL statement and parameters for deletion
        String sql = "";
        int parameterValue = -1;
    
        if (!moduleId.isEmpty()) {
            sql = "DELETE FROM ScheduledActivities WHERE module_id = ?";
            try {
                parameterValue = Integer.parseInt(moduleId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Module ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (!room.isEmpty()) {
            sql = "DELETE FROM ScheduledActivities WHERE room_id = ?";
            try {
                parameterValue = Integer.parseInt(room);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Room ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a Module ID or Room ID.", "No ID Provided", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Execute the delete operation
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, parameterValue);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "All related activities deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Optionally, refresh the activities table to reflect the deletion
                searchActivities(null);
            } else {
                JOptionPane.showMessageDialog(this, "No activities found to delete.", "No Activities Deleted", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting activities: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    

    private Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/universitymanagementsystem?useSSL=false";
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
