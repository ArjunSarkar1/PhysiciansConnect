package physicianconnect.presentation;

import javax.swing.*;
import java.awt.*;

/**
 * LoginPanel
 *
 * This view provides a user interface for logging in to the PhysicianConnect system.
 * It includes email and password fields, with options to register or reset a forgotten password
 */
public class LoginView extends JPanel {

    // Constants 

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
    private JButton dashButton; // NEW: "Go to Dashboard" button added for bypass

    /**
     * Constructor for LoginPanel.
     *
     * @param onDashboard Runnable called when Go to Dashboard is pressed.
     */
    public LoginView(Runnable onDashboard) {
        // //ORIGINAL: frame setup removed when converting to JPanel
        // setTitle("PhysicianConnect – Login");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(FRAME_WIDTH, FRAME_HEIGHT);
        // setLocationRelativeTo(null); // center on screen
        // setResizable(false);


        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        initializeComponents(onDashboard); 
    }

    /**
     * Sets up the Swing UI components and layout.
     *
     * @param onDashboard Runnable for dashboard bypass
     */
    private void initializeComponents(Runnable onDashboard) {
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
        loginButton.setForeground(Color.WHITE); // NEW: Set text color to white
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(160, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(10));

        // Dashboard bypass button
        dashButton = new JButton("Go to Dashboard"); // NEW: bypass login functionality
        dashButton.setFont(BUTTON_FONT);
        dashButton.setBackground(Color.GRAY);
        dashButton.setForeground(Color.WHITE);
        dashButton.setFocusPainted(false);
        dashButton.setPreferredSize(new Dimension(160, 45));
        dashButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(dashButton);

        // Add form panel to LoginPanel
        add(formPanel);

        // Bypass action
        dashButton.addActionListener(e -> onDashboard.run()); // NEW: action listener for dashboard
    }
}
