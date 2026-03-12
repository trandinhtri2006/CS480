import service.AuthService;

import java.awt.*;
import javax.swing.*;

import model.User;

public class ChangePassword extends JPanel {
    private static final int WIDTH = 1280;   // Panel width
    private static final int HEIGHT = 720;   // Panel height

    // Input fields for changing the password
    private JPasswordField passwordText;
    private JPasswordField conPasswordText;

    // Label to display error messages
    private JLabel errorLabel;

    // Reference to the main application to switch scenes
    final private App app;


    public ChangePassword(App app) {
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

        add(createChangePasswordPanel());
    }

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
        // Password field
        // ------------------------------
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(25, 30, 100, 25);
        panel.add(passLabel);

        passwordText = new JPasswordField();
        passwordText.setBounds(25, 50, 450, 25);
        panel.add(passwordText);

        // ------------------------------
        // Confirm password field
        // ------------------------------
        JLabel conPassLabel = new JLabel("Confirm Password:");
        conPassLabel.setBounds(25, 80, 150, 25);
        panel.add(conPassLabel);

        conPasswordText = new JPasswordField();
        conPasswordText.setBounds(25, 100, 450, 25);
        panel.add(conPasswordText);

        // ------------------------------
        // Error message label (initially invisible)
        // ------------------------------
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBounds(25, 120, 450, 25);
        errorLabel.setForeground(new Color(180, 0, 0)); // red color
        errorLabel.setVisible(false);
        errorLabel.setOpaque(false);
        panel.add(errorLabel);

        // ------------------------------
        // "Confirm" button
        // ------------------------------
        JButton resetButton = new JButton("Change Password");
        resetButton.setBounds(150, 200, 200, 30);
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(50, 50, 100));
        resetButton.setFocusable(false);
        resetButton.addActionListener(e -> handleReset()); // Handle account creation
        panel.add(resetButton);
        
        // ------------------------------
        // Username Display
        // ------------------------------
    
        User currentUser = app.getCurrentUser();
        JLabel usernameLabel = new JLabel("Email: " + currentUser.getEmail());
        usernameLabel.setBounds(25, 10, 450, 25);
        panel.add(usernameLabel);
        
        

        return panel;
    }

    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        passwordText.setText("");
        conPasswordText.setText("");
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
        String password = new String(passwordText.getPassword()).trim();
        String conPassword = new String(conPasswordText.getPassword()).trim();

        errorLabel.setVisible(false);

        if (password.isEmpty() || conPassword.isEmpty()) {
            showError("The password or confirm password cannot be empty.");
            return;
        }

        if (!password.equals(conPassword)) {
            showError("Password and Confirm Password do not match.");
            return;
        }

        try {
            AuthService authService = app.getAuthService();
            String username = app.getCurrentUser().getEmail();
            
            authService.resetPassword(username, password);

            JOptionPane.showMessageDialog(this, "Password changed successfully.");
            clearScene("login");

        } catch (Exception e) {
            showError("Failed to change password.");
        }
    }

}