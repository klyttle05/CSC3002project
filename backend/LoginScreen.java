import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginScreen extends JFrame implements ActionListener {
    
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginScreen() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        inputPanel.add(userIdField);
        
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);
        
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            String password = new String(passwordField.getPassword());

            String userType = isValidCredentials(userId, password);
            if (!userType.equals("Invalid")) {
                MainMenu mainMenu = new MainMenu(userType, Integer.toString(userId));
                mainMenu.setVisible(true);
                this.dispose(); // Close the login screen
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String isValidCredentials(int userId, String password) {
        // Try to authenticate against Staff table first
        if (checkUserInTable(userId, password, "Staff")) {
            return "Staff";
        }
        // Then try Students table
        else if (checkUserInTable(userId, password, "Students")) {
            return "Student";
        }
        // If neither, return "Invalid"
        return "Invalid";
    }

    private boolean checkUserInTable(int userId, String password, String tableName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root")) {
            String sql = "SELECT password_hash FROM " + tableName + " WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                return encoder.matches(password, storedHash);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
