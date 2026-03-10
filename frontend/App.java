import java.awt.*;
import javax.swing.*;

public class App extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    public App() {
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Add screens
        container.add(new LoginPage(this), "login");
        container.add(new ChangePassword(this), "changePassword");
        container.add(new CreateAccountPage(this), "createAccount");

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}