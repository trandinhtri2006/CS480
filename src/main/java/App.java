import db.SQLHandler;
import model.FavoriteRouteSummary;
import model.User;
import service.AuthService;
import service.FavoriteService;
import service.GeocodingService;
import service.RoutingService;

import java.awt.*;
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
        try {
            // Initialize routing and geocoding services
            RoutingService routingService = new RoutingService("maps/washington-260309.osm.pbf", "graph-cache");
            GeocodingService geocodingService = new GeocodingService();

            // Create HomePage with correct service order
            HomePage homePage = new HomePage(
                    routingService,
                    geocodingService,
                    favoriteService,
                    currentUser
            );

            // Add HomePage to card layout
            container.add(homePage, "homePage");

            // Other pages that require the App reference
            container.add(new SettingPage(this), "settingPage");
            container.add(new ChangeFavRoute(this), "changeFavRoute");
            container.add(new ChangeUsername(this), "changeUsername");
            container.add(new ChangePassword(this), "changePassword");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize HomePage:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Refresh favorite routes page (recreated to reflect latest data)
    public void updateFavRouteList() {
        container.add(new FavRouteList(this, favoriteService), "favRouteList");
    }

    public FavoriteRouteSummary getSelectedFavoriteRoute() {
        return selectedFavoriteRoute;
    }

    public void setSelectedFavoriteRoute(FavoriteRouteSummary route) {
        this.selectedFavoriteRoute = route;
    }

    // Application entry point (runs UI on Event Dispatch Thread)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}