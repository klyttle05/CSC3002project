import static org.junit.Assert.*;

import java.time.LocalDate;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;

public class MyTimetableScreenTest {
    private MyTimetableScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new MyTimetableScreen(1); // Assuming '1' is a valid student ID
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testInitialLoadActivities() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DefaultTableModel model = (DefaultTableModel) screen.timetable.getModel();
            assertNotNull("Table model should not be null", model);
            assertTrue("Table should have rows after initial load", model.getRowCount() > 0);
        });
    }

    @Test
    public void testNavigationToPreviousWeek() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LocalDate initialDate = screen.currentWeekStart;
            screen.previousWeekButton.doClick();
            assertNotEquals("Week should have changed to the previous week", initialDate, screen.currentWeekStart);
            assertTrue("Previous week should be before the initial week", screen.currentWeekStart.isBefore(initialDate));
        });
    }

    @Test
    public void testNavigationToNextWeek() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LocalDate initialDate = screen.currentWeekStart;
            screen.nextWeekButton.doClick();
            assertNotEquals("Week should have changed to the next week", initialDate, screen.currentWeekStart);
            assertTrue("Next week should be after the initial week", screen.currentWeekStart.isAfter(initialDate));
        });
    }
}
