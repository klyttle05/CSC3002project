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
    
    private JTextField userField;
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
        inputPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        inputPanel.add(userField);
        
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
        String username = userField.getText();
        String password = new String(passwordField.getPassword());

        if ("admin".equals(username) && "admin".equals(password)) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            openMainMenu("Admin");
        } else if (isStudentInDatabase(username)) {
            // Assuming no password required for student login as per requirements
            JOptionPane.showMessageDialog(this, "Student login successful!");
            openMainMenu("Student");
        } else {
            JOptionPane.showMessageDialog(this, "Login failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isStudentInDatabase(String studentNumber) {
        // Placeholder for database check
        // Implement actual database query here to check student existence
        return true; // For demonstration, assume true. Replace with actual check.
    }

    private void openMainMenu(String userType) {
        // Close the login window
        this.dispose();
        // Open the Main Menu window
        //SwingUtilities.invokeLater(() -> new MainMenu(userType).setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
