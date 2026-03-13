import model.FavoriteRoute;
import model.FavoriteRouteSummary;
import model.User;
import service.FavoriteService;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

public class FavRouteList extends JPanel {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    // App reference for navigation + session access
    private final App app;

    // Service used to fetch favorite routes
    private final FavoriteService favoriteService;

    // Reference to home page for loading routes
    private HomePage homePage;

    public FavRouteList(App app, FavoriteService favoriteService, HomePage homePage) {
        this.app = app;
        this.favoriteService = favoriteService;
        this.homePage = homePage;

        setLayout(null);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);

        // ------------------------------
        // Back Button at top-left corner
        // ------------------------------
        JButton backButton = new JButton("Back");
        backButton.setBounds(25, 25, 90, 30); // Position at top-left
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(80, 80, 80));
        backButton.setBorderPainted(false);
        backButton.setFocusable(false);
        backButton.addActionListener(e -> clearScene("homePage"));
        add(backButton);

        add(createFavRoutePanel());
    }

    /**
     * Creates the centered container panel that holds the route list.
     */
    private JPanel createFavRoutePanel() {
        int panelWidth = 600;
        int panelHeight = 400;

        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Favorite Routes");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panel.add(title, BorderLayout.NORTH);

        // Scrollable list container
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate list
        loadRoutes(listContainer);

        return panel;
    }

    /**
    * Fetches favorite routes and renders each as a row.
    */
    private void loadRoutes(JPanel listContainer) {
        User user = app.getCurrentUser();

        try {
            // Service returns structured route summaries
            List<FavoriteRouteSummary> routes =
                    favoriteService.getFavoriteSummaries(user.getUserId());

            for (FavoriteRouteSummary route : routes) {
                listContainer.add(createRouteRow(route));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load favorite routes:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates one visual row for a favorite route.
     */
    private JPanel createRouteRow(FavoriteRouteSummary route) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(550, 60));
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Route name label
        JLabel nameLabel = new JLabel(route.getFavoriteName());

        // --- Buttons ---
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            loadFavoriteRoute(route.getRouteId());
        });

        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(e -> {
            app.openEditFavoriteRoute(route);
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            deleteRoute(route.getRouteId());
        });

        // Button container (keeps buttons grouped on the right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.add(loadButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(deleteButton);

        // Layout
        row.add(nameLabel, BorderLayout.CENTER);
        row.add(buttonPanel, BorderLayout.EAST);

        return row;
    }

    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        app.changeScene(page);
    }

    
    /**
     * Handle delete route logic
     */
    private void deleteRoute(int routeId) {
        try {
            int userId = app.getCurrentUser().getUserId();
            favoriteService.deleteFavorite(routeId, userId);
            app.updateFavRouteList();   // no scene hopping
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete favorite route:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handle load route logic (open home page with route loaded)
     */
    private void loadFavoriteRoute(int routeId) {
        try {
            FavoriteRoute fav = favoriteService.getFavoriteRoute(routeId, app.getCurrentUser().getUserId());
            homePage.loadFavoriteRoute(fav);
            app.changeScene("homePage");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load favorite route:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}