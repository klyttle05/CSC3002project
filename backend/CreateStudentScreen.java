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
    private JTextField studentNumberField, firstNameField, lastNameField, emailField;
    private JPasswordField passwordField;
    private JButton submitButton;

    public CreateStudentScreen() {
        setTitle("Create New Student");
        setSize(300, 250);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Student Number:"));
        studentNumberField = new JTextField();
        add(studentNumberField);

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
        String studentNumber = studentNumberField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Assuming BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // replace this with actual password hashing
        String hashedPassword = password; // Use actual password hashing here

        insertStudent(studentNumber, firstName, lastName, email, hashedPassword);
    }

    private void insertStudent(String studentNumber, String firstName, String lastName, String email, String hashedPassword) {
        String sql = "INSERT INTO Students (student_number, first_name, last_name, email, password_hash) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentNumber);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, hashedPassword);
            
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateStudentScreen().setVisible(true));
    }
}
