import static org.junit.Assert.*;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;

public class ModuleTimetableScreenTest {
    private ModuleTimetableScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new ModuleTimetableScreen();
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testSearchTimetable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.moduleIdField.setText("2");
            screen.searchButton.doClick();  // Simulate the search button click

            DefaultTableModel model = (DefaultTableModel) screen.timetableTable.getModel();
            assertNotNull("Table model should not be null after search", model);
            assertTrue("Table should have rows after search", model.getRowCount() > 0);
        });
    }

    @Test
    public void testInvalidModuleId() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.moduleIdField.setText("invalid");  // Provide an invalid module ID
            screen.searchButton.doClick();  // Simulate the search button click

            DefaultTableModel model = (DefaultTableModel) screen.timetableTable.getModel();
            assertEquals("Table should have no rows for invalid module ID", 0, model.getRowCount());
        });
    }
}
