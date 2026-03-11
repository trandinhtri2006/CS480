import model.User;
import service.AuthService;

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
    private final AuthService authService;

    // Background image
    private Image backgroundImage;

    public LoginPage(App app, AuthService authService) {
        this.app = app;
        this.authService = authService;

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
        // Add the login form and group name panel
        // ------------------------------
        add(createLoginPanel());
        add(displayGroup());
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

    private JPanel displayGroup() {
        int panelWidth = 300;
        int panelHeight = 80;
        int marginFromBottom = 500;

        // Center horizontally and offset from bottom
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = HEIGHT - panelHeight - marginFromBottom; 

        JPanel panel = new JPanel(null);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);

        JLabel groupName = new JLabel("GROUP 5 (●'◡'●)");
        groupName.setBounds(10, 0, 300, 80);
        groupName.setFont(new Font("SansSerif", Font.BOLD, 32));
        groupName.setForeground(Color.RED);
        panel.add(groupName);

        return panel;
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
        // "Create Account" button
        // ------------------------------
        JButton createAccount = new JButton("Create Account");
        createAccount.setBounds((panelWidth / 2) - 75, 155, 150, 30);
        createAccount.setForeground(new Color(50, 50, 100));
        createAccount.setOpaque(false);
        createAccount.setContentAreaFilled(false);
        createAccount.setBorderPainted(false);
        createAccount.setFocusable(false);
        createAccount.addActionListener(e -> clearScene("createAccount")); // switch scene
        panel.add(createAccount);

        // ------------------------------
        // "Forgot Password?" button
        // ------------------------------
        JButton forgotButton = new JButton("Forgot Password?");
        forgotButton.setBounds(5, 180, 150, 30);
        forgotButton.setForeground(new Color(50, 50, 100));
        forgotButton.setOpaque(false);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setBorderPainted(false);
        forgotButton.setFocusable(false);
        forgotButton.addActionListener(e -> clearScene("forgotPassword")); // switch scene
        panel.add(forgotButton);

        // ------------------------------
        // "Sign In" button
        // ------------------------------
        JButton loginButton = new JButton("Sign In");
        loginButton.setBounds((panelWidth / 2) - 75, 130, 150, 30);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(50, 50, 100));
        loginButton.setFocusable(false);
        loginButton.addActionListener(e -> handleLogin()); // validate login
        panel.add(loginButton);

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

    errorLabel.setVisible(false);

    if (username.isEmpty() || password.isEmpty()) {
        showError("The username or password cannot be empty.");
        return;
    }

    try {
        User user = authService.loginUser(username, password);

        if (user == null) {
            showError("Incorrect username or password.");
        } else {
            app.setCurrentUser(user);
            clearScene("settingPage");
        }
    } catch (Exception e) {
        showError("Login failed.");
    }
}

}