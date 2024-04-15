import static org.junit.Assert.*;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

public class LoginScreenTest {
    private LoginScreen screen;

    @Before
    public void setUp() throws Exception {
        // Initialize the screen on the Event Dispatch Thread to handle GUI components properly
        SwingUtilities.invokeAndWait(() -> {
            screen = new LoginScreen();
            screen.setVisible(true); // Necessary to ensure components are properly initialized
        });
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Mock the user input for successful login
            screen.userIdField.setText("2");
            screen.passwordField.setText("kk");

            // Simulate button click
            screen.loginButton.doClick();

            assertFalse("The login screen should be closed upon successful login.", screen.isVisible());
        });
    }

    @Test
    public void testFailedLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Mock the user input for failed login
            screen.userIdField.setText("1");  // Valid user ID
            screen.passwordField.setText("wrongPassword");  // Incorrect password

            // Simulate button click
            screen.loginButton.doClick();

            // Check for the error message shown to the user
            String expectedMessage = "Login failed! Please try again.";
            assertEquals("Expect a failure message upon failed login.", expectedMessage, screen.statuslabel);
        });
    }

    @Test
    public void testInvalidUserIdInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Mock the user input for non-numeric ID
            screen.userIdField.setText("abc");  // Non-numeric input
            screen.passwordField.setText("anyPassword");

            // Simulate button click
            screen.loginButton.doClick();

            // Check for the input error message shown to the user
            String expectedMessage = "Please enter a valid numeric ID.";
            assertEquals("Expect an input error message for non-numeric user ID.", expectedMessage, screen.statuslabel);
        });
    }
}
