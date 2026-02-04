package Authentication.validation;

//Import Statements
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * EmailValidator
 * --------------
 * Utility class for validating email addresses during registration.
 *
 * Responsibilities:
 *  - Validate email format
 *  - Enforce basic sanity rules
 *
 * Non-Responsibilities:
 *  - No uniqueness checks
 *  - No persistence logic
 *  - No authentication logic
 *
 * Design Notes:
 *  - This validator is intentionally conservative.
 *  - It does not attempt to fully implement RFC email specs.
 *  - Goal is to catch obvious invalid input, not be perfect.
 *
 * Used By:
 *  - RegistrationService
 */
public final class EmailValidator 
{

    //Simple email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    //Constructor; WILL NEED TO BE MODIFIED FOR TESTING
    private EmailValidator() 
    {
        // utility class; prevent instantiation
    }

    /**
     * Checks whether the given email is valid.
     *
     * @param email input email
     * @return true if email format is acceptable
     */
    public static boolean isValid(String email) 
    {
        //Error handling for null
        if (email == null) 
        {
            return false;
        }

        //Trim whitespace and check format
        String trimmed = email.trim();
        if (trimmed.isEmpty()) 
        {
            return false;
        }

        //Return pattern match result
        return EMAIL_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates the email and throws an exception if invalid.
     *
     * @param email input email
     * @throws IllegalArgumentException if email is invalid
     */
    public static void validateOrThrow(String email) 
    {
        //Null check
        Objects.requireNonNull(email, "email");

        //Throw exception if invalid email
        if (!isValid(email)) 
        {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
