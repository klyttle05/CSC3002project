import static org.junit.Assert.*;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;

public class StudentTimetableScreenTest {
    private StudentTimetableScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new StudentTimetableScreen();
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testSearchTimetable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.studentIdField.setText("1");  // Assuming '1' is a valid student ID
            screen.searchButton.doClick();  // Simulate the search button click

            DefaultTableModel model = (DefaultTableModel) screen.timetableTable.getModel();
            assertNotNull("Table model should not be null after search", model);
            assertTrue("Table should have rows after search", model.getRowCount() > 0);
        });
    }

    @Test
    public void testInvalidStudentId() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.studentIdField.setText("invalid");  // Provide an invalid student ID
            screen.searchButton.doClick();  // Simulate the search button click

            DefaultTableModel model = (DefaultTableModel) screen.timetableTable.getModel();
            assertEquals("Table should have no rows for invalid student ID", 0, model.getRowCount());
        });
    }
}
