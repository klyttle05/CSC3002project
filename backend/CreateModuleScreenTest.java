import static org.junit.Assert.*;

import java.sql.Timestamp;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class CreateModuleScreenTest {
    private CreateModuleScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new CreateModuleScreen();
        });
    }

    @Test
    public void testModuleCreation() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.moduleNameField.setText("Advanced Programming");
            screen.staffDropdown.setSelectedIndex(0);  // assuming at least one staff is loaded for the test
            screen.roomDropdown.setSelectedIndex(0);  // assuming at least one room is loaded for the test
            screen.dayOfWeekDropdown.setSelectedIndex(0); // select "Monday"
            screen.startTimeSpinner.setValue(new Timestamp(System.currentTimeMillis()));
            screen.endTimeSpinner.setValue(new Timestamp(System.currentTimeMillis() + 3600000)); // plus one hour
            screen.studentList.setSelectedIndices(new int[]{0});  // selecting the first student for simplicity

            screen.submitButton.doClick();

            String expectedMessage = "Module created and scheduled lessons added successfully.";
            assertEquals("Expect the module creation to succeed.", expectedMessage, screen.statusLabel);
        });
    }

    @Test
    public void testLoadStaffMembers() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.loadStaffMembers();
            assertFalse("Staff list should not be empty.", screen.staffDropdown.getModel().getSize() == 0);
        });
    }

    @Test
    public void testLoadRooms() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.loadRooms();
            assertFalse("Room list should not be empty.", screen.roomDropdown.getModel().getSize() == 0);
        });
    }

    @Test
    public void testLoadStudents() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            screen.loadStudents();
            assertFalse("Students list should not be empty.", screen.studentListModel.getSize() == 0);
        });
    }
}
