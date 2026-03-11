import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class LoginPage extends JFrame {

    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;

    public LoginPage() throws HeadlessException {

        /* DEFAULT SETTING */
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(WIDTH, HEIGHT);

        /* Setting the application icon */
        try {
            File file = new File("Background/profile_thumb.jpg");
            ImageIcon image = new ImageIcon(ImageIO.read(file));
            this.setIconImage(image.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* SETTING THE BACKGROUND */
        JLabel imageLabel = null;

        try {
            File file = new File("Background/images_3.jpg");
            ImageIcon image = new ImageIcon(ImageIO.read(file));
            imageLabel = new JLabel(image);
            imageLabel.setLayout(null); // IMPORTANT → allows positioning components on background
            this.setContentPane(imageLabel);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Getting User Login Information */
        int infoWidth = WIDTH/6;
        int infoHeight = HEIGHT/3;
        Color semiTransBG = new Color(255, 255, 255, 128);
        JLabel info = new JLabel();
        info.setLayout(null);
        info.setOpaque(true);
        info.setBackground(semiTransBG);
        info.setBounds(infoWidth, infoHeight, 500, 300);
        imageLabel.add(info);

        // USERNAME LABEL
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(25, 25, 80, 25);
        info.add(userLabel);   

        // USERNAME FIELD
        JTextField userText = new JTextField();
        userText.setBounds(25, 50, 425, 25);
        info.add(userText);

        // PASSWORD LABEL
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(25, 125, 80, 25);
        info.add(passLabel);

        // PASSWORD FIELD
        JPasswordField passText = new JPasswordField();
        passText.setBounds(25, 150, 425, 25);
        info.add(passText);

        // LOGIN BUTTON
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(infoWidth + 50, 200, 100, 30);
        info.add(loginButton);

        /* BUTTON CLICK EVENT */
        loginButton.addActionListener(e -> {

            // Getting entered values
            String username = userText.getText();
            String password = new String(passText.getPassword());

            // Example validation (replace with database check)
            if(username.equals("admin") && password.equals("1234")) {
                JOptionPane.showMessageDialog(this, "Login Successful");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login");
            }
        });

<<<<<<< Updated upstream
        /* Making sure user can see */
        this.setVisible(true);
=======
    // ------------------------------
    // Display an error message
    // ------------------------------
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // ------------------------------
    // Clear errors and inputs and switch scenes
    // ------------------------------
    private void clearScene(String page) {
        errorLabel.setVisible(false);
        userText.setText("");
        passText.setText("");
        app.changeScene(page);
    }

    // ------------------------------
    // Validate login credentials
    // ------------------------------
    private void handleLogin() {
        String username = userText.getText().trim();
        String password = new String(passText.getPassword()).trim();

        errorLabel.setVisible(false); // reset error message

        if (username.isEmpty() || password.isEmpty()) {
            showError("The username or password cannot be empty.");
        } else if (!username.equals("admin") || !password.equals("1234")) {
            showError("Incorrect username or password.");
        } else {
            // Success: switch to change password page
            app.changeScene("home");
        }
>>>>>>> Stashed changes
    }
}