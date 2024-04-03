import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginScreen extends JFrame implements ActionListener {
    
    private JTextField userIdField; // Assuming you have a field for User ID or Username
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginScreen() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        
        setLayout(new BorderLayout());
        
        // Panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        inputPanel.add(userIdField);
        
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);
        
        add(inputPanel, BorderLayout.CENTER);
        
        // Panel for login button
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userId = userIdField.getText();
        String password = new String(passwordField.getPassword());

        // Authentication logic here
        if (isValidCredentials(userId, password)) {
            String userType = determineUserType(userId); // Implement this based on your logic
            MainMenu mainMenu = new MainMenu(userType, userId);
            mainMenu.setVisible(true);
            this.dispose(); // Close the login screen
        } else {
            JOptionPane.showMessageDialog(this, "Login failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Placeholder for credentials validation method
    private boolean isValidCredentials(String userId, String password) {
        // Implement your validation logic here
        // For demonstration purposes, let's assume any input is valid
        return true;
    }

    // Placeholder for determining user type
    private String determineUserType(String userId) {
        // Implement logic to determine if the user is an Admin or Student
        // For demonstration, return "Admin" or "Student" based on some condition
        return userId.startsWith("A") ? "Admin" : "Student";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
