import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mysql.cj.protocol.Message;

public class LoginScreen extends JFrame implements ActionListener {
    
    public JTextField userIdField;
    public JPasswordField passwordField;
    public JButton loginButton;
    public String statuslabel;

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
                statuslabel = "Login failed! Please try again.";
                JOptionPane.showMessageDialog(this, "Login failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            statuslabel = "Please enter a valid numeric ID.";
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

    private boolean sendRecoveryCode(String email) {
        String recoveryCode = generateRecoveryCode();
        if (sendCodeAndUpdateUser(email, recoveryCode, "Staff") || sendCodeAndUpdateUser(email, recoveryCode, "Students")) {
            String subject = "Your Password Recovery Code";
            String messageText = "Here is your password recovery code: " + recoveryCode;
            try {
                sendEmail(email, subject, messageText);
                return true;
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    
    private void sendEmail(String to, String subject, String text) throws MessagingException {
        String from = "your-email@example.com";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.example.com");
        Session session = Session.getDefaultInstance(properties);
    
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
    
        Transport.send(message);
    }

    private boolean sendCodeAndUpdateUser(String email, String recoveryCode, String tableName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement("SELECT email FROM " + tableName + " WHERE email = ?");
             PreparedStatement updateStmt = conn.prepareStatement("UPDATE " + tableName + " SET recovery_code = ? WHERE email = ?")) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                updateStmt.setString(1, recoveryCode);
                updateStmt.setString(2, email);
                updateStmt.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    private boolean validateRecoveryCode(String email, String code) {
        return checkCodeInTable(email, code, "Staff") || checkCodeInTable(email, code, "Students");
    }
    
    private boolean checkCodeInTable(String email, String code, String tableName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement("SELECT recovery_code FROM " + tableName + " WHERE email = ?")) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getString("recovery_code").equals(code)) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    private void updatePassword(String email, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(newPassword);
    
        updatePasswordInTable(email, hashedPassword, "Staff");
        updatePasswordInTable(email, hashedPassword, "Students");
    }
    
    private void updatePasswordInTable(String email, String hashedPassword, String tableName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement("UPDATE " + tableName + " SET password_hash = ? WHERE email = ?")) {
            
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update password in " + tableName, "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
