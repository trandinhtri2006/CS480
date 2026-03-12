import model.RouteResult;
import model.User;
import service.FavoriteService;
import service.GeocodingService;
import service.RoutingService;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.painter.Painter;

import com.graphhopper.util.Instruction;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Home page UI with map, search, dynamic waypoints, and routing.
 */
public class HomePage extends JPanel {

    private JXMapViewer mapViewer;
    private JTextField originField;
    private JTextField destinationField;
    private List<JTextField> waypointFields = new ArrayList<>();
    private JPanel waypointPanel;

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

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(buttonPanel);

        add(topPanel, BorderLayout.WEST);

        // --- Map setup ---
        TileFactoryInfo info = new org.jxmapviewer.OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(4);
        mapViewer.setAddressLocation(new GeoPosition(47.6062, -122.3321)); // Default center

        add(new JScrollPane(mapViewer), BorderLayout.CENTER);
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

    // --- Calculate route ---
    private void calculateRoute() {
        try {
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();

            if (origin.isEmpty() || origin.equals("Origin") ||
                    destination.isEmpty() || destination.equals("Destination")) {
                JOptionPane.showMessageDialog(this, "Please enter both origin and destination.");
                return;
            }

            List<GeoPosition> coordsList = new ArrayList<>();

            double[] originCoords = geocodingService.geocode(origin);
            coordsList.add(new GeoPosition(originCoords[0], originCoords[1]));

            for (JTextField wpField : waypointFields) {
                String wpText = wpField.getText().trim();
                if (!wpText.isEmpty() && !wpText.startsWith("Waypoint")) {
                    double[] wpCoords = geocodingService.geocode(wpText);
                    coordsList.add(new GeoPosition(wpCoords[0], wpCoords[1]));
                }
            }

            double[] destCoords = geocodingService.geocode(destination);
            coordsList.add(new GeoPosition(destCoords[0], destCoords[1]));

            RouteResult result = routingService.calculateRoute(coordsList);

            // Draw route
            RoutePainter routePainter = new RoutePainter(result.getPath());
            mapViewer.setOverlayPainter(routePainter);
            mapViewer.setAddressLocation(result.getPath().get(0));

            // Show instructions
            Translation tr = TranslationMap.t().getWithFallBack("en");
            StringBuilder sb = new StringBuilder();
            for (Instruction instr : result.getInstructions()) {
                sb.append(instr.getTurnDescription(tr)).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Directions", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to calculate route:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}