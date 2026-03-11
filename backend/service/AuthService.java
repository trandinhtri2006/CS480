
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

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
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

    if (newPassword == null || newPassword.length() < 8) {
        throw new IllegalArgumentException("Password must be at least 8 characters with a special character, uppercase, lowercase letter, and a number.");
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