import java.awt.*;
import javax.swing.*;
import javafx.application.Platform;

public class App extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    public App() {

        // Initialize JavaFX toolkit for Swing + JavaFX hybrid
        initJavaFX();

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Add screens
        container.add(new LoginPage(this), "login");
        container.add(new ChangePassword(this), "changePassword");
        container.add(new CreateAccountPage(this), "createAccount");
        container.add(new HomePage(this), "homePage");

        add(container);

        setTitle("Swing App");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Method to switch panels
    public void changeScene(String name) {
        cardLayout.show(container, name);
    }

    /**
     * Initializes the JavaFX toolkit.
     * Must be called before any Platform.runLater() calls.
     */
    private static void initJavaFX() {
        // This ensures the JavaFX runtime is initialized only once
        // Platform.startup() can only be called once
        // If already initialized, it will throw IllegalStateException, so we ignore it
        try {
            Platform.startup(() -> {}); // empty runnable to start FX
        } catch (IllegalStateException ex) {
            // FX already initialized, ignore
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}