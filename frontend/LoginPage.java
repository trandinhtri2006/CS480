import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class LoginPage extends JPanel {

    private static final int WIDTH = 1000; // Panel width
    private static final int HEIGHT = 800; // Panel height

    // Input fields for username and password
    private JTextField userText;
    private JPasswordField passText;

    // Label for displaying error messages
    private JLabel errorLabel;

    // Reference to the main application to switch scenes
    private final App app;

    // Background image
    private Image backgroundImage;

    public LoginPage(App app) {
        this.app = app;

        // Use absolute positioning
        setLayout(null);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // ------------------------------
        // Load background image once
        // ------------------------------
        try {
            backgroundImage = ImageIO.read(new File("Background/LoginpageBg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ------------------------------
        // Add the login form panel
        // ------------------------------
        add(createLoginPanel());
    }

    // ------------------------------
    // Paint the background image behind all components
    // ------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
        }
    }

    // ------------------------------
    // Create the translucent login form panel
    // ------------------------------
    private JPanel createLoginPanel() {
        int panelWidth = 500;
        int panelHeight = 220;
        int marginFromBottom = 100;

        // Center horizontally and offset from bottom
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = HEIGHT - panelHeight - marginFromBottom;

        // Translucent panel with white background
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180)); // translucent white
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);

        // ------------------------------
        // Username field
        // ------------------------------
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(25, 10, 100, 25);
        panel.add(userLabel);

        userText = new JTextField();
        userText.setBounds(25, 30, 450, 25);
        panel.add(userText);

        // ------------------------------
        // Password field
        // ------------------------------
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(25, 60, 100, 25);
        panel.add(passLabel);

        passText = new JPasswordField();
        passText.setBounds(25, 80, 450, 25);
        panel.add(passText);

        // ------------------------------
        // Error message label (initially invisible)
        // ------------------------------
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBounds(25, 110, 450, 25);
        errorLabel.setForeground(new Color(180, 0, 0)); // red text
        errorLabel.setVisible(false);
        errorLabel.setOpaque(false);
        panel.add(errorLabel);

        // ------------------------------
        // "Sign In" button
        // ------------------------------
        JButton loginButton = new JButton("Sign In");
        loginButton.setBounds(125, 150, 100, 30);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(50, 50, 100));
        loginButton.addActionListener(e -> handleLogin()); // validate login
        panel.add(loginButton);

        // ------------------------------
        // "Create Account" button
        // ------------------------------
        JButton createAccount = new JButton("Create Account");
        createAccount.setBounds(255, 150, 150, 30);
        createAccount.setForeground(Color.WHITE);
        createAccount.setBackground(new Color(50, 50, 100));
        createAccount.addActionListener(e -> clearScene("createAccount")); // switch scene
        panel.add(createAccount);

        return panel;
    }

    // ------------------------------
    // Display an error message
    // ------------------------------
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        errorLabel.setVisible(false);
        userText.setText("");
        passText.setText("");
        app.changeScene(page);
    }

    // ------------------------------
    // Validate login credentials
    // ------------------------------
    private void handleLogin() {
        String username = userText.getText().trim();
        String password = new String(passText.getPassword()).trim();

        errorLabel.setVisible(false); // reset error message

        if (username.isEmpty() || password.isEmpty()) {
            showError("The username or password cannot be empty.");
        } else if (!username.equals("admin") || !password.equals("1234")) {
            showError("Incorrect username or password.");
        } else {
            // Success: switch to change password page
            app.changeScene("changePassword");
        }
    }
}