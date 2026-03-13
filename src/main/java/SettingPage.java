import java.awt.*;
import javax.swing.*;

public class SettingPage extends JPanel {
    private static final int WIDTH = 1280;   // Panel width
    private static final int HEIGHT = 720;   // Panel height

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
        backButton.setBorderPainted(false);
        backButton.setFocusable(false);
        backButton.addActionListener(e -> clearScene("homePage")); // Return to login page
        add(backButton);

        add(createSettingPage());
    }

    private JPanel createSettingPage() {
        int panelWidth = 500;
        int panelHeight = 300;
        int marginFromBottom = 350;

        // Center horizontally, offset from bottom
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = HEIGHT - panelHeight - marginFromBottom;

        // Transparent panel with white background and some alpha
        JPanel panel = new JPanel(null);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);

        // --------------------------------
        // Editing username
        // --------------------------------
        JPanel usernamePanel = new JPanel();
        usernamePanel.setBounds(10, 10, 475, 50);
        usernamePanel.setBackground(Color.GRAY);
        usernamePanel.setLayout(new BorderLayout());

        // Edit button on the far left

        // Username on the far right
        JLabel usernameLabel = new JLabel("Email: " + app.getCurrentUser().getEmail());
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        usernamePanel.add(usernameLabel, BorderLayout.WEST);

        panel.add(usernamePanel);

        // ----------------------------
        // Changing password
        // ----------------------------
        JPanel passwordPanel = new JPanel();
        passwordPanel.setBounds(10, 70, 475, 50);
        passwordPanel.setBackground(Color.GRAY);
        passwordPanel.setLayout(new BorderLayout());


        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        passwordPanel.add(passwordLabel, BorderLayout.WEST);

        JButton passwordButton = new JButton("Edit");
        passwordButton.setOpaque(false);
        passwordButton.setContentAreaFilled(false);
        passwordButton.setBorderPainted(false);
        passwordButton.setFocusable(false);
        passwordButton.addActionListener(e -> clearScene("changePassword"));
        passwordPanel.add(passwordButton, BorderLayout.EAST);

        panel.add(passwordPanel);
        
        // ----------------------------
        // Editing Favorite route
        // ----------------------------
        JPanel favRoutePanel = new JPanel();
        favRoutePanel.setBounds(10, 130, 475, 50);
        favRoutePanel.setBackground(Color.GRAY);
        favRoutePanel.setLayout(new BorderLayout());

        JLabel favRouteLabel = new JLabel("Favorite Route");
        favRouteLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        favRoutePanel.add(favRouteLabel, BorderLayout.WEST);

        JButton favRouteButton = new JButton("Edit");
        favRouteButton.setOpaque(false);
        favRouteButton.setContentAreaFilled(false);
        favRouteButton.setBorderPainted(false);
        favRouteButton.setFocusable(false);
        favRouteButton.addActionListener(e -> clearScene("favRouteList"));
        favRoutePanel.add(favRouteButton, BorderLayout.EAST);

        panel.add(favRoutePanel);
        
        return panel;
    }



    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        app.updateFavRouteList(); // Refresh favorite routes page to reflect any changes
        app.changeScene(page);
    }
}