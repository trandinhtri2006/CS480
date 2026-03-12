import model.RouteResult;
import model.User;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import service.FavoriteService;
import service.GeocodingService;
import service.RoutingService;

import org.jxmapviewer.JXMapViewer;
import com.graphhopper.util.Instruction;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Home page UI with map, search, dynamic waypoints, and routing.
 */
public class HomePage extends JPanel {

    private JXMapViewer mapViewer;
    private JTextField originField;
    private JTextField destinationField;
    private List<JTextField> waypointFields = new ArrayList<>();
    private JPanel waypointPanel;
    private JTextArea directionsArea;
    private JLabel distanceLabel;
    private JLabel timeLabel;
    private JList<String> routeList;
    private DefaultListModel<String> routeListModel;
    private List<RouteResult> currentRoutes;

    private RoutingService routingService;
    private GeocodingService geocodingService;
    private FavoriteService favoriteService;
    private User currentUser;

    private static final int FIELD_WIDTH = 180;
    private static final int FIELD_HEIGHT = 25;
    private static final int MAX_WAYPOINTS = 5;

    public HomePage(RoutingService routingService,
                    GeocodingService geocodingService,
                    FavoriteService favoriteService,
                    User currentUser) {

        this.routingService = routingService;
        this.geocodingService = geocodingService;
        this.favoriteService = favoriteService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());

        // --- Top panel with BoxLayout for inputs and buttons ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fixed origin/destination fields
        originField = new JTextField();
        setFixedSize(originField);
        setPlaceholder(originField, "Origin");
        topPanel.add(originField);
        topPanel.add(Box.createVerticalStrut(5));

        destinationField = new JTextField();
        setFixedSize(destinationField);
        setPlaceholder(destinationField, "Destination");
        topPanel.add(destinationField);
        topPanel.add(Box.createVerticalStrut(5));

        // Dynamic waypoint panel
        waypointPanel = new JPanel();
        waypointPanel.setLayout(new BoxLayout(waypointPanel, BoxLayout.Y_AXIS));
        topPanel.add(waypointPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addWaypointButton = new JButton("Add Waypoint");
        setFixedButtonSize(addWaypointButton);
        addWaypointButton.addActionListener(e -> addWaypoint());
        buttonPanel.add(addWaypointButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton removeWaypointButton = new JButton("Remove Waypoint");
        setFixedButtonSize(removeWaypointButton);
        removeWaypointButton.addActionListener(e -> removeWaypoint());
        buttonPanel.add(removeWaypointButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton routeButton = new JButton("Get Route");
        setFixedButtonSize(routeButton);
        routeButton.addActionListener(e -> calculateRoute());
        buttonPanel.add(routeButton);

        // Panel under buttons to show multiple route options
        routeListModel = new DefaultListModel<>();
        routeList = new JList<>(routeListModel);
        routeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Scrollable box for route previews
        JScrollPane routeScroll = new JScrollPane(routeList);
        routeScroll.setPreferredSize(new Dimension(400, 150));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(new JLabel("Available Routes:"));
        topPanel.add(routeScroll);

        routeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && routeList.getSelectedIndex() >= 0) {
                RouteResult selected = currentRoutes.get(routeList.getSelectedIndex());
                drawRoute(selected);
            }
        });

        distanceLabel = new JLabel("Distance: ");
        timeLabel = new JLabel("Time: ");

        directionsArea = new JTextArea(8, 40);
        directionsArea.setEditable(false);
        directionsArea.setLineWrap(true);
        directionsArea.setWrapStyleWord(true);

        JScrollPane directionsScroll = new JScrollPane(directionsArea);
        directionsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel routeInfoPanel = new JPanel();
        routeInfoPanel.setLayout(new BorderLayout());

        JPanel summaryPanel = new JPanel(new GridLayout(2,1));
        summaryPanel.add(distanceLabel);
        summaryPanel.add(timeLabel);

        routeInfoPanel.add(summaryPanel, BorderLayout.NORTH);
        routeInfoPanel.add(directionsScroll, BorderLayout.CENTER);


        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(buttonPanel);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(routeInfoPanel);

        add(topPanel, BorderLayout.WEST);
        mapViewer = new JXMapViewer();

        TileFactoryInfo info = new TileFactoryInfo(
                0, 19, 19,
                256,
                true, true,
                "https://tile.openstreetmap.org",
                "x", "y", "z"
        ) {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                int z = 19 - zoom;
                return this.baseURL + "/" + z + "/" + x + "/" + y + ".png";
            }
        };

        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        GeoPosition start = new GeoPosition(47.018077, -120.538130);
        mapViewer.setAddressLocation(start);
        mapViewer.setZoom(4);

        add(mapViewer, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Add waypoint dynamically ---
    private void addWaypoint() {
        if (waypointFields.size() >= MAX_WAYPOINTS) {
            JOptionPane.showMessageDialog(this, "Maximum of " + MAX_WAYPOINTS + " waypoints reached.");
            return;
        }

        int wpNum = waypointFields.size() + 1;
        JTextField wpField = new JTextField();
        setFixedSize(wpField);
        setPlaceholder(wpField, "Waypoint " + wpNum);
        waypointFields.add(wpField);

        waypointPanel.add(Box.createVerticalStrut(5));
        waypointPanel.add(wpField);
        wpField.setAlignmentX(Component.LEFT_ALIGNMENT);

        waypointPanel.revalidate();
        waypointPanel.repaint();
    }

    // --- Remove last waypoint dynamically ---
    private void removeWaypoint() {
        if (waypointFields.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No waypoints to remove.");
            return;
        }

        JTextField lastWp = waypointFields.remove(waypointFields.size() - 1);
        waypointPanel.remove(lastWp);

        if (waypointPanel.getComponentCount() > 0) {
            waypointPanel.remove(waypointPanel.getComponentCount() - 1); // remove strut
        }

        waypointPanel.revalidate();
        waypointPanel.repaint();
    }

    // --- Helpers for fixed size and placeholders ---
    private void setFixedSize(JTextField field) {
        Dimension size = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);
        field.setPreferredSize(size);
        field.setMaximumSize(size);
        field.setMinimumSize(size);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void setFixedButtonSize(JButton button) {
        Dimension size = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void setPlaceholder(JTextField field, String text) {
        field.setForeground(Color.GRAY);
        field.setText(text);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(text);
                }
            }
        });
    }

//    private void drawRoute(RouteResult result) {
//        List<GeoPosition> track = result.getPath();
//
//        if (track == null || track.isEmpty()) {
//            return;
//        }
//
//        // Center the map on the route
//        mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);
//
//        // Create route painter
//        RoutePainter routePainter = new RoutePainter(track);
//
//        // Optional: add start/end markers
//        Set<GeoPosition> markers = new HashSet<>();
//        markers.add(track.get(0)); // start
//        markers.add(track.get(track.size() - 1)); // end
//
//        Set<Waypoint> waypoints = new HashSet<>();
//        waypoints.add(new DefaultWaypoint(track.get(0))); // start
//        waypoints.add(new DefaultWaypoint(track.get(track.size() - 1))); // end
//
//        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
//        waypointPainter.setWaypoints(waypoints);
//        waypointPainter.setRenderer((g, map, wp) -> {
//            Point2D point = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
//            g.setColor(Color.BLUE);
//            g.fillOval((int) point.getX() - 5, (int) point.getY() - 5, 10, 10);
//        });
//
//        // Combine painters
//        CompoundPainter<JXMapViewer> painters = new CompoundPainter<>();
//        painters.setPainters(routePainter, waypointPainter);
//        mapViewer.setOverlayPainter(painters);
//        mapViewer.repaint();
//    }


    private void drawRoute(RouteResult result) {

        List<GeoPosition> track = result.getPath();

        if (track == null || track.isEmpty()) {
            return;
        }

        // --- Route line ---
        RoutePainter routePainter = new RoutePainter(track);

        // --- Waypoint dots ---
        Set<Waypoint> waypoints = new HashSet<>();
        GeoPosition[] markers = result.getMarkers();

        for (GeoPosition pos : markers) {
            waypoints.add(new DefaultWaypoint(pos));
        }

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);

        waypointPainter.setRenderer((g, map, wp) -> {

            Point2D point = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            Rectangle viewport = map.getViewportBounds();

            int x = (int) (point.getX() - viewport.getX());
            int y = (int) (point.getY() - viewport.getY());

            int index = track.indexOf(wp.getPosition());

            if (index == 0) {
                g.setColor(Color.GREEN); // start
            }
            else if (index == track.size() - 1) {
                g.setColor(Color.RED); // destination
            }
            else {
                g.setColor(Color.BLUE); // waypoint
            }

            g.fillOval(x - 7, y - 7, 14, 14);

            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(index), x - 3, y + 4);
        });

        // --- Combine painters ---
        CompoundPainter<JXMapViewer> painters = new CompoundPainter<>();
        painters.setPainters(routePainter, waypointPainter);
        mapViewer.setOverlayPainter(painters);

        // --- Center and fit map ---
        mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);
        mapViewer.repaint();


        StringBuilder directionsText = new StringBuilder();
        List<Instruction> instructions = result.getInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            Instruction instr = instructions.get(i);

            // Convert meters → miles
            double miles = instr.getDistance() / 1609.34;
            String distanceStr = String.format("%.2f mi", miles);

            // Get turn description (if null, fallback to "continue")
            String turn = instr.getTurnDescription(routingService.getTranslation());
            if (turn == null || turn.isBlank()) {
                turn = "continue";
            }

            // Append step number, turn, and distance
            directionsText.append(i + 1)
                    .append(". ")
                    .append(turn)
                    .append(" (")
                    .append(distanceStr)
                    .append(")")
                    .append("\n");
        }

        // Set the text area
        directionsArea.setText(directionsText.toString());
        directionsArea.setCaretPosition(0); // scroll to top

        directionsArea.setText(directionsText.toString());

        distanceLabel.setText("Distance: " + result.getDistanceMiles() + " miles");
        timeLabel.setText("Time: " + result.getFormattedTime());

        JTextArea directionsArea = new JTextArea(directionsText.toString());
        directionsArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(directionsArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));
    }

    // --- Calculate route ---
    private void calculateRoute() {

        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();

        if (origin.isEmpty() || origin.equals("Origin") ||
                destination.isEmpty() || destination.equals("Destination")) {
            JOptionPane.showMessageDialog(this, "Please enter both origin and destination.");
            return;
        }

        List<String> allAddresses = new ArrayList<>();
        allAddresses.add(origin);

        for (JTextField wpField : waypointFields) {
            String wp = wpField.getText().trim();
            if (!wp.isEmpty() && !wp.startsWith("Waypoint")) {
                allAddresses.add(wp);
            }
        }

        allAddresses.add(destination);

        try {
            // Convert addresses → coordinates
            List<double[]> allCoords = geocodingService.getCoordinates(allAddresses);

            // Get multiple alternative routes
            currentRoutes = routingService.calculateRoutes(allCoords, allAddresses, "car");

            // Populate the route preview list
            routeListModel.clear();
            for (int i = 0; i < currentRoutes.size(); i++) {
                RouteResult r = currentRoutes.get(i);
                routeListModel.addElement("Route " + (i + 1) +
                        " — " + String.format("%.1f mi, %s", r.getDistanceMiles(), r.getFormattedTime()));
            }

            if (!currentRoutes.isEmpty()) {
                routeList.setSelectedIndex(0);
                drawRoute(currentRoutes.get(0)); // show first route by default
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Routing error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}