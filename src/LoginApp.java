import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class LoginAppTest {

    private LoginApp loginApp;
    private JTextField emailField;
    private JPasswordField passwordField;

    @BeforeEach
    void setUp() {
        loginApp = new LoginApp();
        emailField = loginApp.getEmailField();
        passwordField = loginApp.getPasswordField();
    }
    public JTextField getEmailField() {
        return emailField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }
    public class LoginApp {
        public boolean isPasswordStrong(String password) {
            if (password == null || password.length() < 8) {
                return false; // Minimum length requirement
            }
            boolean hasUpperCase = password.matches(".[A-Z].");
            boolean hasLowerCase = password.matches(".[a-z].");
            boolean hasDigit = password.matches(".\\d.");
            boolean hasSpecialChar = password.matches(".[!@#$%^&()].*");

            return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
}
    }

    // Test 1: SQL Injection Test
    @Test
    void testSQLInjection() {
        String maliciousEmail = "' OR '1'='1";
        String maliciousPassword = "' OR '1'='1";

        emailField.setText(maliciousEmail);
        passwordField.setText(maliciousPassword);

        String userName = loginApp.authenticateUser(maliciousEmail, maliciousPassword);

        assertNull(userName, "Login should fail for SQL injection attempts");

        JOptionPane.showMessageDialog(null, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }

    // Test 2: Case Sensitivity Test
    @Test
    void testCaseSensitivity() {
        String originalEmail = "JohnDoe@example.com";
        String originalPassword = "Password123";
        String incorrectEmail = "johndoe@example.com"; // lowercase email
        String incorrectPassword = "password123"; // lowercase password

        emailField.setText(originalEmail);
        passwordField.setText(originalPassword);
        String userName = loginApp.authenticateUser(originalEmail, originalPassword);

        assertNotNull(userName, "Login should succeed with correct case");
        assertEquals("John Doe", userName, "Expected username doesn't match the result");

        emailField.setText(incorrectEmail);
        passwordField.setText(incorrectPassword);
        userName = loginApp.authenticateUser(incorrectEmail, incorrectPassword);

        assertNull(userName, "Login should fail if case sensitivity is enforced");
    }

    // Test 3: Locked Account Test
    @Test
    void testLockedAccount() {
        String email = "lockeduser@example.com";
        String wrongPassword = "wrongPassword";
        String correctPassword = "correctPassword";

        for (int i = 0; i < 3; i++) {
            emailField.setText(email);
            passwordField.setText(wrongPassword);

            String userName = loginApp.authenticateUser(email, wrongPassword);
            assertNull(userName, "Login should fail with incorrect password");
        }

        emailField.setText(email);
        passwordField.setText(correctPassword);

        String userName = loginApp.authenticateUser(email, correctPassword);
        assertNull(userName, "Login should fail if the account is locked, even with the correct password");

        JOptionPane.showMessageDialog(null, "Account locked due to too many failed attempts.", "Account Locked", JOptionPane.ERROR_MESSAGE);
    }

    // Test 4: Weak Password Detection Test
    @Test
    void testWeakPasswordDetection() {
        String weakPassword = "12345";
        String email = "newuser@example.com";

        boolean isPasswordValid = loginApp.isPasswordStrong(weakPassword);

        assertFalse(isPasswordValid, "Weak passwords should not be accepted");

        JOptionPane.showMessageDialog(null, "Password is too weak. Please use a stronger password.", "Weak Password", JOptionPane.WARNING_MESSAGE);
    }

    // Test 5: Login With Special Characters
    @Test
    void testSpecialCharactersLogin() {
        String email = "user+special@example.com";
        String password = "P@ssw0rd!#";

        emailField.setText(email);
        passwordField.setText(password);

        String userName = loginApp.authenticateUser(email, password);

        assertNotNull(userName, "Login should succeed for valid credentials with special characters");
        assertEquals("Special User", userName, "Expected username doesn't match the result");

        JOptionPane.showMessageDialog(null, "Welcome, " + userName + "!");
    }
}
