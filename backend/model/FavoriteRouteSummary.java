package model;

//favorites list disply 
public class FavoriteRouteSummary {

    private int routeId;
    private String favoriteName;
    private String originAddress;
    private String destinationAddress;
    private int chosenRouteIndex;
    private String chosenOverviewPolyline;

    public FavoriteRouteSummary() {
    }

    public FavoriteRouteSummary(int routeId,String favoriteName, String originAddress, String destinationAddress,
     int chosenRouteIndex, String chosenOverviewPolyline) {
        this.routeId = routeId;
        this.favoriteName = favoriteName;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.chosenRouteIndex = chosenRouteIndex;
        this.chosenOverviewPolyline = chosenOverviewPolyline;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
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

    @Override
    public String toString() {
        return "FavoriteRouteSummary{" +
                "routeId=" + routeId +
                ", favoriteName='" + favoriteName + '\'' +
                ", originAddress='" + originAddress + '\'' +
                ", destinationAddress='" + destinationAddress + '\'' +
                ", chosenRouteIndex=" + chosenRouteIndex +
                '}';
    }
}
