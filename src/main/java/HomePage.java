import model.FavoriteRoute;
import model.FavoriteRouteSummary;
import model.RouteResult;
import model.User;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;
import service.AuthService;
import service.FavoriteService;
import service.GeocodingService;
import service.RoutingService;

import org.jxmapviewer.JXMapViewer;
import com.graphhopper.util.Instruction;


import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    private GeoPosition[] currentMarkers;
    private String[] currentMarkerAddresses;

    private RoutingService routingService;
    private GeocodingService geocodingService;
    private FavoriteService favoriteService;
    private User currentUser;
    private App app;
    private AuthService authService;
    private BufferedImage backgroundImage;

    private static final int FIELD_WIDTH = 350;
    private static final int FIELD_HEIGHT = 25;
    private static final int MAX_WAYPOINTS = 5;

    public HomePage(App app, AuthService authService,
                    RoutingService routingService,
                    GeocodingService geocodingService,
                    FavoriteService favoriteService,
                    User currentUser) {

        this.routingService = routingService;
        this.geocodingService = geocodingService;
        this.favoriteService = favoriteService;
        this.currentUser = currentUser;
        this.app = app;
        this.authService = authService;

        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/Background/loginpageBG.jpg"));
        } catch (Exception ex) {
            backgroundImage = null;
        }
        setLayout(new BorderLayout());
        add(createMenuBar(), BorderLayout.NORTH);

        // Sidebar with background image
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    int pw = getWidth();
                    int ph = getHeight();
                    int iw = backgroundImage.getWidth();
                    int ih = backgroundImage.getHeight();
                    double scale = Math.max((double) pw / iw, (double) ph / ih);
                    int dw = (int) (iw * scale);
                    int dh = (int) (ih * scale);
                    int dx = (pw - dw) / 2;
                    int dy = (ph - dh) / 2;
                    g.drawImage(backgroundImage, dx, dy, dw, dh, this);
                }
            }
        };
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Origin and destination fields
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

        waypointPanel = new JPanel();
        waypointPanel.setLayout(new BoxLayout(waypointPanel, BoxLayout.Y_AXIS));
        waypointPanel.setOpaque(false);
        topPanel.add(waypointPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setFocusable(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton addWaypointButton = new JButton("Add Waypoint");
        addWaypointButton.setFocusable(false);
        setFixedButtonSize(addWaypointButton);
        addWaypointButton.addActionListener(e -> addWaypoint());
        buttonPanel.add(addWaypointButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton removeWaypointButton = new JButton("Remove Waypoint");
        removeWaypointButton.setFocusable(false);
        setFixedButtonSize(removeWaypointButton);
        removeWaypointButton.addActionListener(e -> removeWaypoint());
        buttonPanel.add(removeWaypointButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton routeButton = new JButton("Get Route");
        routeButton.setFocusable(false);
        setFixedButtonSize(routeButton);
        routeButton.addActionListener(e -> calculateRoute());
        buttonPanel.add(routeButton);

        // Route list
        routeListModel = new DefaultListModel<>();
        routeList = new JList<>(routeListModel);
        routeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane routeScroll = new JScrollPane(routeList);
        Dimension routeScrollSize = new Dimension(FIELD_WIDTH, 100);
        routeScroll.setPreferredSize(routeScrollSize);
        routeScroll.setMaximumSize(new Dimension(FIELD_WIDTH, 100));
        routeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(new JLabel("Available Routes:") {{
            setForeground(Color.BLACK);
        }});
        topPanel.add(routeScroll);

        routeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && routeList.getSelectedIndex() >= 0) {
                RouteResult selected = currentRoutes.get(routeList.getSelectedIndex());
                drawRoute(selected);
            }
        });

        distanceLabel = new JLabel("Distance: ");
        distanceLabel.setForeground(Color.BLACK);
        timeLabel = new JLabel("Time: ");
        timeLabel.setForeground(Color.BLACK);

        directionsArea = new JTextArea(8, 40);
        directionsArea.setEditable(false);
        directionsArea.setLineWrap(true);
        directionsArea.setWrapStyleWord(true);

        JScrollPane directionsScroll = new JScrollPane(directionsArea);
        directionsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel routeInfoPanel = new JPanel();
        routeInfoPanel.setOpaque(false);
        routeInfoPanel.setLayout(new BorderLayout());

        JPanel summaryPanel = new JPanel(new GridLayout(2,1));
        summaryPanel.setOpaque(false);
        summaryPanel.add(distanceLabel);
        summaryPanel.add(timeLabel);

        routeInfoPanel.add(summaryPanel, BorderLayout.NORTH);
        routeInfoPanel.add(directionsScroll, BorderLayout.CENTER);


        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(buttonPanel);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(routeInfoPanel);

        add(topPanel, BorderLayout.WEST);

        // Map viewer
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

        GetUserLocation location = new GetUserLocation();
        double[] coords = location.getCoordinates();
        GeoPosition start = new GeoPosition(coords[0], coords[1]);
        mapViewer.setAddressLocation(start);
        mapViewer.setZoom(3);

        add(mapViewer, BorderLayout.CENTER);

        // Zoom controls
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.Y_AXIS));
        zoomPanel.setOpaque(false);

        JButton zoomInButton = new JButton("+");
        zoomInButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        zoomInButton.setMargin(new Insets(0, 0, 0, 0));
        zoomInButton.setPreferredSize(new Dimension(45, 45));
        zoomInButton.setMinimumSize(new Dimension(45, 45));
        zoomInButton.setMaximumSize(new Dimension(45, 45));
        zoomInButton.setFocusable(false);
        zoomInButton.addActionListener(e -> {
            int zoom = mapViewer.getZoom();
            if (zoom > 0) mapViewer.setZoom(zoom - 1);
        });

        JButton zoomOutButton = new JButton("\u2212");
        zoomOutButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        zoomOutButton.setMargin(new Insets(0, 0, 0, 0));
        zoomOutButton.setPreferredSize(new Dimension(45, 45));
        zoomOutButton.setMinimumSize(new Dimension(45, 45));
        zoomOutButton.setMaximumSize(new Dimension(45, 45));
        zoomOutButton.setFocusable(false);
        zoomOutButton.addActionListener(e -> {
            int zoom = mapViewer.getZoom();
            if (zoom < 19) mapViewer.setZoom(zoom + 1);
        });

        zoomPanel.add(zoomInButton);
        zoomPanel.add(Box.createVerticalStrut(2));
        zoomPanel.add(zoomOutButton);

        JPanel mapWrapper = new JPanel(new BorderLayout());
        mapWrapper.add(mapViewer, BorderLayout.CENTER);

        mapViewer.setLayout(null);
        mapViewer.add(zoomPanel);
        mapViewer.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                zoomPanel.setBounds(mapViewer.getWidth() - 60, 10, 50, 95);
            }
        });

        remove(mapViewer);
        add(mapWrapper, BorderLayout.CENTER);
        // Mouse wheel zoom
        mapViewer.addMouseWheelListener(e -> {
            int zoom = mapViewer.getZoom();
            if (e.getWheelRotation() < 0) {
                if (zoom > 0) mapViewer.setZoom(zoom - 1);
            } else {
                if (zoom < 19) mapViewer.setZoom(zoom + 1);
            }
        });

        // Drag panning
        final Point[] lastMousePosition = {null};
        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                lastMousePosition[0] = e.getPoint();
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                lastMousePosition[0] = null;
            }
        });
        mapViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (lastMousePosition[0] != null) {
                    Point current = e.getPoint();
                    int dx = current.x - lastMousePosition[0].x;
                    int dy = current.y - lastMousePosition[0].y;
                    Point2D center = mapViewer.getCenter();
                    mapViewer.setCenter(new Point((int) center.getX() - dx, (int) center.getY() - dy));
                    lastMousePosition[0] = current;
                }
            }
        });

        // Tooltip on marker hover
        ToolTipManager.sharedInstance().registerComponent(mapViewer);
        ToolTipManager.sharedInstance().setInitialDelay(200);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        mapViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (currentMarkers == null || currentMarkerAddresses == null) {
                    mapViewer.setToolTipText(null);
                    return;
                }
                Point mousePoint = e.getPoint();
                Rectangle viewport = mapViewer.getViewportBounds();
                for (int i = 0; i < currentMarkers.length; i++) {
                    Point2D geoPixel = mapViewer.getTileFactory().geoToPixel(currentMarkers[i], mapViewer.getZoom());
                    int mx = (int) (geoPixel.getX() - viewport.getX());
                    int my = (int) (geoPixel.getY() - viewport.getY());
                    if (Math.abs(mousePoint.x - mx) < 12 && Math.abs(mousePoint.y - my) < 12) {
                        mapViewer.setToolTipText(currentMarkerAddresses[i]);
                        return;
                    }
                }
                mapViewer.setToolTipText(null);
            }
        });

        setVisible(true);
    }

    private JPanel createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        // -------------------------
        // Favorites menu
        // -------------------------
        JMenu favoritesMenu = new JMenu("Favorites");

        JMenuItem viewFavorites = new JMenuItem("View Favorites");
        viewFavorites.addActionListener(e -> {
            app.updateFavRouteList();         // refresh latest favorite routes
            app.changeScene("favRouteList");  // switch scene
        });

        JMenuItem addFavorite = new JMenuItem("Add Current Route");
addFavorite.addActionListener(e -> {
    if (currentRoutes == null || currentRoutes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No route calculated to add as favorite.");
        return;
    }

    int selectedIndex = routeList.getSelectedIndex();
    if (selectedIndex < 0 || selectedIndex >= currentRoutes.size()) {
        selectedIndex = 0; // fallback to first route
    }

    RouteResult selectedRoute = currentRoutes.get(selectedIndex);

    // Prompt user for favorite name
    String favName = JOptionPane.showInputDialog(this,
            "Enter a name for this favorite route:",
            "Favorite Route Name",
            JOptionPane.PLAIN_MESSAGE);

    if (favName == null) {
        return; // User canceled
    }

    favName = favName.trim();

    // Build FavoriteRoute object
    FavoriteRoute fav = new FavoriteRoute();
    fav.setUserId(currentUser.getUserId());
    fav.setOriginAddress(originField.getText().trim());
    fav.setDestinationAddress(destinationField.getText().trim());
    fav.setFavoriteName(favName);
    fav.setChosenRouteIndex(selectedIndex);

    List<String> waypoints = new ArrayList<>();
    for (JTextField wpField : waypointFields) {
        String wp = wpField.getText().trim();
        if (!wp.isEmpty() && !wp.startsWith("Waypoint")) {
            waypoints.add(wp);
        }
    }
    fav.setWaypoints(waypoints);

    fav.setChosenOverviewPolyline(""); // satisfy NOT NULL, we don’t use it

    try {
        favoriteService.saveFavorite(fav);
        JOptionPane.showMessageDialog(this, "Route added to favorites!");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Failed to save favorite route:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
});

        favoritesMenu.add(viewFavorites);
        favoritesMenu.add(addFavorite);

        // -------------------------
        // Settings menu
        // -------------------------
        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem accountSettings = new JMenuItem("Account Settings");
        accountSettings.addActionListener(e -> app.changeScene("settingPage"));
        settingsMenu.add(accountSettings);

        // -------------------------
        // Account menu
        // -------------------------
        JMenu userMenu = new JMenu("Account");

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                app.logout(); // properly clears user and returns to login
            }
        });

        userMenu.add(logoutItem);

        // Add all menus
        menuBar.add(favoritesMenu);
        menuBar.add(settingsMenu);
        menuBar.add(userMenu);

        // Wrap in panel
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(menuBar, BorderLayout.CENTER);

        return wrapper;
    }

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

    /**
     * Loads a full favorite route into the HomePage.
     */
    public void loadFavoriteRoute(FavoriteRoute favoriteRoute) {
        if (favoriteRoute == null) return;

        // Clear existing waypoints
        for (JTextField wpField : new ArrayList<>(waypointFields)) {
            removeWaypoint();
        }

        // Fill origin & destination
        originField.setText(favoriteRoute.getOriginAddress());
        destinationField.setText(favoriteRoute.getDestinationAddress());

        // Fill waypoints
        for (String wp : favoriteRoute.getWaypoints()) {
            addWaypoint();
            waypointFields.get(waypointFields.size() - 1).setText(wp);
        }

        calculateRoute();

        // If you want to select the previously chosen route index:
        if (currentRoutes != null && !currentRoutes.isEmpty()) {
            int chosenIndex = favoriteRoute.getChosenRouteIndex();
            if (chosenIndex >= 0 && chosenIndex < currentRoutes.size()) {
                routeList.setSelectedIndex(chosenIndex);
                drawRoute(currentRoutes.get(chosenIndex));
            }
        }
    }

    private void drawRoute(RouteResult result) {

        List<GeoPosition> track = result.getPath();

        if (track == null || track.isEmpty()) {
            return;
        }

        RoutePainter routePainter = new RoutePainter(track);

        GeoPosition[] markers = result.getMarkers();
        currentMarkers = markers;
        // Resolve addresses via reverse geocoding
        String[] resolvedAddresses = new String[markers.length];
        for (int i = 0; i < markers.length; i++) {
            resolvedAddresses[i] = geocodingService.reverseGeocode(
                    markers[i].getLatitude(), markers[i].getLongitude());
        }
        currentMarkerAddresses = resolvedAddresses;

        // Marker painter
        Painter<JXMapViewer> markerPainter = (g2d, map, w, h) -> {
            Graphics2D g = (Graphics2D) g2d;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            for (int mi = 0; mi < markers.length; mi++) {
                Point2D point = map.getTileFactory().geoToPixel(markers[mi], map.getZoom());
                Rectangle viewport = map.getViewportBounds();

                int x = (int) (point.getX() - viewport.getX());
                int y = (int) (point.getY() - viewport.getY());

                if (mi == 0) {
                    g.setColor(new Color(0, 180, 0));
                } else if (mi == markers.length - 1) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(new Color(30, 100, 255));
                }

                g.fillOval(x - 12, y - 12, 24, 24);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(2));
                g.drawOval(x - 12, y - 12, 24, 24);

                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                String label = String.valueOf((char) ('A' + mi));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(label, x - fm.stringWidth(label) / 2, y + fm.getAscent() / 2 - 1);
            }
        };

        CompoundPainter<JXMapViewer> painters = new CompoundPainter<>();
        painters.setPainters(routePainter, markerPainter);
        painters.setCacheable(false);
        mapViewer.setOverlayPainter(painters);

        mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);
        mapViewer.repaint();


        StringBuilder directionsText = new StringBuilder();
        List<Instruction> instructions = result.getInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            Instruction instr = instructions.get(i);

            double miles = instr.getDistance() / 1609.34;
            String distanceStr = String.format("%.2f mi", miles);

            String turn = instr.getTurnDescription(routingService.getTranslation());
            if (turn == null || turn.isBlank()) {
                turn = "continue";
            }
            directionsText.append(i + 1)
                    .append(". ")
                    .append(turn)
                    .append(" (")
                    .append(distanceStr)
                    .append(")")
                    .append("\n");
        }

        directionsArea.setText(directionsText.toString());
        directionsArea.setCaretPosition(0);

        distanceLabel.setText("Distance: " + result.getDistanceMiles() + " miles");
        timeLabel.setText("Time: " + result.getFormattedTime());
    }

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

        //duplicate check
        Set<String> uniqueAddresses = new HashSet<>();

        for (String addr : allAddresses) {
            String normalized = addr.toLowerCase().trim();

            if (!uniqueAddresses.add(normalized)) {
                JOptionPane.showMessageDialog(this,
                        "Duplicate addresses are not allowed in the route.",
                        "Invalid Route",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        try {
            List<double[]> allCoords = geocodingService.getCoordinates(allAddresses);
            currentRoutes = routingService.calculateRoutes(allCoords, allAddresses, "car");

            routeListModel.clear();
            for (int i = 0; i < currentRoutes.size(); i++) {
                RouteResult r = currentRoutes.get(i);
                routeListModel.addElement("Route " + (i + 1) +
                        " — " + String.format("%.1f mi, %s", r.getDistanceMiles(), r.getFormattedTime()));
            }

            if (!currentRoutes.isEmpty()) {
                routeList.setSelectedIndex(0);
                drawRoute(currentRoutes.get(0));
            }

        } catch (RuntimeException | Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to find a route. Please check your addresses and try again.",
                    "Route Not Found",
                    JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
    }
}