package physicianconnect.presentation;

import javax.swing.*;
import java.awt.*;

/**
 * AppFrame
 *
 * Hosts the LoginView and the main application panel (PhysicianDashboardView) in a CardLayout.
 * Passes callbacks for navigation.
 */
public class AppFrame extends JFrame {

    private static final String LOGIN_CARD = "login";
    private static final String MAIN_APP_CARD = "main_app";
    // Using constants from PhysicianDashboardView for consistent sizing
    private static final int FRAME_WIDTH = PhysicianDashboardView.FRAME_W; 
    private static final int FRAME_HEIGHT = PhysicianDashboardView.FRAME_H;


    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private LoginView loginPanel;
    private PhysicianDashboardView mainApplicationPanel; 

    public AppFrame() {
        super("PhysicianConnect");
        // It's good practice to set UIManager properties early, e.g., in App.java or here.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            System.err.println("Failed to set Look and Feel or System Properties: " + e.getMessage());
        }
        
        initializeFrame();
        initializeComponents();
        showLoginCard(); 
    }

    private void initializeFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {
        loginPanel = new LoginView(this::showMainApplicationScreen); 
        cards.add(loginPanel, LOGIN_CARD);
        setContentPane(cards);
    }

    private void showLoginCard() {
        if (mainApplicationPanel != null) {
            cards.remove(mainApplicationPanel);
            mainApplicationPanel = null; 
        }
        cardLayout.show(cards, LOGIN_CARD);
        // Optional: resize frame back to login size if it's different
        // setSize(LoginView.FRAME_WIDTH, LoginView.FRAME_HEIGHT); // If LoginView had its own dimensions
        // setLocationRelativeTo(null);
    }

    private void showMainApplicationScreen() {
        // In a real app, physicianId would come from the login process.
        int physicianId = 0; // Default/Placeholder
        mainApplicationPanel = new PhysicianDashboardView(physicianId, this::showLoginCard); 
        
        cards.add(mainApplicationPanel, MAIN_APP_CARD);
        // Ensure frame is sized for the main application
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null); // Re-center after potential resize
        cardLayout.show(cards, MAIN_APP_CARD);
    }
}
