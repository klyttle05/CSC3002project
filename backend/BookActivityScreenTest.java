import static org.junit.Assert.*;

import java.sql.Timestamp;

import javax.swing.JOptionPane;

import org.junit.Before;
import org.junit.Test;

public class BookActivityScreenTest {
    private BookActivityScreen screen;

    @Before
    public void setUp() {
        // Initialize the screen
        screen = new BookActivityScreen();
        // Set predefined selections for consistent testing
        screen.activityTypeDropdown.setSelectedItem("Lesson");
        screen.activityNameField.setText("Math Review");
        screen.startTimeSpinner.setValue(new Timestamp(System.currentTimeMillis()));
        screen.endTimeSpinner.setValue(new Timestamp(System.currentTimeMillis() + 3600000)); // Plus one hour
        screen.staffDropdown.setSelectedItem("1 - John Doe");
        screen.roomDropdown.setSelectedItem("101 - Room A");
        screen.onlineRadioButton.setSelected(false);
    }

    @Test
    public void testActivityBooking() {
        // Simulate pressing the submit button
        screen.submitButton.doClick();

        // Verify if the activity was booked successfully
        JOptionPane optionPane = (JOptionPane) JOptionPane.getFrameForComponent(screen).getContentPane();
        String message = optionPane.getMessage().toString();
        assertEquals("Activity booked successfully.", message);
    }

    @Test
    public void testRoomAvailabilityCheck() {
        // Assume room 101 is available at the specified time
        screen.roomDropdown.setSelectedItem("101 - Room A"); // Ensure room is set
        screen.checkAvailabilityButton.doClick();

        // Verify if the room availability check passed
        JOptionPane optionPane = (JOptionPane) JOptionPane.getFrameForComponent(screen).getContentPane();
        String message = optionPane.getMessage().toString();
        assertTrue(message.contains("The selected room is available for the desired time."));
    }

    @Test
    public void testOnlineActivitySelection() {
        // Set the activity to online
        screen.onlineRadioButton.setSelected(true);

        // Trigger the action event linked to online radio button selection
        screen.onlineRadioButton.doClick();

        // Check if the room dropdown is disabled as expected for online activities
        assertFalse(screen.roomDropdown.isEnabled());
        assertEquals("Room 1", screen.roomDropdown.getSelectedItem().toString());
    }
}
