import model.FavoriteRouteSummary;
import service.FavoriteService;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

public class ChangeFavRoute extends JPanel {
    private static final int WIDTH = 1280;   // Panel width
    private static final int HEIGHT = 720;   // Panel height

    // Input fields for the account creation form
    private JTextField routeNameField;

    // Label to display error messages
    private JLabel errorLabel;

    // Reference to the main application to switch scenes
    final private App app;
    private FavoriteService favoriteService;
    private int routeId;
    private FavoriteRouteSummary editingRoute;

    public ChangeFavRoute(App app, FavoriteService favoriteService) {
        this.app = app;
        this.favoriteService = favoriteService;

        // Use absolute positioning
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
        backButton.setFocusable(false);
        backButton.addActionListener(e -> clearScene("favRouteList")); // Return to login page
        add(backButton);

        add(createChangeFavRoutePanel());
    }

    public ChangeFavRoute(App app, FavoriteService favoriteService, int routeId) {
        this.app = app;
        this.favoriteService = favoriteService;
        this.routeId = routeId;

        // Use absolute positioning
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
        backButton.setFocusable(false);
        backButton.addActionListener(e -> clearScene("favRouteList")); // Return to login page
        add(backButton);

        add(createChangeFavRoutePanel());
    }

    public void setEditingRoute(FavoriteRouteSummary route) {
    this.editingRoute = route;

    if (route != null) {
        this.routeId = route.getRouteId();
        routeNameField.setText(route.getFavoriteName());
    }
}

    private JPanel createChangeFavRoutePanel() {
        int panelWidth = 500;
        int panelHeight = 300;
        int marginFromBottom = 400;

        // Center horizontally, offset from bottom
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = HEIGHT - panelHeight - marginFromBottom;

        // Transparent panel with white background and some alpha
        JPanel panel = new JPanel(null);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);

        // -----------------------------
        // "Name" Field
        // -----------------------------
        JLabel routeNameLabel = new JLabel("Name:");
        routeNameLabel.setBounds(25, 10, 100, 25);
        panel.add(routeNameLabel);

        routeNameField = new JTextField();
        routeNameField.setBounds(25, 30, 450, 25);
        panel.add(routeNameField);

        // ------------------------------
        // Error message label (initially invisible)
        // ------------------------------
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBounds(25, 150, 450, 25);
        errorLabel.setForeground(new Color(180, 0, 0)); // red color
        errorLabel.setVisible(false);
        errorLabel.setOpaque(false);
        panel.add(errorLabel);

        // ------------------------------
        // "Create Account" button
        // ------------------------------
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(45, 200, 200, 30);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(80, 80, 80));
        cancelButton.setFocusable(false);
        cancelButton.addActionListener(e -> clearScene("favRouteList"));
        panel.add(cancelButton);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(255, 200, 200, 30);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBackground(new Color(50, 50, 100));
        confirmButton.setFocusable(false);
        confirmButton.addActionListener(e -> handleReset());
        panel.add(confirmButton);

        return panel;
    }

    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        routeNameField.setText("");
        app.changeScene(page);
    }

    // ------------------------------
    // Display error message on form
    // ------------------------------
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // ------------------------------
    // Handle change password logic
    // ------------------------------
   private void handleReset() {
    String routeName = routeNameField.getText().trim();

    if (routeName.isEmpty()) {
        routeName = "Route";
    }

    if (routeId <= 0) {
        showError("No route selected to edit.");
        return;
    }

    try {
        favoriteService.updateFavoriteRouteName(
                routeId,
                app.getCurrentUser().getUserId(),
                routeName
        );

        editingRoute.setFavoriteName(routeName); // update local copy
        app.updateFavRouteList(); // refresh the list page
        clearScene("favRouteList");

    } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
    } catch (SQLException ex) {
        showError("Failed to update route: " + ex.getMessage());
    }
}
}