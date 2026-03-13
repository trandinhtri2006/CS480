import db.SQLHandler;
import model.FavoriteRoute;
import model.FavoriteRouteSummary;
import model.User;
import service.AuthService;
import service.FavoriteService;
import service.GeocodingService;
import service.RoutingService;

import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import javax.swing.*;

// Main application window (root frame)
public class App extends JFrame {

    // Layout manager to switch between screens
    private CardLayout cardLayout;

    // Container panel that holds all screens
    private JPanel container;

    // Database access layer
    private SQLHandler sqlHandler;

    // Business logic services
    private AuthService authService;
    private FavoriteService favoriteService;

    // Currently logged-in user (null if not logged in)
    private User currentUser;

    private FavoriteRouteSummary selectedFavoriteRoute;
    private HomePage homePage;

    private ChangeFavRoute changeFavRoute;

    // App constructor — initializes services, UI, and screens
    public App() {
        try {
            // Initialize database handler
            sqlHandler = new SQLHandler();
            sqlHandler.initializeDatabase();

            // Initialize service layer with DB dependency
            authService = new AuthService(sqlHandler);
            favoriteService = new FavoriteService(sqlHandler);

        } catch (Exception e) {
            // Show startup error dialog if initialization fails
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize database/services:\n" + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1); // Terminate app on critical failure
        }

        // Set up card-based navigation system
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Register initial screens
        container.add(new LoginPage(this, authService), "login");
        container.add(new CreateAccountPage(this, authService), "createAccount");
        container.add(new ForgotPassword(this), "forgotPassword");

        // Add container to frame
        add(container);

        // Frame configuration
        setTitle("GPS APP");
        try {
            Image icon = new ImageIcon("src/main/resources/Background/applicationIcon.jpg").getImage();
            setIconImage(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSize(1280, 720);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }



    // Switch visible screen by card name
    public void changeScene(String name) {
        cardLayout.show(container, name);
    }

    // Expose authentication service to other components
    public AuthService getAuthService() {
        return authService;
    }

    // Expose favorites service to other components
    public FavoriteService getFavoriteService() {
        return favoriteService;
    }

    // Get currently logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Set current logged-in user (after login)
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // Log out current user and return to login screen
    public void logout() {
        this.currentUser = null;
        changeScene("login");
    }

    public void loadPages() {
        // Create loading dialog
        JDialog loadingDialog = new JDialog(this, "Loading", true);
        JPanel loadingPanel = new JPanel(new BorderLayout(10, 10));
        loadingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel loadingLabel = new JLabel("Loading map data, please wait...", SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        loadingPanel.add(loadingLabel, BorderLayout.NORTH);
        loadingPanel.add(progressBar, BorderLayout.CENTER);

        loadingDialog.setContentPane(loadingPanel);
        loadingDialog.setSize(350, 120);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loadingDialog.setResizable(false);

        // Run heavy initialization on background thread
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Dynamically find the map file in the maps folder
                File mapsDir = new File("maps");
                File[] mapFiles = mapsDir.listFiles((dir, name) -> name.endsWith(".osm.pbf") || name.endsWith(".osm"));
                if (mapFiles == null || mapFiles.length == 0) {
                    throw new FileNotFoundException("No map file found in the 'maps' folder.");
                }
                RoutingService routingService = new RoutingService(mapFiles[0].getPath(), "graph-cache");
                GeocodingService geocodingService = new GeocodingService();

                homePage = new HomePage(
                        App.this,
                        authService,
                        routingService,
                        geocodingService,
                        favoriteService,
                        currentUser
                );
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // check for exceptions

                    container.add(homePage, "homePage");
                    container.add(new SettingPage(App.this), "settingPage");
                    changeFavRoute = new ChangeFavRoute(App.this, favoriteService);
                    container.add(changeFavRoute, "changeFavRoute");
                    container.add(new ChangeUsername(App.this), "changeUsername");
                    container.add(new ChangePassword(App.this), "changePassword");

                    loadingDialog.dispose();
                    changeScene("homePage");

                } catch (Exception e) {
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(App.this,
                            "Failed to initialize HomePage:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true); // blocks until dialog is disposed
    }
    //method to change favorite route name
    public void openEditFavoriteRoute(FavoriteRouteSummary route) {
    this.editingRoute = route;

    if (changeFavRoute != null) {
        changeFavRoute.setEditingRoute(route);
    }

    changeScene("changeFavRoute");
}
    // Refresh favorite routes page (recreated to reflect latest data)
    public void updateFavRouteList() {
        // Remove existing card by name
        for (Component comp : container.getComponents()) {
            if ("favRouteList".equals(comp.getName())) {
                container.remove(comp);
                break;
            }
        }

        // Create fresh panel
        JPanel panel = new FavRouteList(this, favoriteService, homePage);
        panel.setName("favRouteList");
        container.add(panel, "favRouteList");

        // Show the card
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, "favRouteList");

        container.revalidate();
        container.repaint();
    }

    public FavoriteRouteSummary getSelectedFavoriteRoute() {
        return selectedFavoriteRoute;
    }

    public void setSelectedFavoriteRoute(FavoriteRouteSummary route) {
        this.selectedFavoriteRoute = route;
    }

    private FavoriteRouteSummary editingRoute;

    public void setEditingRoute(FavoriteRouteSummary route) {
        this.editingRoute = route;
    }

    public FavoriteRouteSummary getEditingRoute() {
        return editingRoute;
    }


    // Application entry point (runs UI on Event Dispatch Thread)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}