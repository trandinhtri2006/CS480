
package model;

import java.util.ArrayList;
import java.util.List;

//full saved route including waypoints 

public class FavoriteRoute {

    private int routeId;
    private int userId;
    private String favoriteName;
    private String originAddress;
    private String destinationAddress;
    private int chosenRouteIndex;
    private String chosenOverviewPolyline;
    private List<String> waypoints;

    public FavoriteRoute() {
        this.waypoints = new ArrayList<>();
    }

    public FavoriteRoute(int routeId, int userId, String favoriteName, String originAddress,
        String destinationAddress, int chosenRouteIndex, String chosenOverviewPolyline, List<String> waypoints) {
        this.routeId = routeId;
        this.userId = userId;
        this.favoriteName = favoriteName;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.chosenRouteIndex = chosenRouteIndex;
        this.chosenOverviewPolyline = chosenOverviewPolyline;
        this.waypoints = waypoints != null ? waypoints : new ArrayList<>();
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getChosenRouteIndex() {
        return chosenRouteIndex;
    }

    public void setChosenRouteIndex(int chosenRouteIndex) {
        this.chosenRouteIndex = chosenRouteIndex;
    }

    public String getChosenOverviewPolyline() {
        return chosenOverviewPolyline;
    }

    public void setChosenOverviewPolyline(String chosenOverviewPolyline) {
        this.chosenOverviewPolyline = chosenOverviewPolyline;
    }

    public List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String> waypoints) {
        this.waypoints = waypoints != null ? waypoints : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "FavoriteRoute{" +
                "routeId=" + routeId +
                ", userId=" + userId +
                ", favoriteName='" + favoriteName + '\'' +
                ", originAddress='" + originAddress + '\'' +
                ", destinationAddress='" + destinationAddress + '\'' +
                ", chosenRouteIndex=" + chosenRouteIndex +
                ", waypoints=" + waypoints +
                '}';
    }
}