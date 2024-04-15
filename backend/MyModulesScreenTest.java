import static org.junit.Assert.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;

public class MyModulesScreenTest {
    private MyModulesScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            // Assuming studentId 1 is used for testing
            screen = new MyModulesScreen(1);
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testModuleDataLoaded() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTable modulesTable = screen.modulesTable; // Assuming there is a getter for the modules table
            DefaultTableModel model = (DefaultTableModel) modulesTable.getModel();

            // Check if the table is not empty
            assertTrue("Modules table should have one or more rows", model.getRowCount() > 0);
        });
    }

    @Test
    public void testErrorHandling() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen = new MyModulesScreen(-1); // Passing an invalid student ID to simulate failure
            screen.setVisible(true);

            assertEquals("Error message should be visible", "Error fetching module information: ", screen.statuslabel);
        });
    }

}
