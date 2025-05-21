package physicianconnect.presentation;

import javax.swing.*;
import java.awt.*;

/**
 * AppFrame
 *
 * Hosts the LoginPanel and DashboardPanel in a CardLayout.
 * Passes a lambda to LoginPanel so that “Go to Dashboard” will flip cards.
 */
public class AppFrame extends JFrame {

    // Constants
    private static final String LOGIN_CARD = "login";
    private static final String DASHBOARD_CARD = "dashboard";
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;

    // Fields
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    /**
     * Constructs the application frame and initializes UI.
     */
    public AppFrame() {
        super("PhysicianConnect");

        initializeFrame();
        initializeComponents();
        showLoginCard();
    }

    /**
     * Sets up frame properties.
     */
    private void initializeFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Creates and adds cards for login and dashboard.
     */
    private void initializeComponents() {
        // Create panels with appropriate callbacks
        LoginView loginPanel = new LoginView(this::showDashboardCard);
        PhysicianDashboardView dashboardPanel = new PhysicianDashboardView();

        // Add to card container
        cards.add(loginPanel, LOGIN_CARD);
        cards.add(dashboardPanel, DASHBOARD_CARD);
        setContentPane(cards);
    }

    /**
     * Displays the login card.
     */
    private void showLoginCard() {
        cardLayout.show(cards, LOGIN_CARD);
    }

    // Displays the dashboard card
    private void showDashboardCard() {
        cardLayout.show(cards, DASHBOARD_CARD);
    }

}
