import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class LoginPage extends JFrame {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;

    private JTextField userText;
    private JPasswordField passText;
    private JLabel errorLabel; // single label to show errors

    public LoginPage() {
        /* DEFAULT SETTINGS */
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center window

        /* Set application icon */
        setAppIcon("Background/profile_thumb.jpg");

        /* Set background image */
        JLabel backgroundLabel = setBackground("Background/LoginpageBackground.jpg");

        /* Add login panel with error label */
        JPanel loginPanel = createLoginPanel();
        backgroundLabel.add(loginPanel);

        /* Show window */
        setVisible(true);
    }

    private void setAppIcon(String path) {
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(new File(path)));
            setIconImage(icon.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JLabel setBackground(String path) {
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setLayout(null); // Allows absolute positioning
        try {
            ImageIcon background = new ImageIcon(ImageIO.read(new File(path)));
            backgroundLabel.setIcon(background);
            setContentPane(backgroundLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return backgroundLabel;
    }

    private JPanel createLoginPanel() {
        int panelWidth = 500;
        int panelHeight = 200;

        // Center horizontally and place near bottom
        int marginFromBottom = 100;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = HEIGHT - panelHeight - marginFromBottom;

        // Semi-transparent panel
        JPanel panel = new JPanel(null);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);
        panel.setBackground(new Color(255, 255, 255, 180));

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(25, 10, 80, 25);
        panel.add(userLabel);

        userText = new JTextField();
        userText.setBounds(25, 30, 450, 25);
        panel.add(userText);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(25, 55, 80, 25);
        panel.add(passLabel);

        passText = new JPasswordField();
        passText.setBounds(25, 75, 450, 25);
        panel.add(passText);

        // Error label (hidden by default)
        errorLabel = new JLabel("");
        errorLabel.setBounds(25, 110, 450, 25);
        errorLabel.setBackground(new Color(0,0,0,0));
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        panel.add(errorLabel);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds((panelWidth - 100) / 2, 145, 100, 30); // center button
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(50, 50, 100));
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton);

        return panel;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void handleLogin() {
        String username = userText.getText();
        String password = new String(passText.getPassword());

        // Hide previous error first
        errorLabel.setVisible(false);

        if (username.isBlank() || password.isBlank()) {
            showError("The username or password cannot be empty.");
        } else if (!"admin".equals(username) || !"1234".equals(password)) {
            showError("Incorrect username or password.");
        } else {
            JOptionPane.showMessageDialog(this, "Login Successful");
        }
    }
}