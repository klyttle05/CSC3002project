import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreateStaffScreen extends JFrame {
    public JTextField firstNameField, lastNameField, emailField, departmentIdField;
    public JPasswordField passwordField;
    public JButton submitButton;
    public String statuslabel;

    public CreateStaffScreen() {
        setTitle("Create New Staff Member");
        setSize(300, 250);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Department:"));
        departmentIdField = new JTextField();
        add(departmentIdField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this::submitAction);
        add(submitButton);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
    }

    private void submitAction(ActionEvent e) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String departmentId = departmentIdField.getText();
        String password = new String(passwordField.getPassword());

        // Hashing the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        insertStaffMember(firstName, lastName, email, departmentId, hashedPassword);
    }

    private void insertStaffMember(String firstName, String lastName, String email, String departmentId, String hashedPassword) {
        String sql = "INSERT INTO Staff (first_name, last_name, email, password_hash) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPassword);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long newStaffId = generatedKeys.getLong(1);
                        statuslabel = "Staff member created successfully.";
                        JOptionPane.showMessageDialog(this, "Staff member created successfully. Staff ID: " + newStaffId);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to retrieve staff ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                statuslabel = "Failed to create staff member.";
                JOptionPane.showMessageDialog(this, "Failed to create staff member.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating staff member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateStaffScreen().setVisible(true));
    }
}
