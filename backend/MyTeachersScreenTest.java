import static org.junit.Assert.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;

public class MyTeachersScreenTest {
    private MyTeachersScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new MyTeachersScreen("1"); // Assuming '1' is a valid student ID
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testTeacherDataLoaded() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTable teachersTable = screen.teachersTable; // Assuming there is a getter for the teachers table
            DefaultTableModel model = (DefaultTableModel) teachersTable.getModel();

            // Check if the table is not empty
            assertTrue("Teachers table should have one or more rows", model.getRowCount() > 0);

        });
    }

    @Test
    public void testErrorHandling() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen = new MyTeachersScreen("-1"); // Passing an invalid student ID to simulate failure
            screen.setVisible(true);

            // Check if an error message is displayed
            assertNotNull("Error label should not be null", screen.statuslabel);
            assertFalse("Error message should be visible", screen.statuslabel.isEmpty());
        });
    }

}
