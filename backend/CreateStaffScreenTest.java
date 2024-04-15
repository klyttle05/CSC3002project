import static org.junit.Assert.*;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class CreateStaffScreenTest {
    private CreateStaffScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new CreateStaffScreen();
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testStaffCreationSuccess() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Provide test input for staff creation
            screen.firstNameField.setText("John");
            screen.lastNameField.setText("Doe");
            screen.emailField.setText("johne.doe@example.com");
            screen.departmentIdField.setText("1");
            screen.passwordField.setText("securepassword123");

            // Simulate button click
            screen.submitButton.doClick();

            // Check for success message
            String expectedMessage = "Staff member created successfully.";
            assertEquals("Expect the staff creation to succeed.", expectedMessage, screen.statuslabel);
        });
    }
}
