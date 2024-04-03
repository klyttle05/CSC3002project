import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void searchActivities(ActionEvent e) {
        // Placeholder for searching activities by date
        // You should replace this with actual SQL query to fetch activities
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Type");
        columnNames.add("Title");
        columnNames.add("Start Time");
        columnNames.add("Module/Room");
        
        Vector<Vector<Object>> data = new Vector<>();
        
        // Example data fetching
        // Assume getConnection() provides a valid database connection
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Activity WHERE date = ?");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> vector = new Vector<>();
                vector.add(rs.getString("id"));
                vector.add(rs.getString("type"));
                vector.add(rs.getString("title"));
                vector.add(rs.getString("start_time"));
                vector.add(rs.getString("module_room"));
                data.add(vector);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        activitiesTable.setModel(model);
    }

    private void deleteSelectedActivity(ActionEvent e) {
        // Placeholder for deleting the selected activity
        // Get the selected row's activity ID and execute a delete SQL statement
    }

    private void deleteAllActivities(ActionEvent e) {
        // Placeholder for deleting all activities for a given module or room
        // Use the text from deleteModuleField or deleteRoomField to construct SQL statement
    }

    // Placeholder for the getConnection() method to get a database connection
    private Connection getConnection() {
        // Implement database connection logic here
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeleteActivityScreen().setVisible(true));
    }
}

