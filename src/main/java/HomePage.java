import java.awt.*;
import javax.swing.*;

public class HomePage extends JPanel {
    private static final int WIDTH = 1000;   // Panel width
    private static final int HEIGHT = 800;   // Panel height

    // Reference to the main application to switch scenes
    final private App app;  

    public HomePage(App app) {
        this.app = app;

        // Use absolute positioning
        setLayout(null);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);
    }
}