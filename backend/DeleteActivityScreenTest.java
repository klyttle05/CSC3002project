import static org.junit.Assert.*;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class DeleteActivityScreenTest {
    private DeleteActivityScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new DeleteActivityScreen();
            screen.setVisible(true); // Necessary to ensure components are properly initialized
        });
    }

    @Test
    public void testDeleteAllActivitiesByModuleId() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Set a module ID that is known to have associated activities
            screen.deleteModuleField.setText("1");
            screen.deleteAllButton.doClick(); // Simulate button click to delete all activities for a module

            // Check for a success message
            String expectedMessage = "Activity deleted successfully.";
            assertEquals("All activities for the given module should be deleted.", expectedMessage, screen.statuslabel);
        });
    }

    @Test
    public void testDeleteAllActivitiesByRoomId() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Set a room ID that is known to have associated activities
            screen.deleteRoomField.setText("101");
            screen.deleteAllButton.doClick(); // Simulate button click to delete all activities for a room

            // Check for a success message
            String expectedMessage = "Activity deleted successfully.";
            assertEquals("All activities for the given room should be deleted.", expectedMessage, screen.statuslabel);
        });
    }
}
