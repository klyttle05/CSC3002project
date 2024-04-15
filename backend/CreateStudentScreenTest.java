import static org.junit.Assert.*;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class CreateStudentScreenTest {
    private CreateStudentScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new CreateStudentScreen();
            screen.setVisible(true); // Set the screen visible for interaction
        });
    }

    @Test
    public void testStudentCreationSuccess() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Provide test input for student creation
            screen.firstNameField.setText("Jane");
            screen.lastNameField.setText("Doe");
            screen.emailField.setText("jane.doe@example.com");
            screen.passwordField.setText("securepassword123");

            // Simulate button click
            screen.submitButton.doClick();

            // Check for success message
            String expectedMessage = "Student created successfully. Student ID: ";
            assertTrue("Expect the student creation to succeed.", screen.statuslabel.startsWith(expectedMessage));
        });
    }

    @Test
    public void testStudentCreationFailure() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Provide test input that is expected to fail
            screen.firstNameField.setText(""); // Empty first name
            screen.lastNameField.setText("Doe");
            screen.emailField.setText("jane.doe@example.com");
            screen.passwordField.setText("securepassword123");

            // Simulate button click
            screen.submitButton.doClick();

            // Check for failure message
            String expectedMessage = "Failed to create student.";
            assertEquals("Expect the student creation to fail due to validation.", expectedMessage, screen.statuslabel);
        });
    }
}