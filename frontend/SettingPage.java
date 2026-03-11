import java.awt.*;
import javax.swing.*;

public class SettingPage extends JPanel {
    private static final int WIDTH = 1000;   // Panel width
    private static final int HEIGHT = 800;   // Panel height

    // Reference to the main application to switch scenes
    final private App app;  

    public SettingPage(App app) {
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
        backButton.addActionListener(e -> clearScene("homePage")); // Return to login page
        add(backButton);

        add(createSettingPage());
    }

    private JPanel createSettingPage() {
        int panelWidth = 500;
        int panelHeight = 300;
        int marginFromBottom = 400;

        // Center horizontally, offset from bottom
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = HEIGHT - panelHeight - marginFromBottom;

        // Transparent panel with white background and some alpha
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);

        // ---------------------------
        // Editing username
        // ---------------------------
        JLabel usernameLabel = new JLabel();
        usernameLabel.setText("Username: CHUD1234");
        usernameLabel.setBounds(10, 10, 250, 30);
        panel.add(usernameLabel);

        JButton usernameButton = new JButton("Edit");
        usernameButton.setOpaque(false);
        usernameButton.setContentAreaFilled(false);
        usernameButton.setBorderPainted(false);
        usernameButton.setBounds(375, 10, 100, 30);
        usernameButton.addActionListener(e -> clearScene("changeUsername"));
        panel.add(usernameButton);

        // ----------------------------
        // Changing password
        // ----------------------------
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 100, 30);
        panel.add(passwordLabel);

        JButton passwordButton = new JButton("Edit");
        passwordButton.setOpaque(false);
        passwordButton.setContentAreaFilled(false);
        passwordButton.setBorderPainted(false);
        passwordButton.setBounds(375, 50, 100, 30);
        passwordButton.addActionListener(e -> clearScene("changePassword"));
        panel.add(passwordButton);

        // ----------------------------
        // Editing Favorite route
        // ----------------------------
        JLabel favRouteLabel = new JLabel("Favorite Route");
        favRouteLabel.setBounds(10, 90, 100, 30);
        panel.add(favRouteLabel);

        JButton favRouteButton = new JButton("Edit");
        favRouteButton.setOpaque(false);
        favRouteButton.setContentAreaFilled(false);
        favRouteButton.setBorderPainted(false);
        favRouteButton.setBounds(375, 90, 100, 30);
        favRouteButton.addActionListener(e -> clearScene("changeFavRoute"));
        panel.add(favRouteButton);


        return panel;
    }



    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        app.changeScene(page);
    }
}