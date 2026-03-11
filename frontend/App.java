
import db.SQLHandler;
import service.AuthService;
import service.FavoriteService;

import java.awt.*;
import javax.swing.*;

public class App extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private SQLHandler sqlHandler;
    private AuthService authService;
    private FavoriteService favoriteService;

    public App() {
         try {
        sqlHandler = new SQLHandler();
        sqlHandler.initializeDatabase();

        authService = new AuthService(sqlHandler);
        favoriteService = new FavoriteService(sqlHandler);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Failed to initialize database/services:\n" + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        System.exit(1);
    }
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Add screens
        container.add(new LoginPage(this, authService), "login");
        container.add(new ChangePassword(this), "changePassword");
        container.add(new CreateAccountPage(this, authService), "createAccount");    
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