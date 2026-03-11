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
        container.add(new ForgotPassword(this), "forgotPassword");
        container.add(new HomePage(this), "homePage");
        container.add(new ChangeFavRoute(this), "changeFavRoute");
        container.add(new ChangeUsername(this), "changeUsername");
        container.add(new SettingPage(this), "settingPage");
        add(container);

        setTitle("GPS APP");
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