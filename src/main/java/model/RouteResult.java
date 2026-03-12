package model;

<<<<<<< Updated upstream
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

    /**
     * Distance in miles, rounded to 1 decimal.
     */
    public double getDistanceMiles() {
        return Math.round(distanceMeters / 1609.34 * 10.0) / 10.0;
    }

    /**
     * Formatted time string like "12 min" or "1 hr 5 min".
     */
    public String getFormattedTime() {
        long totalMinutes = timeMillis / 60000;
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        }
        return (totalMinutes / 60) + " hr " + (totalMinutes % 60) + " min";
=======
import org.jxmapviewer.viewer.GeoPosition;
import com.graphhopper.util.Instruction;
import java.util.List;

/**
 * Holds everything the UI needs after a route is calculated:
 * the path to draw, marker positions, turn-by-turn text,
 * and total distance / time.
 */
public class RouteResult {

    private final List<GeoPosition>  path;             // polyline points
    private final GeoPosition[]      markers;          // start, waypoints, end
    private final String[]           markerAddresses;  // human-readable addresses
    private final List<Instruction>  instructions;     // turn-by-turn (GraphHopper)
    private final double             distanceMeters;
    private final long               timeMs;

    public RouteResult(List<GeoPosition> path,
                       GeoPosition[] markers,
                       String[] markerAddresses,
                       List<Instruction> instructions,
                       double distanceMeters,
                       long timeMs) {
        this.path            = path;
        this.markers         = markers;
        this.markerAddresses = markerAddresses;
        this.instructions    = instructions;
        this.distanceMeters  = distanceMeters;
        this.timeMs          = timeMs;
    }

    /* ---- getters ---- */
    public List<GeoPosition>  getPath()            { return path; }
    public GeoPosition[]      getMarkers()         { return markers; }
    public String[]           getMarkerAddresses() { return markerAddresses; }
    public List<Instruction>  getInstructions()    { return instructions; }
    public double             getDistanceMeters()  { return distanceMeters; }
    public long               getTimeMs()          { return timeMs; }

    /* ---- convenience ---- */
    public double getDistanceKm() {
        return distanceMeters / 1000.0;
    }

    public double getDistanceMiles() {
        return getDistanceKm() * 0.621371;
    }

    public String getFormattedTime() {
        long totalSec = timeMs / 1000;
        long hrs  = totalSec / 3600;
        long mins = (totalSec % 3600) / 60;
        if (hrs > 0) return String.format("%d hr %d min", hrs, mins);
        return String.format("%d min", mins);
>>>>>>> Stashed changes
    }
}

