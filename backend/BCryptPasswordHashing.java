import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordHashing {
    
    // Create a BCryptPasswordEncoder object. You might want to reuse this instance rather than creating a new one every time.
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * Hashes a password using BCrypt.
     * 
     * @param password The plaintext password to hash.
     * @return A hashed representation of the password.
     */
    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    /**
     * Verifies a plaintext password against a hashed password.
     * 
     * @param plaintext The plaintext password to verify.
     * @param hashed The hashed password to compare against.
     * @return true if the plaintext password matches the hashed password, false otherwise.
     */
    public static boolean verifyPassword(String plaintext, String hashed) {
        return encoder.matches(plaintext, hashed);
    }

    public static void main(String[] args) {
        // Example usage
        String originalPassword = "password123";
        String hashedPassword = hashPassword(originalPassword);

        System.out.println("Original: " + originalPassword);
        System.out.println("Hashed: " + hashedPassword);

        // Verifying the password
        boolean isMatch = verifyPassword("password123", hashedPassword);
        System.out.println("Password matches: " + isMatch);
    }
}
