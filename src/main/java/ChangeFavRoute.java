import java.awt.*;
import javax.swing.*;

public class ChangeFavRoute extends JPanel {
    private static final int WIDTH = 1280;   // Panel width
    private static final int HEIGHT = 720;   // Panel height

    // Input fields for the account creation form
    private JTextField fromField;
    private JTextField toField;
    private JTextField routeNameField;

    // Label to display error messages
    private JLabel errorLabel;

    // Reference to the main application to switch scenes
    final private App app;

    public ChangeFavRoute(App app) {
        this.app = app;

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
        backButton.addActionListener(e -> clearScene("settingPage")); // Return to login page
        add(backButton);

        add(createChangeFavRoutePanel());
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
        // "From" Field
        // ------------------------------
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setBounds(25, 60, 100, 25);
        panel.add(fromLabel);

        fromField = new JTextField();
        fromField.setBounds(25, 80, 450, 25);
        panel.add(fromField);

        // ------------------------------
        // "To" Field
        // ------------------------------
        JLabel toLabel = new JLabel("To:");
        toLabel.setBounds(25, 110, 150, 25);
        panel.add(toLabel);

        toField = new JTextField();
        toField.setBounds(25, 130, 450, 25);
        panel.add(toField);

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
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(150, 200, 200, 30);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBackground(new Color(50, 50, 100));
        confirmButton.setFocusable(false);
        confirmButton.addActionListener(e -> handleReset()); // Handle account creation
        panel.add(confirmButton);

        return panel;
    }

    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        fromField.setText("");
        toField.setText("");
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
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        String routeName = routeNameField.getText().trim();

        errorLabel.setVisible(false); // reset error message

        if (from.isEmpty() || to.isEmpty()) {
            showError("The route name, origin, and destination cannot be empty.");
        } else {
            // Success: return to login page
            app.changeScene("homePage");
        }
    }
}