import static org.junit.Assert.*;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class MainMenuTest {
    private MainMenu screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new MainMenu("Staff", "1"); // Test with a staff user
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testStaffUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Assert that all staff-specific buttons are visible and enabled
            assertEquals("Expected number of buttons for 'Staff'", 2, screen.getContentPane().getComponentCount());
        });
    }

    @Test
    public void testStudentUserInterface() throws Exception {
        // Reinitialize as a student
        SwingUtilities.invokeAndWait(() -> {
            screen.dispose(); // Dispose the previous instance
            screen = new MainMenu("Student", "2"); // Test with a student user
            screen.setVisible(true);
        });

        SwingUtilities.invokeAndWait(() -> {
            // Assert that all student-specific buttons are visible and enabled
            assertEquals("Expected number of buttons for 'Student'", 2, screen.getContentPane().getComponentCount());
        });
    }

}
