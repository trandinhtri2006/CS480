import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ChangePassword extends JPanel {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;
    final private App app;

    public ChangePassword(App app) {
        this.app = app;

        setLayout(null);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Create background JLabel and add to this JPanel
        JLabel backgroundLabel = setBackground("Background/NoBitches.jpg");
        add(backgroundLabel);
    }

    /**
     * Sets background image inside a JLabel
     */
    private JLabel setBackground(String path) {
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setLayout(null); // Allows absolute positioning

        try {
            ImageIcon background = new ImageIcon(ImageIO.read(new File(path)));
            backgroundLabel.setIcon(background);
            backgroundLabel.setBounds(0, 0, WIDTH, HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return backgroundLabel;
    }

    private JPanel createChangePasswordPanel() {
        return null;
    }
}