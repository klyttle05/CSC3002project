import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class BookActivityScreenTest {
    private BookActivityScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new BookActivityScreen();
            screen.activityTypeDropdown.setSelectedItem("Lesson");
            screen.activityNameField.setText("Calculus Class");
            screen.startTimeSpinner.setValue(new Timestamp(new Date().getTime()));
            screen.endTimeSpinner.setValue(new Timestamp(new Date().getTime() + 3600000)); // one hour later
            screen.staffDropdown.setSelectedIndex(0);  // at least one staff in dropdown
            screen.roomDropdown.setSelectedIndex(0);  // at least one room in dropdown
            screen.onlineRadioButton.setSelected(false);
        });
    }

    @Test
    public void testSubmitButtonAction() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.submitButton.doClick();
            // Check if a success message is displayed or a certain state is achieved
            String expectedMessage = "Activity booked successfully.";
            assertEquals("Activity should be booked successfully.", expectedMessage, screen.statuslabel);
        });
    }

    @Test
    public void testCheckRoomAvailability() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.roomDropdown.setSelectedItem("101 - Room A");
            screen.checkAvailabilityButton.doClick();
            // After clicking check availability, you can check for the expected output, such as:
            String expectedAvailabilityMessage = "The selected room is available for the desired time.";
            assertEquals("Room availability should be correctly determined.", expectedAvailabilityMessage, screen.statuslabel);
        });
    }
}