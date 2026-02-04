package Authentication.cryptography;

/**
 * PasswordHasher
 * --------------
 * Abstraction for secure password hashing and verification.
 *
 * This interface isolates authentication and registration logic
 * from the specific hashing algorithm used (bcrypt, argon2, etc.).
 *
 * Responsibilities:
 *  - Generate secure password hashes
 *  - Verify plaintext passwords against stored hashes
 *
 * Non-Responsibilities:
 *  - No persistence logic
 *  - No user lookup logic
 *  - No validation logic (length, strength, etc.)
 *
 * Design Notes:
 *  - Implementations must use a slow, salted hashing algorithm.
 *  - Plaintext passwords must NEVER be stored or returned.
 *
 * Used By:
 *  - RegistrationService (hashing new passwords)
 *  - EmailPasswordLoginModule (verifying passwords)
 */
public interface PasswordHasher 
{

    /**
     * Hashes a plaintext password.
     *
     * @param plainPassword plaintext password
     * @return secure hashed representation
     */
    String hash(String plainPassword);

    /**
     * Verifies a plaintext password against a stored hash.
     *
     * @param plainPassword plaintext password
     * @param storedHash previously generated hash
     * @return true if password matches, false otherwise
     */
    boolean verify(String plainPassword, String storedHash);
}
