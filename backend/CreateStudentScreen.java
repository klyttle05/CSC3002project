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

public class CreateStudentScreen extends JFrame {
    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField;
    private JButton submitButton;

    public CreateStudentScreen() {
        setTitle("Create New Student");
        setSize(300, 200); // Adjusted size since student number field is removed
        setLayout(new GridLayout(5, 2)); // Adjusted grid layout

        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

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
        String password = new String(passwordField.getPassword());

        // Implement password hashing here
        String hashedPassword = password; // Placeholder for hashed password

        insertStudent(firstName, lastName, email, hashedPassword);
    }

    private void insertStudent(String firstName, String lastName, String email, String hashedPassword) {
        String sql = "INSERT INTO Students (first_name, last_name, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPassword);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Student created successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Connection getConnection() {
        try {
            // Replace these with your actual database details
            String url = "jdbc:mysql://localhost:3306/universitymanagementsystem";
            String user = "root";
            String password = "root";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateStudentScreen().setVisible(true));
    }
}
