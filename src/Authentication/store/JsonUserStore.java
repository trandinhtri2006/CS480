//Package Location
package Authentication.store;

//Import Statements
import Authentication.model.UserRecord;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.*;

/**
 * JsonUserStore Class
 * ------------
 * JSON-backed implementation of UserStore.
 *
 * Stores user records in a local JSON file (users.json). Supports:
 *  - findByEmail (read)
 *  - addUser (write)
 *
 * Persistence Requirements:
 *  - Writes must be atomic (write temp file, then replace).
 *  - Writes must be synchronized/locked to prevent corruption.
 *
 * Notes:
 *  - This class intentionally does NOT implement JSON parsing yet because we don't have Maven yet.
 *  - JSON reading/writing will be implemented later once a JSON 
 */
public final class JsonUserStore implements UserStore 
{

    //Private Fields
    private final Path usersFile;

    /**
     * @param usersFile path to users.json (must be a writable location)
     */
    public JsonUserStore(Path usersFile) 
    {
        this.usersFile = Objects.requireNonNull(usersFile, "usersFile");
    }
    //Override of findByEmail method
    @Override
    public Optional<UserRecord> findByEmail(String email) 
    {
        Objects.requireNonNull(email, "email");
        String normalized = normalizeEmail(email);

        // Read-only operation. Shared lock prevents reading
        // partially-written data during registration.
        try (FileChannel channel = FileChannel.open(usersFile,StandardOpenOption.CREATE,StandardOpenOption.READ);
        //Will be used later to lock the file     
        FileLock lock = channel.lock(0L, Long.MAX_VALUE, true))
        {
            // Load all users and search for matching email
            List<UserRecord> users = loadAll();
            
            //Loop through users JSON to find matching email
            for (UserRecord user : users) 
            {
                if (normalizeEmail(user.getEmail()).equals(normalized)) 
                {
                    return Optional.of(user);
                }
            }
            return Optional.empty();

        } 
        catch (IOException e) 
        {
            throw new IllegalStateException("Failed to read users store: " + usersFile, e);
        }
    }
    //Override of addUser method
    @Override
    public void addUser(UserRecord user) 
    {
        //Validate user object
        Objects.requireNonNull(user, "user");

        //Write operation must be exclusive
        try (FileChannel channel = FileChannel.open(usersFile,StandardOpenOption.CREATE,StandardOpenOption.READ,StandardOpenOption.WRITE);
             FileLock lock = channel.lock()) 
        {
            // Load existing users
            List<UserRecord> users = loadAll();

            //Enforce unique email (case-insensitive)
            String newEmail = normalizeEmail(user.getEmail());
            for (UserRecord existing : users) 
            {
                if (normalizeEmail(existing.getEmail()).equals(newEmail)) 
                {
                    throw new IllegalStateException("User already exists with email: " + user.getEmail());
                }
            }

            //Add new user
            users.add(user);

            //Atomic write: write to temp file then replace. Sounds cool.
            atomicSaveAll(users);

        } 
        catch (IOException e) 
        {
            throw new IllegalStateException("Failed to write users store: " + usersFile, e);
        }
    }

    /**
     * Normalizes email for consistent comparisons.
     *
     * Currently, this just lowercases the email.
     */
    private String normalizeEmail(String email) 
    {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Loads all users from the JSON file.
     *
     * NOTE TO SELF:
     * Implement JSON parsing here using Gson or Jackson once
     * dependency management (Maven) is introduced.
     * We dont have maven yet so we cant add JSON libraries so this is just a placeholder.
     *
     * Expected JSON schema:
     * {
     *   "users": [
     *     {
     *       "id": "...",
     *       "email": "...",
     *       "passwordHash": "...",
     *       "roles": ["USER"],
     *       "enabled": true
     *     }
     *   ]
     * }
     */
    private List<UserRecord> loadAll() throws IOException 
    {
        if (!Files.exists(usersFile) || Files.size(usersFile) == 0) 
        {
            return new ArrayList<>();
        }

        // NOTE TO SELF:
        // Parse users.json into List<UserRecord> once JSON
        // library is available.
        throw new UnsupportedOperationException("JSON parsing not implemented yet (loadAll)");
    }

    /**
     * Saves all users to the JSON file safely (atomic write).
     *
     * NOTE TO SELF:
     * Implement JSON serialization here using Gson or Jackson.
     */
    private void atomicSaveAll(List<UserRecord> users) throws IOException 
    {
        //Validate input
        Objects.requireNonNull(users, "users");

        //Ensure parent directory exists
        Path dir = usersFile.getParent();
        if (dir != null) 
        {
            Files.createDirectories(dir);
        }

        //Temp file path
        Path tmp = usersFile.resolveSibling(usersFile.getFileName().toString() + ".tmp");

        // NOTE TO SELF:
        // Serialize `users` to JSON bytes and write to temp file.
        // After writing:
        // Files.move(tmp, usersFile,
        //     StandardCopyOption.REPLACE_EXISTING,
        //     StandardCopyOption.ATOMIC_MOVE);

        throw new UnsupportedOperationException("JSON serialization not implemented yet (atomicSaveAll)");
    }
}
