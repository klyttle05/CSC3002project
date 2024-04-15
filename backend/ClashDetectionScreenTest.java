import static org.junit.Assert.*;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;

public class ClashDetectionScreenTest {
    private ClashDetectionScreen screen;

    @Before
    public void setUp() throws Exception {
        // Set up the environment by initializing the screen
        SwingUtilities.invokeAndWait(() -> {
            screen = new ClashDetectionScreen(1, true); // Assuming 1 is a staff ID for testing purposes
        });
    }

    @Test
    public void testLoadStudentClashes() throws Exception {
        // Invoke the loadStudentClashes method and check the table contents
        SwingUtilities.invokeAndWait(() -> {
            screen.loadStudentClashes();  // This populates the studentClashesTable
            DefaultTableModel model = (DefaultTableModel) screen.studentClashesTable.getModel();
            assertNotNull("Model should not be null", model);
            assertTrue("Table should have more than zero rows for clashes", model.getRowCount() > 0);
        });
    }

    @Test
    public void testLoadStaffClashes() throws Exception {
        // Invoke the loadStaffClashes method and check the table contents if the user is staff
        SwingUtilities.invokeAndWait(() -> {
            screen.loadStaffClashes();  // This populates the staffClashesTable
            DefaultTableModel model = (DefaultTableModel) screen.staffClashesTable.getModel();
            assertNotNull("Model should not be null", model);
            assertTrue("Table should have more than zero rows for clashes", model.getRowCount() > 0);
        });
    }
}
