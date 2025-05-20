import javax.swing.*;
import java.awt.*;

/**
 * LoginView
 *
 * This view provides a user interface for logging in to the PhysicianConnect system.
 * It includes email and password fields, with options to register or reset a forgotten password.
 */
public class LoginView extends JFrame {

    // Constants
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 255);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Color PRIMARY_COLOR = new Color(33, 111, 174);

    // Fields
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;

    /**
     * Constructor for LoginView.
     */
    public LoginView() {
        setTitle("PhysicianConnect – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null); // center on screen
        setResizable(false);

        initializeComponents();
    }

    /**
     * Sets up the Swing UI components and layout.
     */
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(PANEL_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(50, 70, 50, 70)
        ));

        // Title
        JLabel titleLabel = new JLabel("PHYSICIAN CONNECT");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(PRIMARY_COLOR);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(40));

        // Email input
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));

        // Password input
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(20));

        // Register / Forgot Password
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        linkPanel.setOpaque(false);
        registerButton = new JButton("Register");
        forgotPasswordButton = new JButton("Forgot Password");
        linkPanel.add(registerButton);
        linkPanel.add(forgotPasswordButton);

        formPanel.add(linkPanel);
        formPanel.add(Box.createVerticalStrut(30));

        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setPreferredSize(new Dimension(160, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(loginButton);

        // Add form to center of main panel
        mainPanel.add(formPanel);
        setContentPane(mainPanel);
    }

    /**
     * Entry point for testing the LoginView UI independently.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}