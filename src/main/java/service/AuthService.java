
package service;

import db.SQLHandler;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import model.User;

//handles authentication logic 
public class AuthService {

    private final SQLHandler sqlHandler;

    public AuthService(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }

   //register a new user 
    public int registerUser(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // Must be gmail domain
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must be a Gmail address.");
        }

        // Must be gmail domain
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must be a Gmail address.");
        }

        // Extract local part (before @)
        String localPart = email.substring(0, email.indexOf("@"));

        // Local part must be alphanumeric only
        if (!localPart.matches("[A-Za-z0-9]+")) {
            throw new IllegalArgumentException("Email username must contain only letters and numbers.");
        }

        if (password == null) {
            throw new IllegalArgumentException("Password is required.");
        }

        // Length: 8–30
        if (password.length() < 8 || password.length() > 30) {
            throw new IllegalArgumentException("Password must be 8–30 characters long.");
        }

        // At least one lowercase
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must include at least one lowercase letter.");
        }

        // At least one uppercase
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must include at least one uppercase letter.");
        }

        // At least one digit
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must include at least one number.");
        }

        // At least one special character
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Password must include at least one special character.");
        }

        if (sqlHandler.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }

        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return sqlHandler.createUser(email.trim(), hash, salt);
    }

    //authenticate the user 
    public User loginUser(String email, String password) throws Exception {

        User user = sqlHandler.getUserByEmail(email);

        if (user == null) {
            return null;
        }

        String computedHash = hashPassword(password, user.getPasswordSalt());

        if (!computedHash.equals(user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    //updates a pasword 
    public boolean resetPassword(String email, String newPassword) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // Must be gmail domain
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must be a Gmail address.");
        }

        // Must be gmail domain
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must be a Gmail address.");
        }
        
        // Length: 8–30
        if (newPassword.length() < 8 || newPassword.length() > 30) {
            throw new IllegalArgumentException("Password must be 8–30 characters long.");
        }

        // At least one lowercase
        if (!newPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must include at least one lowercase letter.");
        }

        // At least one uppercase
        if (!newPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must include at least one uppercase letter.");
        }

        // At least one digit
        if (!newPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must include at least one number.");
        }

        // At least one special character
        if (!newPassword.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Password must include at least one special character.");
        }

        String salt = generateSalt();
        String hash = hashPassword(newPassword, salt);

        return sqlHandler.updatePassword(email.trim(), hash, salt);
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        String salted = password + salt;

        byte[] hashBytes = digest.digest(salted.getBytes());

        return Base64.getEncoder().encodeToString(hashBytes);
    }
}