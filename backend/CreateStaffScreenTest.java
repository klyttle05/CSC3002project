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
            screen.emailField.setText("john.doe@example.com");
            screen.departmentIdField.setText("1");
            screen.passwordField.setText("securepassword123");

            // Simulate button click
            screen.submitButton.doClick();

            // Check for success message
            String expectedMessage = "Staff member created successfully.";
            assertEquals("Expect the staff creation to succeed.", expectedMessage, screen.statuslabel);
        });
    }

    @Test
    public void testStaffCreationFailure() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Provide test input that is expected to fail
            screen.firstNameField.setText(""); // Empty first name
            screen.lastNameField.setText("Doe");
            screen.emailField.setText("john.doe@example.com");
            screen.departmentIdField.setText("1");
            screen.passwordField.setText("securepassword123");

            // Simulate button click
            screen.submitButton.doClick();

            // Check for failure message
            String expectedMessage = "Failed to create staff member.";
            assertEquals("Expect the staff creation to fail due to validation.", expectedMessage, screen.statuslabel);
        });
    }
}
