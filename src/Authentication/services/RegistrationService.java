package Authentication.services;

//Import Statements
import Authentication.cryptography.PasswordHasher;
import Authentication.model.UserRecord;
import Authentication.store.UserStore;
import Authentication.validation.EmailValidator;
import Authentication.validation.PasswordPolicy;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * RegistrationService
 * -------------------
 * Handles business logic for user registration.
 *
 * Responsibilities:
 *  - Validate registration input
 *  - Hash passwords securely
 *  - Create UserRecord instances
 *  - Persist new users via UserStore
 *
 * Non-Responsibilities:
 *  - No GUI interaction
 *  - No file I/O
 *  - No authentication/login logic
 *
 * Used By:
 *  - RegistrationController
 */
public final class RegistrationService 
{

    private final UserStore userStore;
    private final PasswordHasher passwordHasher;

    /**
     * @param userStore backing user persistence
     * @param passwordHasher password hashing implementation
     */
    public RegistrationService(UserStore userStore, PasswordHasher passwordHasher) 
    {
        this.userStore = Objects.requireNonNull(userStore, "userStore");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher");
    }

    /**
     * Registers a new user.
     *
     * @param email plaintext email
     * @param password plaintext password
     * @return the newly created UserRecord
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if user already exists
     */
    public UserRecord register(String email, String password) 
    {
        //Validate input
        EmailValidator.validateOrThrow(email);
        PasswordPolicy.validateOrThrow(password);

        //Hash password
        String passwordHash = passwordHasher.hash(password);

        //Create new user record
        UserRecord user = new UserRecord(UUID.randomUUID().toString(),email.trim(),passwordHash,List.of("USER"),true);

        //Persist user
        userStore.addUser(user);

        return user;
    }
}   
