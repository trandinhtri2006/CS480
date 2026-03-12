package service;

import com.graphhopper.util.*;
import model.RouteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;

/**
 * Wraps GraphHopper for route calculation. Initialize once with an OSM file,
 * then call calculateRoute() as needed.
 */
public class RoutingService {

    private final GraphHopper hopper;
    private final Translation translation;

    /**
     * @param osmFile path to the .osm.pbf file
     * @param cacheDir directory for the GraphHopper routing cache
     */
    public RoutingService(String osmFile, String cacheDir) {
        hopper = new GraphHopper();
        hopper.setOSMFile(osmFile);
        hopper.setGraphHopperLocation(cacheDir);
        hopper.setEncodedValuesString("road_class");
        CustomModel customModel = new CustomModel();
        customModel.addToSpeed(Statement.If("true", Statement.Op.LIMIT, "100"));
        customModel.addToSpeed(Statement.If("road_class == FOOTWAY || road_class == PATH || road_class == STEPS || road_class == TRACK", Statement.Op.MULTIPLY, "0"));
        hopper.setProfiles(new Profile("car").setCustomModel(customModel).setWeighting("custom"));

        TranslationMap translationMap = new TranslationMap().doImport();
        translation = translationMap.getWithFallBack(Locale.of("en"));

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
            String fromAddress, String toAddress) throws Exception {
        GHRequest request = new GHRequest()
                .addPoint(new GHPoint(fromCoords[0], fromCoords[1]))
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
            new GeoPosition(toCoords[0], toCoords[1])
        };
        String[] markerAddresses = {fromAddress.trim(), toAddress.trim()};

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
     */
    public List<RouteResult> calculateRoutes(List<double[]> allCoords, List<String> allAddresses, String profile) throws Exception {
        if (allCoords.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 points for a route");
        }

        GHRequest request = new GHRequest();
        for (double[] c : allCoords) {
            request.addPoint(new GHPoint(c[0], c[1]));
        }
        request.setProfile(profile)
                .setLocale(Locale.US)
                .setAlgorithm("alternative_route"); // request multiple alternatives

        GHResponse response = hopper.route(request);
        if (response.hasErrors()) {
            throw new RuntimeException("Routing error: " + response.getErrors());
        }

        List<RouteResult> results = new ArrayList<>();
        for (ResponsePath path : response.getAll()) { // get all alternative routes
            List<GeoPosition> geoPath = new ArrayList<>();
            PointList points = path.getPoints();
            for (int i = 0; i < points.size(); i++) {
                geoPath.add(new GeoPosition(points.getLat(i), points.getLon(i)));
            }

            GeoPosition[] markers = new GeoPosition[allCoords.size()];
            String[] markerAddresses = new String[allAddresses.size()];
            for (int i = 0; i < allCoords.size(); i++) {
                markers[i] = new GeoPosition(allCoords.get(i)[0], allCoords.get(i)[1]);
                markerAddresses[i] = allAddresses.get(i).trim();
            }

            results.add(new RouteResult(
                    geoPath,
                    markers,
                    markerAddresses,
                    path.getInstructions(),
                    path.getDistance(),
                    path.getTime()
            ));
        }
        return results;
    }

    public Translation getTranslation() {
        return translation;
    }
}
