package service;

<<<<<<< Updated upstream
=======
//Import Statements
>>>>>>> Stashed changes
import model.RouteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

<<<<<<< Updated upstream
import org.jxmapviewer.viewer.GeoPosition;

=======
// JXMapViewer
import org.jxmapviewer.viewer.GeoPosition;

// GraphHopper
>>>>>>> Stashed changes
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

/**
 * Wraps GraphHopper for route calculation. Initialize once with an OSM file,
 * then call calculateRoute() as needed.
 */
public class RoutingService {

    private final GraphHopper hopper;

    /**
     * @param osmFile path to the .osm.pbf file
     * @param cacheDir directory for the GraphHopper routing cache
     */
<<<<<<< Updated upstream
    public RoutingService(String osmFile, String cacheDir) {
=======
    public RoutingService(String osmFile, String cacheDir) 
    {
>>>>>>> Stashed changes
        hopper = new GraphHopper();
        hopper.setOSMFile(osmFile);
        hopper.setGraphHopperLocation(cacheDir);
        hopper.setEncodedValuesString("road_class");
        CustomModel customModel = new CustomModel();
        customModel.addToSpeed(Statement.If("true", Statement.Op.LIMIT, "100"));
        customModel.addToSpeed(Statement.If("road_class == FOOTWAY || road_class == PATH || road_class == STEPS || road_class == TRACK", Statement.Op.MULTIPLY, "0"));
        hopper.setProfiles(new Profile("car").setCustomModel(customModel).setWeighting("custom"));

        System.out.println("Importing map data... This may take a moment.");
        hopper.importOrLoad();
        System.out.println("Map data loaded!");
    }

    /**
     * Calculate a route between two addresses.
     *
     * @return RouteResult with path, markers, instructions, distance, time
     */
    public RouteResult calculateRoute(double[] fromCoords, double[] toCoords,
<<<<<<< Updated upstream
            String fromAddress, String toAddress) throws Exception {
=======
            String fromAddress, String toAddress) throws Exception 
            {
>>>>>>> Stashed changes
        GHRequest request = new GHRequest()
                .addPoint(new GHPoint(fromCoords[0], fromCoords[1]))
                .addPoint(new GHPoint(toCoords[0], toCoords[1]))
                .setProfile("car")
                .setLocale(Locale.US);

        GHResponse response = hopper.route(request);
<<<<<<< Updated upstream
        if (response.hasErrors()) {
            throw new RuntimeException("Routing error: " + response.getErrors());
        }

        ResponsePath best = response.getBest();
        PointList points = best.getPoints();

        List<GeoPosition> path = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            path.add(new GeoPosition(points.getLat(i), points.getLon(i)));
        }

=======
        if (response.hasErrors()) 
        {
            throw new RuntimeException("Routing error: " + response.getErrors());
        }

        // Extract the best path and convert to RouteResult
        ResponsePath best = response.getBest();
        PointList points = best.getPoints();

        // Convert PointList to List<GeoPosition> for the path
        List<GeoPosition> path = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) 
        {
            path.add(new GeoPosition(points.getLat(i), points.getLon(i)));
        }

        // Markers for start and end points
>>>>>>> Stashed changes
        GeoPosition[] markers = {
            new GeoPosition(fromCoords[0], fromCoords[1]),
            new GeoPosition(toCoords[0], toCoords[1])
        };
        String[] markerAddresses = {fromAddress.trim(), toAddress.trim()};

<<<<<<< Updated upstream
=======
        // Convert turn-by-turn instructions to a List<Instruction>
>>>>>>> Stashed changes
        List<Instruction> instructions = new ArrayList<>();
        for (Instruction instr : best.getInstructions()) {
            instructions.add(instr);
        }

        return new RouteResult(path, markers, markerAddresses, instructions,
                best.getDistance(), best.getTime());
    }

    /**
     * Calculate a route with an intermediate waypoint.
     */
    public RouteResult calculateRoute(double[] fromCoords, double[] waypointCoords, double[] toCoords,
            String fromAddress, String waypointAddress, String toAddress) throws Exception {
        GHRequest request = new GHRequest()
                .addPoint(new GHPoint(fromCoords[0], fromCoords[1]))
                .addPoint(new GHPoint(waypointCoords[0], waypointCoords[1]))
                .addPoint(new GHPoint(toCoords[0], toCoords[1]))
                .setProfile("car")
                .setLocale(Locale.US);

        GHResponse response = hopper.route(request);
        if (response.hasErrors()) {
            throw new RuntimeException("Routing error: " + response.getErrors());
        }

        ResponsePath best = response.getBest();
        PointList points = best.getPoints();

        List<GeoPosition> path = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            path.add(new GeoPosition(points.getLat(i), points.getLon(i)));
        }

        GeoPosition[] markers = {
            new GeoPosition(fromCoords[0], fromCoords[1]),
            new GeoPosition(waypointCoords[0], waypointCoords[1]),
            new GeoPosition(toCoords[0], toCoords[1])
        };
        String[] markerAddresses = {fromAddress.trim(), waypointAddress.trim(), toAddress.trim()};

        List<Instruction> instructions = new ArrayList<>();
        for (Instruction instr : best.getInstructions()) {
            instructions.add(instr);
        }

        return new RouteResult(path, markers, markerAddresses, instructions,
                best.getDistance(), best.getTime());
    }

    /**
     * Calculate a route through any number of points (2 or more).
     *
     * @param allCoords list of [lat, lon] arrays in order
     * @param allAddresses list of address strings in the same order
     */
    public RouteResult calculateRoute(List<double[]> allCoords, List<String> allAddresses) throws Exception {
        if (allCoords.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 points for a route");
        }

        GHRequest request = new GHRequest();
        for (double[] c : allCoords) {
            request.addPoint(new GHPoint(c[0], c[1]));
        }
        request.setProfile("car").setLocale(Locale.US);

        GHResponse response = hopper.route(request);
        if (response.hasErrors()) {
            throw new RuntimeException("Routing error: " + response.getErrors());
        }

        ResponsePath best = response.getBest();
        PointList points = best.getPoints();

        List<GeoPosition> path = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            path.add(new GeoPosition(points.getLat(i), points.getLon(i)));
        }

        GeoPosition[] markers = new GeoPosition[allCoords.size()];
        String[] markerAddresses = new String[allAddresses.size()];
        for (int i = 0; i < allCoords.size(); i++) {
            markers[i] = new GeoPosition(allCoords.get(i)[0], allCoords.get(i)[1]);
            markerAddresses[i] = allAddresses.get(i).trim();
        }

        List<Instruction> instructions = new ArrayList<>();
        for (Instruction instr : best.getInstructions()) {
            instructions.add(instr);
        }

        return new RouteResult(path, markers, markerAddresses, instructions,
                best.getDistance(), best.getTime());
    }
}
