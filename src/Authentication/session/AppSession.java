package Authentication.session;

import Authentication.model.UserRecord;
import java.util.Optional;

/**
 * AppSession
 * ----------
 * Holds the authentication state for the currently running application.
 *
 * This class represents the "logged-in user" concept for the desktop app.
 * It is intentionally simple and UI-agnostic.
 *
 * Responsibilities:
 *  - Track the currently authenticated user
 *  - Provide read-only access to session state
 *  - Allow login/logout transitions
 *
 * Non-Responsibilities:
 *  - No authentication logic
 *  - No password handling
 *  - No persistence
 *  - No authorization decisions
 *
 * Design Notes:
 *  - Desktop apps typically have a single active session.
 *  - This class is implemented as a simple singleton.
 *
 * Used By:
 *  - AuthController (login / logout)
 *  - GUI components (checking login state)
 */
public final class AppSession 
{
    //Singleton instance of AppSession
    private static final AppSession INSTANCE = new AppSession();

    //Currently authenticated user
    private UserRecord currentUser;

    //Temporary private constructor to enforce singleton pattern WILL NEED TO BE MODIFIED FOR TESTING
    private AppSession() 
    {
        // prevent external instantiation
    }

    /**
     * @return the singleton AppSession instance
     */
    public static AppSession get() 
    {
        return INSTANCE;
    }

    /**
     * Sets the currently authenticated user.
     *
     * @param user authenticated user record
     */
    public void login(UserRecord user) 
    {
        this.currentUser = user;
    }

    /**
     * Clears the current session.
     */
    public void logout() 
    {
        this.currentUser = null;
    }

    /**
     * @return true if a user is currently authenticated
     */
    public boolean isAuthenticated() 
    {
        return currentUser != null;
    }

    /**
     * @return Optional containing the current user, if authenticated
     */
    public Optional<UserRecord> getCurrentUser() 
    {
        return Optional.ofNullable(currentUser);
    }
}
