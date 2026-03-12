package model;

import com.graphhopper.util.Instruction;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.List;

/**
 * Holds the result of a route calculation: the polyline path, waypoint markers,
 * turn-by-turn instructions, total distance, and total time.
 */
public class RouteResult {

    private final List<GeoPosition> path;
    private final GeoPosition[] markers;
    private final String[] markerAddresses;
    private final List<Instruction> instructions;
    private final double distanceMeters;
    private final long timeMillis;

    public RouteResult(List<GeoPosition> path, GeoPosition[] markers, String[] markerAddresses,
            List<Instruction> instructions, double distanceMeters, long timeMillis) {
        this.path = path;
        this.markers = markers;
        this.markerAddresses = markerAddresses;
        this.instructions = instructions;
        this.distanceMeters = distanceMeters;
        this.timeMillis = timeMillis;
    }

    public List<GeoPosition> getPath() {
        return path;
    }

    public GeoPosition[] getMarkers() {
        return markers;
    }

    public String[] getMarkerAddresses() {
        return markerAddresses;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public double getDistanceMiles() {
        return Math.round(distanceMeters / 1609.34 * 10.0) / 10.0;
    }

    public String getFormattedTime() {
        long totalMinutes = timeMillis / 60000;
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        }
        return (totalMinutes / 60) + " hr " + (totalMinutes % 60) + " min";
    }
}

