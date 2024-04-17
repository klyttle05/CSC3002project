import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private JTextField userIdField, emailField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private String recoveryCode; // Temporary recovery code

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        inputPanel.add(userIdField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        forgotPasswordButton = new JButton("Forgot Password");
        forgotPasswordButton.addActionListener(e -> handleForgotPassword());
        inputPanel.add(forgotPasswordButton);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        inputPanel.add(loginButton);

        add(inputPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loginUser();
    }

    private void loginUser() {
        String userIdText = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            int userId = Integer.parseInt(userIdText);
            String userType = isValidCredentials(userId, password);
            if (!userType.equals("Invalid")) {
                JOptionPane.showMessageDialog(this, "Login successful as " + userType, "Success", JOptionPane.INFORMATION_MESSAGE);
                new MainMenu(userType, userIdText).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String isValidCredentials(int userId, String password) {
        if (checkUserInTable(userId, password, "Staff")) {
            return "Staff";
        } else if (checkUserInTable(userId, password, "Students")) {
            return "Student";
        }
        return "Invalid";
    }

    private boolean checkUserInTable(int userId, String password, String tableName) {
        String query = "SELECT password_hash FROM " + tableName + " WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                return encoder.matches(password, storedHash);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(this, "Enter your email for password recovery:");
        if (email != null && !email.isEmpty()) {
            recoveryCode = generateRecoveryCode();
            sendEmail(email, "Your Password Recovery Code", "Here is your password recovery code: " + recoveryCode);
            String code = JOptionPane.showInputDialog(this, "Enter the recovery code sent to your email:");
            if (code != null && code.equals(recoveryCode)) {
                String newPassword = JOptionPane.showInputDialog(this, "Enter your new password:");
                if (newPassword != null && !newPassword.isEmpty()) {
                    updatePassword(email, newPassword);
                    JOptionPane.showMessageDialog(this, "Password updated successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid recovery code.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email not found or unable to send email.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateRecoveryCode() {
        return Integer.toString((int) (Math.random() * 9000) + 1000);
    }

    private void sendEmail(String to, String subject, String text) {
        // Note: Implement this method to actually send an email.
    }

    private void updatePassword(String email, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(newPassword);
        updatePasswordInTable(email, hashedPassword, "Staff");
        updatePasswordInTable(email, hashedPassword, "Students");
    }

    private void updatePasswordInTable(String email, String hashedPassword, String tableName) {
        String sql = "UPDATE " + tableName + " SET password_hash = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update password.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
