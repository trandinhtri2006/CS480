package service;

import db.SQLHandler;
import model.FavoriteRoute;
import model.FavoriteRouteSummary;

import java.sql.SQLException;
import java.util.List;

//favorite route length 
public class FavoriteService {

    private final SQLHandler sqlHandler;

    private static final int MAX_FAVORITES = 5;
    private static final int MAX_NAME_LENGTH = 255;

    public FavoriteService(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }

   //saves route after validating 
    public int saveFavorite(FavoriteRoute favoriteRoute) throws SQLException {

        if (favoriteRoute == null) {
            throw new IllegalArgumentException("Favorite route cannot be null.");
        }

        if (favoriteRoute.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (isBlank(favoriteRoute.getOriginAddress())) {
            throw new IllegalArgumentException("Origin address is required.");
        }

        if (isBlank(favoriteRoute.getDestinationAddress())) {
            throw new IllegalArgumentException("Destination address is required.");
        }

        int count = sqlHandler.getFavoriteCount(favoriteRoute.getUserId());

        if (count >= MAX_FAVORITES) {
            throw new IllegalArgumentException("Maximum 5 favorite routes allowed.");
        }

        String name = favoriteRoute.getFavoriteName();

        if (isBlank(name)) {
            name = "Route" + (count + 1);
        }

        name = name.trim();

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Favorite name cannot exceed 255 characters.");
        }

        favoriteRoute.setFavoriteName(name);

        return sqlHandler.insertFavoriteRoute(favoriteRoute);
    }

    //gets users favorites 
    public List<FavoriteRouteSummary> getFavoriteSummaries(int userId) throws SQLException {
        return sqlHandler.getFavoriteSummariesByUserId(userId);
    }

    //returns a full route
    public FavoriteRoute getFavoriteRoute(int routeId, int userId) throws SQLException {
        return sqlHandler.getFavoriteRouteById(routeId, userId);
    }

    //renames a route
    public boolean renameFavorite(int routeId, int userId, String newName) throws SQLException {

        if (isBlank(newName)) {
            throw new IllegalArgumentException("Favorite name cannot be empty.");
        }

        if (newName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Favorite name too long.");
        }

        return sqlHandler.renameFavorite(routeId, userId, newName.trim());
    }

    //deletes a favorite route 
    public boolean deleteFavorite(int routeId, int userId) throws SQLException {
        return sqlHandler.deleteFavorite(routeId, userId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
