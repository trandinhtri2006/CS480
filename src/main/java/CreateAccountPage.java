import service.AuthService;

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class CreateAccountPage extends JPanel {
    private static final int WIDTH = 1280;   // Panel width
    private static final int HEIGHT = 720;   // Panel height

    // Input fields for the account creation form
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JPasswordField conPasswordText;

    // Label to display error messages
    private JLabel errorLabel;

    // Reference to the main application to switch scenes
    final private App app;
    private final AuthService authService;

    // Background image for the panel
    private Image backgroundImage;

    public CreateAccountPage(App app, AuthService authService) {
        this.app = app;
        this.authService = authService;

        // Use absolute positioning
        setLayout(null);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/Background/loginpageBG.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ------------------------------
        // Back Button at top-left corner
        // ------------------------------
        JButton backButton = new JButton("Back");
        backButton.setBounds(25, 25, 90, 30); // Position at top-left
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(80, 80, 80));
        backButton.setFocusable(false);
        backButton.addActionListener(e -> clearScene("login")); // Return to login page
        add(backButton);

        // ------------------------------
        // Translucent panel containing input fields
        // ------------------------------
        add(createChangePasswordPanel());
    }
    
    // ------------------------------
    // Paint the background image
    // ------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
        }
    }

    // ------------------------------
    // Creates the translucent panel containing username/password fields
    // ------------------------------
    private JPanel createChangePasswordPanel() {
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
        // Username field
        // ------------------------------
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(25, 10, 100, 25);
        panel.add(userLabel);

        usernameText = new JTextField();
        usernameText.setBounds(25, 30, 450, 25);
        panel.add(usernameText);

        // ------------------------------
        // Password field
        // ------------------------------
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(25, 60, 100, 25);
        panel.add(passLabel);

        passwordText = new JPasswordField();
        passwordText.setBounds(25, 80, 450, 25);
        panel.add(passwordText);

        // ------------------------------
        // Confirm password field
        // ------------------------------
        JLabel conPassLabel = new JLabel("Confirm Password:");
        conPassLabel.setBounds(25, 110, 150, 25);
        panel.add(conPassLabel);

        conPasswordText = new JPasswordField();
        conPasswordText.setBounds(25, 130, 450, 25);
        panel.add(conPasswordText);

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
        JButton createButton = new JButton("Create Account");
        createButton.setBounds(175, 200, 150, 30);
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(50, 50, 100));
        createButton.setFocusable(false);
        createButton.addActionListener(e -> handleCreate()); // Handle account creation
        panel.add(createButton);

        return panel;
    }
    
    // ------------------------------
    // Display error message on form
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
        usernameText.setText("");
        passwordText.setText("");
        conPasswordText.setText("");
        app.changeScene(page);
    }

    // ------------------------------
    // Handle account creation logic
    // ------------------------------
    private void handleCreate() {
        String email = usernameText.getText().trim();
        String password = new String(passwordText.getPassword());
        String confirmPassword = new String(conPasswordText.getPassword());

        errorLabel.setVisible(false);

        // Quick UI check only for confirm password
        if (!password.equals(confirmPassword)) {
            showError("Password and Confirm Password do not match.");
            return;
        }

        try {
            authService.registerUser(email, password);

            // If no exception, registration succeeded
            JOptionPane.showMessageDialog(this, "Account created successfully.");
            clearScene("login");

        } catch (IllegalArgumentException e) {
            // Validation errors from service layer
            showError(e.getMessage());

        } catch (Exception e) {
            // System/DB errors
            showError("Account creation failed.");
        }
    }
}