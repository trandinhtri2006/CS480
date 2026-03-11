import java.awt.*;
import javax.swing.*;

public class ChangeUsername extends JPanel {
    private static final int WIDTH = 1000;   // Panel width
    private static final int HEIGHT = 800;   // Panel height

    // Input fields for the account creation form
    private JPasswordField oldUsernameField;
    private JPasswordField newUsernameField;
    private JPasswordField passwordField;

    // Label to display error messages
    private JLabel errorLabel;

    // Reference to the main application to switch scenes
    final private App app;

    public ChangeUsername(App app) {
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

        // ------------------------------
        // "Old Username" Field
        // ------------------------------
        JLabel oldUsernameLabel = new JLabel("Old Username:");
        oldUsernameLabel.setBounds(25, 10, 100, 25);
        panel.add(oldUsernameLabel);

        oldUsernameField = new JPasswordField();
        oldUsernameField.setBounds(25, 30, 450, 25);
        panel.add(oldUsernameField);

        // ------------------------------
        // "New username" Field
        // ------------------------------
        JLabel newUsernameLabel = new JLabel("New username:");
        newUsernameLabel.setBounds(25, 60, 150, 25);
        panel.add(newUsernameLabel);

        newUsernameField = new JPasswordField();
        newUsernameField.setBounds(25, 80, 450, 25);
        panel.add(newUsernameField);

        // ------------------------------
        // "Password" Field
        // ------------------------------
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(25, 110, 150, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(25, 130, 450, 25);
        panel.add(passwordField);

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
        // "Confirm" button
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
        oldUsernameField.setText("");
        newUsernameField.setText("");
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
        String oldUsername = new String(oldUsernameField.getPassword()).trim();
        String newUsername = new String(newUsernameField.getPassword()).trim();
        String password = new String(passwordField.getPassword()).trim();

        errorLabel.setVisible(false); // reset error message

        if (oldUsername.isEmpty() || newUsername.isEmpty() || password.isEmpty()) {
            showError("The old username, new username, or password cannot be empty.");
        } else if (oldUsername.equalsIgnoreCase(newUsername)) {
            showError("New username cannot be the same as old username.");    
        } else {
            // Success: return to login page
            app.changeScene("HomePage");
        }
    }
}