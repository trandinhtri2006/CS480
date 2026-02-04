package Authentication.validation;

//Import Statements
import java.util.Objects;

/**
 * PasswordPolicy
 * --------------
 * Defines password requirements for user registration.
 *
 * Responsibilities:
 *  - Enforce minimum password rules
 *  - Provide clear validation failures
 *
 * Non-Responsibilities:
 *  - No hashing
 *  - No persistence
 *  - No authentication logic
 *
 * Design Notes:
 *  - Rules are intentionally simple for a desktop application.
 *  - Can be tightened later without changing registration logic.
 *
 * Used By:
 *  - RegistrationService
 */
public final class PasswordPolicy 
{
    //Private Fields
    private static final int MIN_LENGTH = 8;

    //Constructor; WILL NEED TO BE MODIFIED FOR TESTING
    private PasswordPolicy() 
    {
        // utility class; prevent instantiation
    }

    /**
     * Checks whether a password meets minimum requirements.
     *
     * @param password plaintext password
     * @return true if password is acceptable
     */
    public static boolean isValid(String password) 
    {
        //Error handling for null
        if (password == null) 
        {
            return false;
        }

        //Return whether length is good enough
        return password.length() >= MIN_LENGTH;
    }

    /**
     * Validates a password and throws an exception if invalid.
     *
     * @param password plaintext password
     * @throws IllegalArgumentException if password does not meet policy
     */
    public static void validateOrThrow(String password) 
    {
        //Null check
        Objects.requireNonNull(password, "password");

        //Check password validity
        if (!isValid(password)) 
        {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters long");
        }
    }
}
