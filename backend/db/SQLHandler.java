
package db;

import model.FavoriteRoute;
import model.FavoriteRouteSummary;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLHandler
 *
 * Handles all SQLite database access for the application
 * Services should call this class instead of writing raw SQL 
 */
public class SQLHandler {

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:backend/db/mapapp.db";

    private final String dbUrl;

    public SQLHandler() {
        this(DEFAULT_DB_URL);
    }

    public SQLHandler(String dbUrl) {
        this.dbUrl = dbUrl;
    }

   //creates a writes a new database connection
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(dbUrl);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }

        return connection;
    }

    //starts the database by executing schema.sql
    //calls once when the application starts 
    public void initializeDatabase() throws SQLException, IOException {
        String schemaSql = loadSchemaSql();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            for (String sqlStatement : splitSqlStatements(schemaSql)) {
                if (!sqlStatement.trim().isEmpty()) {
                    statement.execute(sqlStatement);
                }
            }
        }
    }


 //Loads schema.sql from the backend/db folder.
 
private String loadSchemaSql() throws IOException {
    Path schemaPath = Path.of("backend", "db", "schema.sql");
    return Files.readString(schemaPath, StandardCharsets.UTF_8);
}

    
     //Splits a SQL script into individual statements.
     
   private List<String> splitSqlStatements(String sqlScript) {
    List<String> statements = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inTrigger = false;

    String[] lines = sqlScript.split("\\R");

    for (String line : lines) {
        String trimmed = line.trim();

        if (trimmed.isEmpty() || trimmed.startsWith("--")) {
            continue;
        }

        if (trimmed.toUpperCase().startsWith("CREATE TRIGGER")) {
            inTrigger = true;
        }

        current.append(line).append('\n');

        if (inTrigger) {
            if (trimmed.equalsIgnoreCase("END;")) {
                statements.add(current.toString().trim());
                current.setLength(0);
                inTrigger = false;
            }
        } else {
            if (trimmed.endsWith(";")) {
                statements.add(current.toString().trim());
                current.setLength(0);
            }
        }
    }

    if (!current.isEmpty()) {
        statements.add(current.toString().trim());
    }

    return statements;
}

    private String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }


    // USER METHODS

   //retrns true if an email already exists 

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    
     // Creates a user and returns the generated user_id.
     
    public int createUser(String email, String passwordHash, String passwordSalt) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, password_salt) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, passwordSalt);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating user failed; no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException("Creating user failed; no generated key returned.");
        }
    }

    
    
    //returns the user 

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, email, password_hash, password_salt FROM users WHERE email = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setPasswordSalt(rs.getString("password_salt"));
                return user;
            }
        }
    }

    //update password 

    public boolean updatePassword(String email, String newPasswordHash, String newPasswordSalt) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE email = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setString(2, newPasswordSalt);
            ps.setString(3, email);

            return ps.executeUpdate() == 1;
        }
    }

    
    // FAVORITES METHODS 

    
     //Returns how many favrites a user currently has
    
    public int getFavoriteCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) AS favorite_count FROM favorite_routes WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("favorite_count");
                }
                return 0;
            }
        }
    }

    
     //Inserts a favorite route and its waypoints 
     // Returns the routeid 
    
    public int insertFavoriteRoute(FavoriteRoute favoriteRoute) throws SQLException {
        String insertRouteSql = """
                INSERT INTO favorite_routes
                (user_id, favorite_name, origin_address, destination_address, chosen_route_index, chosen_overview_polyline)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertWaypointSql = """
                INSERT INTO route_waypoints
                (route_id, stop_order, waypoint_address)
                VALUES (?, ?, ?)
                """;

        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int generatedRouteId;

            try (PreparedStatement routePs = connection.prepareStatement(insertRouteSql, Statement.RETURN_GENERATED_KEYS)) {
                routePs.setInt(1, favoriteRoute.getUserId());
                routePs.setString(2, favoriteRoute.getFavoriteName());
                routePs.setString(3, favoriteRoute.getOriginAddress());
                routePs.setString(4, favoriteRoute.getDestinationAddress());
                routePs.setInt(5, favoriteRoute.getChosenRouteIndex());
                routePs.setString(6, favoriteRoute.getChosenOverviewPolyline());

                int rowsAffected = routePs.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insert favorite route failed; no rows affected.");
                }

                try (ResultSet generatedKeys = routePs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedRouteId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Insert favorite route failed; no generated route_id returned.");
                    }
                }
            }

            List<String> waypoints = favoriteRoute.getWaypoints();
            if (waypoints != null && !waypoints.isEmpty()) {
                try (PreparedStatement waypointPs = connection.prepareStatement(insertWaypointSql)) {
                    for (int i = 0; i < waypoints.size(); i++) {
                        waypointPs.setInt(1, generatedRouteId);
                        waypointPs.setInt(2, i + 1);
                        waypointPs.setString(3, waypoints.get(i));
                        waypointPs.addBatch();
                    }
                    waypointPs.executeBatch();
                }
            }

            connection.commit();
            return generatedRouteId;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.addSuppressed(e);
                    throw rollbackEx;
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    //returns route summary 

    public List<FavoriteRouteSummary> getFavoriteSummariesByUserId(int userId) throws SQLException {
        String sql = """
                SELECT route_id, favorite_name, origin_address, destination_address,
                       chosen_route_index, chosen_overview_polyline
                FROM favorite_routes
                WHERE user_id = ?
                ORDER BY route_id ASC
                """;

        List<FavoriteRouteSummary> summaries = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FavoriteRouteSummary summary = new FavoriteRouteSummary();
                    summary.setRouteId(rs.getInt("route_id"));
                    summary.setFavoriteName(rs.getString("favorite_name"));
                    summary.setOriginAddress(rs.getString("origin_address"));
                    summary.setDestinationAddress(rs.getString("destination_address"));
                    summary.setChosenRouteIndex(rs.getInt("chosen_route_index"));
                    summary.setChosenOverviewPolyline(rs.getString("chosen_overview_polyline"));
                    summaries.add(summary);
                }
            }
        }

        return summaries;
    }

    //returns favorite route by user using only routeid and userid 

    public FavoriteRoute getFavoriteRouteById(int routeId, int userId) throws SQLException {
        String sql = """
                SELECT route_id, user_id, favorite_name, origin_address, destination_address,
                       chosen_route_index, chosen_overview_polyline
                FROM favorite_routes
                WHERE route_id = ? AND user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, routeId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                FavoriteRoute favoriteRoute = new FavoriteRoute();
                favoriteRoute.setRouteId(rs.getInt("route_id"));
                favoriteRoute.setUserId(rs.getInt("user_id"));
                favoriteRoute.setFavoriteName(rs.getString("favorite_name"));
                favoriteRoute.setOriginAddress(rs.getString("origin_address"));
                favoriteRoute.setDestinationAddress(rs.getString("destination_address"));
                favoriteRoute.setChosenRouteIndex(rs.getInt("chosen_route_index"));
                favoriteRoute.setChosenOverviewPolyline(rs.getString("chosen_overview_polyline"));
                favoriteRoute.setWaypoints(getWaypointsByRouteId(routeId));

                return favoriteRoute;
            }
        }
    }

    //returns waypoints in order

    public List<String> getWaypointsByRouteId(int routeId) throws SQLException {
        String sql = """
                SELECT waypoint_address
                FROM route_waypoints
                WHERE route_id = ?
                ORDER BY stop_order ASC
                """;

        List<String> waypoints = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, routeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    waypoints.add(rs.getString("waypoint_address"));
                }
            }
        }

        return waypoints;
    }

    //renames a favorite route 

    public boolean renameFavorite(int routeId, int userId, String newFavoriteName) throws SQLException {
        String sql = """
                UPDATE favorite_routes
                SET favorite_name = ?
                WHERE route_id = ? AND user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newFavoriteName);
            ps.setInt(2, routeId);
            ps.setInt(3, userId);

            return ps.executeUpdate() == 1;
        }
    }

    
      //Deletes a favorite route for a user
      //Waypoints are also removed 
     
    public boolean deleteFavorite(int routeId, int userId) throws SQLException {
        String sql = "DELETE FROM favorite_routes WHERE route_id = ? AND user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, routeId);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        }
    }
}