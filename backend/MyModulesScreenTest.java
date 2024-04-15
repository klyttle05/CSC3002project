import static org.junit.Assert.*;

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
            DefaultTableModel model = (DefaultTableModel) screen.modulesTableJT.getModel();

            // Check if the table is not empty
            assertTrue("Modules table should have one or more rows", model.getRowCount() > 0);
        });
    }
}
