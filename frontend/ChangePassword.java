    import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ChangePassword extends JFrame {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;

    private JTextField userText;
    private JPasswordField passText;
    private JLabel errorLabel; // single label to show errors

    public ChangePassword() {
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
}