import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CreateStaffScreen extends JFrame {
    private JTextField firstNameField, lastNameField, emailField, departmentIdField;
    private JPasswordField passwordField;
    private JButton submitButton;

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

        // Placeholder for the BCrypt password encoder
        // Assume BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = password; // Use actual password hashing here

        insertStaffMember(firstName, lastName, email, departmentId, hashedPassword);
    }

    private void insertStaffMember(String firstName, String lastName, String email, String departmentId, String hashedPassword) {
        String sql = "INSERT INTO Staff (first_name, last_name, email, department, password_hash) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, departmentId);
            pstmt.setString(5, hashedPassword);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Staff member created successfully.");
            } else {
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
