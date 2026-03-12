
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
    int bugNum;
   //register a new user 
    public int registerNewUser(String email, String password, String conPassword) throws Exception {

        if (email == null || email.trim().isEmpty()) {
            bugNum=1;
            throw new IllegalArgumentException("Email is required.");
            
        }else if (!email.matches(".+@.+\\.com")) {
            bugNum=2;
            throw new IllegalArgumentException("Valid email is required.");
            
        }else if (password == null || password.length() < 8) {
            bugNum=3;
            throw new IllegalArgumentException("Password must be at least 8 characters.");

         }else if(!password.equals(conPassword)){
            bugNum=4;
            throw new IllegalArgumentException("passwords must match");
  
        }else if (!password.matches(".*[A-Z].*")) {
            bugNum=5;
            throw new IllegalArgumentException("Password must contain at least 1 uppercase character.");
            
        }else if (!password.matches(".*[a-z].*")) {
            bugNum=6;
            throw new IllegalArgumentException("Password must contain at least 1 lowercase character.");
            
        }else if (!password.matches(".*\\d.*")) {
            bugNum=7;
            throw new IllegalArgumentException("Password must contain at least 1 number.");
            
        }else if (!password.matches(".*[^a-zA-Z0-9].*")) {
            bugNum = 8;
            throw new IllegalArgumentException("Password must contain at least 1 special character.");

        }else if (sqlHandler.emailExists(email)) {
            bugNum=9;
            throw new IllegalArgumentException("Email already exists.");      
        }else{
            String salt = generateSalt();
            String hash = hashPassword(password, salt);
            sqlHandler.createUser(email.trim(), hash, salt);
        }

       



       
        return bugNum;
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
   public int resetPassword(String email, String password, String conPassword) throws Exception {

       
        if (password == null || password.length() < 8) {
            bugNum=3;
            throw new IllegalArgumentException("Password must be at least 8 characters.");

         }else if(!password.equals(conPassword)){
            bugNum=4;
            throw new IllegalArgumentException("passwords must match");
  
        }else if (!password.matches(".*[A-Z].*")) {
            bugNum=5;
            throw new IllegalArgumentException("Password must contain at least 1 uppercase character.");
            
        }else if (!password.matches(".*[a-z].*")) {
            bugNum=6;
            throw new IllegalArgumentException("Password must contain at least 1 lowercase character.");
            
        }else if (!password.matches(".*\\d.*")) {
            bugNum=7;
            throw new IllegalArgumentException("Password must contain at least 1 number.");
            
        }else if (!password.matches(".*[^a-zA-Z0-9].*")) {
            bugNum = 8;
            throw new IllegalArgumentException("Password must contain at least 1 special character.");

        }else if (sqlHandler.emailExists(email)) {
            bugNum=9;
            throw new IllegalArgumentException("Email already exists.");      
        }else{

        String salt = generateSalt();
        String hash = hashPassword(newPassword, salt);
        sqlHandler.updatePassword(email.trim(), hash, salt);
        return bugNum;
        }
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