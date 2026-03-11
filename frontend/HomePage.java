import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class HomePage extends JPanel {

    private App app;
    private JTextField originField;
    private JTextField destinationField;
    private List<JTextField> waypointFields = new ArrayList<>();
    private JPanel waypointPanel;
    private WebEngine webEngine;

    private static final boolean USE_GOOGLE_MAPS = false; // toggle Google Maps
    private static final int MAX_WAYPOINTS = 5;

    private static final int FIELD_WIDTH = 180;
    private static final int FIELD_HEIGHT = 25;

    public HomePage(App app) {
        this.app = app;
        setLayout(new BorderLayout());

        // --- JFXPanel for map ---
        JFXPanel fxPanel = new JFXPanel();

        // --- Overlay panel ---
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setOpaque(false);
        overlayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        overlayPanel.setMaximumSize(new Dimension(FIELD_WIDTH, Integer.MAX_VALUE));
        overlayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Fixed origin/destination panel ---
        JPanel fixedPanel = new JPanel();
        fixedPanel.setLayout(new BoxLayout(fixedPanel, BoxLayout.Y_AXIS));
        fixedPanel.setOpaque(false);

        originField = new JTextField();
        setFixedSize(originField);
        setPlaceholder(originField, "Origin");
        fixedPanel.add(originField);
        fixedPanel.add(Box.createVerticalStrut(5));

        destinationField = new JTextField();
        setFixedSize(destinationField);
        setPlaceholder(destinationField, "Destination");
        fixedPanel.add(destinationField);
        fixedPanel.add(Box.createVerticalStrut(5));

        overlayPanel.add(fixedPanel);

        // --- Waypoint panel (dynamic) ---
        waypointPanel = new JPanel();
        waypointPanel.setLayout(new BoxLayout(waypointPanel, BoxLayout.Y_AXIS));
        waypointPanel.setOpaque(false);
        overlayPanel.add(waypointPanel);

        // --- Buttons panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

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

        overlayPanel.add(Box.createVerticalStrut(10));
        overlayPanel.add(buttonPanel);

        // --- LayeredPane to overlay inputs on map ---
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // use absolute positioning
        layeredPane.add(fxPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
        add(layeredPane, BorderLayout.CENTER);

        // --- Resize listener for fullscreen support ---
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                Dimension size = getSize();
                fxPanel.setBounds(0, 0, size.width, size.height);
                overlayPanel.setBounds(size.width - FIELD_WIDTH - 20, 10, FIELD_WIDTH, size.height - 20);
            }
        });

        // --- Initialize map ---
        new Thread(() -> {
            double[] coords;
            try {
                coords = GetUserLocation.getCoordinates(); // get user location
            } catch (Exception e) {
                coords = new double[]{46.9965, -120.5478, 0}; // fallback
            }
            double lat = coords[0];
            double lon = coords[1];

            Platform.runLater(() -> {
                WebView webView = new WebView();
                webEngine = webView.getEngine();
                fxPanel.setScene(new Scene(webView));

                // Build HTML with user location as center
                String html = buildMapHTML(lat, lon);
                webEngine.loadContent(html);
            });
        }).start();
    }

    // --- Add a waypoint dynamically ---
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

        // Also remove the vertical strut above it
        if (waypointPanel.getComponentCount() > 0) {
            waypointPanel.remove(waypointPanel.getComponentCount() - 1);
        }

        waypointPanel.revalidate();
        waypointPanel.repaint();
    }

    // --- Helpers for fixed size ---
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

    private String buildMapHTML(double lat, double lon) {
        if (USE_GOOGLE_MAPS) {
            return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
              <style>
                #map { height: 100%%; width: 100%%; }
                html, body { height: 100%%; margin: 0; padding: 0; }
              </style>
              <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places"></script>
              <script>
                let map, directionsService, directionsRenderer;
                function initMap() {
                  const userLoc = { lat: %f, lng: %f }; // user location
                  map = new google.maps.Map(document.getElementById('map'), { center: userLoc, zoom: 14 });
                  directionsService = new google.maps.DirectionsService();
                  directionsRenderer = new google.maps.DirectionsRenderer();
                  directionsRenderer.setMap(map);
                }
                function calculateRouteJS(origin, destination, waypoints) {
                  let wp = [];
                  if (waypoints && waypoints.length > 0) {
                    for (let i=0; i<waypoints.length; i++) {
                      if(waypoints[i].trim()!=='') wp.push({ location: waypoints[i], stopover: true });
                    }
                  }
                  directionsService.route({
                    origin: origin,
                    destination: destination,
                    waypoints: wp,
                    travelMode: google.maps.TravelMode.DRIVING
                  }, (result, status) => {
                    if(status==='OK') directionsRenderer.setDirections(result);
                    else alert('Route error: '+status);
                  });
                }
              </script>
            </head>
            <body onload="initMap()"><div id="map"></div></body>
            </html>
        """.formatted(lat, lon);
        } else {
            // Leaflet fallback for testing
            return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8" />
              <title>Test Map</title>
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
              <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
              <style>html, body, #map { height: 100%%; margin:0; padding:0; }</style>
            </head>
            <body>
              <div id="map"></div>
              <script>
                var map = L.map('map').setView([%f, %f], 13);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  attribution: '&copy; OpenStreetMap contributors'
                }).addTo(map);
                function calculateRouteJS(origin, destination, waypoints) {
                  alert('Routing with waypoints not implemented in Leaflet test mode.');
                }
              </script>
            </body>
            </html>
        """.formatted(lat, lon);
        }
    }

    // --- Call JS to calculate route ---
    private void calculateRoute() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();

        if (origin.isEmpty() || origin.equals("Origin") || destination.isEmpty() || destination.equals("Destination")) {
            JOptionPane.showMessageDialog(this, "Please enter both origin and destination.");
            return;
        }

        List<String> waypoints = new ArrayList<>();
        for (int i = 0; i < waypointFields.size(); i++) {
            String wp = waypointFields.get(i).getText().trim();
            if (!wp.isEmpty() && !wp.equals("Waypoint " + (i + 1))) waypoints.add(wp);
        }

        Platform.runLater(() -> {
            StringBuilder wpJS = new StringBuilder("[");
            for (int i = 0; i < waypoints.size(); i++) {
                wpJS.append("'").append(waypoints.get(i).replace("'", "\\'")).append("'");
                if (i < waypoints.size() - 1) wpJS.append(",");
            }
            wpJS.append("]");

            String js = String.format("calculateRouteJS('%s','%s',%s);",
                    origin.replace("'", "\\'"),
                    destination.replace("'", "\\'"),
                    wpJS.toString());
            webEngine.executeScript(js);
        });
    }
}