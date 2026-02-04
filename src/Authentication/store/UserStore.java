package Authentication.store;

//Import statements
import Authentication.model.UserRecord;
import java.util.Optional;

/**
 * UserStore
 * ---------
 * Abstraction layer for accessing and modifying user records.
 *
 * This interface defines the contract between higher-level services
 * (authentication and registration) and the underlying persistence
 * mechanism (JSON file, database, etc.).
 *
 * Responsibilities:
 *  - Provide read access to user records
 *  - Provide write access for user registration
 *
 * Non-Responsibilities:
 *  - No password hashing
 *  - No validation logic
 *  - No authentication logic
 *  - No knowledge of file formats or databases
 *
 * Design Notes:
 *  - Implementations must handle persistence safely (e.g., locking,
 *    atomic writes) if writing is supported.
 *  - Authentication logic assumes read operations are reliable.
 *
 * Used By:
 *  - RegistrationService (to add new users)
 *  - EmailPasswordLoginModule (to look up users)
 */
public interface UserStore 
{

    /**
     * Finds a user by email.
     *
     * @param email login email (case-insensitive)
     * @return Optional containing the UserRecord if found, otherwise empty
     */
    Optional<UserRecord> findByEmail(String email);

    /**
     * Adds a new user to the store.
     *
     * Implementations must ensure:
     *  - Email uniqueness
     *  - Safe persistence (no partial writes)
     *
     * @param user fully constructed user record
     * @throws IllegalStateException if a user with the same email already exists
     */
    void addUser(UserRecord user);
}
