 package Authentication.model;

/**
 * UserRecord
 * ----------
 * Immutable data model representing a single application user.
 *
 * This class maps directly to a user entry in `users.json` and serves as the
 * canonical representation of user identity and credentials throughout
 * authentication and registration.
 *
 * Responsibilities:
 *  - Hold user identity data (id, email)
 *  - Hold credential data (password hash only — never plaintext)
 *  - Hold authorization data (roles)
 *  - Hold account state (enabled / disabled)
 *
 * Non-Responsibilities:
 *  - No password hashing logic
 *  - No validation logic
 *  - No persistence logic
 *  - No authentication logic
 *
 * Design Notes:
 *  - This class should be treated as immutable once constructed.
 *  - Passwords must NEVER be stored here in plaintext.
 *  - Any changes to this model must be reflected in the JSON schema.
 *
 * Used By:
 *  - JsonUserStore (loading / saving users)
 *  - RegistrationService (user creation)
 *  - EmailPasswordLoginModule (authentication)
 *  - AppSession (identity reference)
 */

//UserRecord Class
public final class UserRecord 
{
    //Local variables
    /**
     * Stable internal identifier for the user.
     * Not exposed to the GUI.
     */
    private final String id;

    /**
     * User's login identifier.
     * Expected to be unique. Case doesn't matter.
     */
    private final String email;

    /**
     * Secure password hash: (bcrypt / argon2).
     * Never in plaintext.
     */
    private final String passwordHash;

    /**
     * Logical roles assigned to the user (e.g. USER, ADMIN).
     * Used for authorization decisions.
     */
    private final java.util.List<String> roles;

    /**
     * Whether the account is enabled.
     * Disabled users must fail authentication.
     */
    private final boolean enabled;

    /**
     * UserRecord Constructor
     *
     * @param id unique internal identifier
     * @param email login email
     * @param passwordHash hashed password
     * @param roles list of assigned roles
     * @param enabled account status flag
     */
    public UserRecord(String id,String email,String passwordHash,java.util.List<String> roles,boolean enabled) 
    {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = java.util.List.copyOf(roles);
        this.enabled = enabled;
    }

    /**
     * getId method 
     * 
     * @return internal user identifier */
    public String getId() 
    {
        return id;
    }

    /** @return login email */
    public String getEmail() {
        return email;
    }

    /** @return password hash */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @return immutable list of roles */
    public java.util.List<String> getRoles() {
        return roles;
    }

    /** @return true if account is enabled */
    public boolean isEnabled() {
        return enabled;
    }
}
