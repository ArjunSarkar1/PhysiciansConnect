package physicianconnect.presentation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.awt.font.TextAttribute;


/**
 * PhysicianDashboardView
 *
 * Main dashboard UI for physicians:
 * - Header with user info + logout
 * - Tab bar for navigation
 * - Split view of upcoming appointments and availability
 *
 */
public class PhysicianDashboardView extends JFrame {

    /*-------------------------------------------------------------------------*/
    /* Constants                                                               */
    /*-------------------------------------------------------------------------*/
    private static final int FRAME_W = 1300;
    private static final int FRAME_H = 800;

    // Palette
    private static final Color COLOR_PRIMARY_ACCENT = new Color(0x1976D2); // Deep Blue 
    private static final Color COLOR_PRIMARY_ACCENT_LIGHT = new Color(0x63A4FF); // Lighter blue for hover/highlights
    private static final Color COLOR_BACKGROUND_MAIN = new Color(0xF5F5F5); // Light Gray (overall background)
    private static final Color COLOR_BACKGROUND_COMPONENT = Color.WHITE;     // Cards, pop-ups
    private static final Color COLOR_TEXT_PRIMARY = new Color(0x212121);      // Dark Gray (for main text)
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x757575);    // Medium Gray (for subtitles, secondary info)
    private static final Color COLOR_BORDER_SUBTLE = new Color(0xE0E0E0);      // Even lighter gray

    // Fonts (Using logical SansSerif for portability)
    private static final String FONT_FAMILY = Font.SANS_SERIF;
    private static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 26);
    private static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 15);
    private static final Font FONT_TAB = new Font(FONT_FAMILY, Font.BOLD, 15);
    private static final Font FONT_CARD_TITLE = new Font(FONT_FAMILY, Font.BOLD, 20);
    private static final Font FONT_TEXT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_TEXT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_LINK_BUTTON = new Font(FONT_FAMILY, Font.PLAIN, 14);

    // Icons
    private static final String ICON_LOGOUT = "🚪"; // Or "->|"
    private static final String ICON_EDIT = "✏️";
    private static final String ICON_CANCEL = "❌";
    private static final String ICON_DASHBOARD = "📊";
    private static final String ICON_MANAGE_APPTS = "📅";
    private static final String ICON_PATIENT_HISTORY = "📜";
    private static final String ICON_PRESCRIBE = "℞";


    /*-------------------------------------------------------------------------*/
    /* Fields                                                                  */
    /*-------------------------------------------------------------------------*/
    private JButton btnDashboard, btnManage, btnHistory, btnPrescribe, btnLogout;

    /*-------------------------------------------------------------------------*/
    /* Constructor                                                             */
    /*-------------------------------------------------------------------------*/
    public PhysicianDashboardView() {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        setTitle("PhysicianConnect – Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_W, FRAME_H);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    /*-------------------------------------------------------------------------*/
    /* UI Initialization                                                       */
    /*-------------------------------------------------------------------------*/

    /**
     * Initialize and layout all UI components.
     */
    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BACKGROUND_MAIN);

        JPanel headerPanel = createHeaderPanel();
        JPanel contentWrapperPanel = new JPanel(new BorderLayout());
        contentWrapperPanel.setOpaque(false); // Transparent to show rootPanel background

        JPanel tabBarPanel = createTabBarPanel();
        JSplitPane mainSplitPane = createMainContentSplitPane();

        contentWrapperPanel.add(tabBarPanel, BorderLayout.NORTH);
        contentWrapperPanel.add(mainSplitPane, BorderLayout.CENTER);
        // Adding some overall padding around the content area (tabs + split pane)
        contentWrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));


        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(contentWrapperPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    /**
     * Creates the header panel with user info, title, and logout button.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0)); // Add horizontal gap
        header.setBackground(COLOR_BACKGROUND_COMPONENT);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(15, 20, 15, 20) // Padding
        ));

        // Left: Physician info + date/time
        String userInfoText = String.format(
                "<html><b>Physician:</b> Dr. Eleanor Vance<br/><font color='#757575'>%s</font></html>", // Using secondary text color
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a"))
        );
        JLabel lblUserInfo = new JLabel(userInfoText);
        lblUserInfo.setFont(FONT_SUBTITLE);
        lblUserInfo.setForeground(COLOR_TEXT_PRIMARY);
        header.add(lblUserInfo, BorderLayout.WEST);

        // Center: Title
        JLabel lblTitle = new JLabel("DASHBOARD", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY_ACCENT);
        header.add(lblTitle, BorderLayout.CENTER);

        // Right: Logout button
        btnLogout = new JButton(ICON_LOGOUT + " Logout");
        styleLinkButton(btnLogout, true); // true for primary accent on hover
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // No internal padding
        logoutPanel.setOpaque(false);
        logoutPanel.add(btnLogout);
        header.add(logoutPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Creates the tab bar panel for navigation.
     */
    private JPanel createTabBarPanel() {
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(COLOR_BACKGROUND_COMPONENT);
        tabBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(5,0,5,0) // Vertical padding for tabs
        ));


        btnDashboard = createTab(ICON_DASHBOARD + " Dashboard", true);
        btnManage = createTab(ICON_MANAGE_APPTS + " Manage Appointments", false);
        btnHistory = createTab(ICON_PATIENT_HISTORY + " Patient History", false);
        btnPrescribe = createTab(ICON_PRESCRIBE + " Prescribe Medication", false);

        // Add some spacing between tabs if not handled by button margins
        Dimension tabSpacing = new Dimension(5,0);
        tabBar.add(btnDashboard);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnManage);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnHistory);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnPrescribe);

        // Wrap tab bar in another panel to provide left padding matching content cards
        JPanel tabBarWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBarWrapper.setOpaque(false);
        tabBarWrapper.add(tabBar);

        return tabBarWrapper;
    }

    /**
     * Creates the main JSplitPane holding appointment and availability cards.
     */
    private JSplitPane createMainContentSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(FRAME_W * 2 / 3 - 50); 
        splitPane.setDividerSize(8); // Make divider more visible/grabbable
        splitPane.setBorder(null); // No border for the split pane itself
        splitPane.setContinuousLayout(true);
        splitPane.setOpaque(false); // Transparent background

        // --- Left Card: Upcoming Appointments ---
        JPanel appointmentsCard = createCard("Your Upcoming Appointments");
        JPanel appointmentsListPanel = new JPanel();
        appointmentsListPanel.setLayout(new BoxLayout(appointmentsListPanel, BoxLayout.Y_AXIS));
        appointmentsListPanel.setOpaque(false); // Transparent background for the list itself
        appointmentsListPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0)); // Padding for the list

        appointmentsListPanel.add(createAppointmentEntry("Alice Smith", "21 Jul 2025 • 10:30 – 12:00", "Cataract Surgery Follow-up"));
        appointmentsListPanel.add(Box.createVerticalStrut(15));
        appointmentsListPanel.add(createAppointmentEntry("Bob Johnson", "21 Jul 2025 • 12:30 – 14:00", "Annual Physical Examination"));
        appointmentsListPanel.add(Box.createVerticalStrut(15));
        appointmentsListPanel.add(createAppointmentEntry("Charlie Lee", "22 Jul 2025 • 09:00 – 10:00", "General Consultation - Flu Symptoms"));
        appointmentsListPanel.add(Box.createVerticalStrut(15));
        appointmentsListPanel.add(createAppointmentEntry("Diana Ross", "22 Jul 2025 • 11:00 – 11:30", "Prescription Refill Request"));


        JScrollPane appointmentsScrollPane = new JScrollPane(appointmentsListPanel);
        appointmentsScrollPane.setBorder(null); // No border for scroll pane
        appointmentsScrollPane.getViewport().setOpaque(false); // Transparent viewport background
        appointmentsScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

        appointmentsCard.add(appointmentsScrollPane, BorderLayout.CENTER);

        // --- Right Card: Availability ---
        JPanel availabilityCard = createCard("Your Availability");
        JPanel availabilityListPanel = new JPanel();
        availabilityListPanel.setLayout(new BoxLayout(availabilityListPanel, BoxLayout.Y_AXIS));
        availabilityListPanel.setOpaque(false);
        availabilityListPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));


        String[] availabilityDays = {
                "Monday    • 09:00 – 17:00",
                "Tuesday   • 09:00 – 17:00",
                "Wednesday • 09:00 – 17:00",
                "Thursday  • 09:00 – 13:00 (Half Day)",
                "Friday    • 09:00 – 17:00",
                "Saturday  • Unavailable",
                "Sunday    • Unavailable"
        };

        for (String dayInfo : availabilityDays) {
            JLabel lblDay = new JLabel(dayInfo);
            lblDay.setFont(FONT_TEXT_NORMAL);
            lblDay.setForeground(COLOR_TEXT_PRIMARY);
            // Add a small leading icon or different style for emphasis
            if (dayInfo.contains("Unavailable")) {
                lblDay.setForeground(COLOR_TEXT_SECONDARY);
            }
            availabilityListPanel.add(lblDay);
            availabilityListPanel.add(Box.createVerticalStrut(10));
        }
        availabilityCard.add(new JScrollPane(availabilityListPanel), BorderLayout.CENTER); // Make it scrollable if many items

        JButton btnChangeAvailability = new JButton("Change Availability");
        stylePrimaryButton(btnChangeAvailability);
        JPanel changeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align button to the right
        changeButtonPanel.setOpaque(false);
        changeButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Top margin for button
        changeButtonPanel.add(btnChangeAvailability);
        availabilityCard.add(changeButtonPanel, BorderLayout.SOUTH);

        // Assemble split pane
        splitPane.setLeftComponent(appointmentsCard);
        splitPane.setRightComponent(availabilityCard);

        // Set background of splitpane components to be transparent so root shows
        ((JPanel)splitPane.getLeftComponent()).setOpaque(false);
        ((JPanel)splitPane.getRightComponent()).setOpaque(false);


        return splitPane;
    }


    /*-------------------------------------------------------------------------*/
    /* UI Element Creation Helpers                                             */
    /*-------------------------------------------------------------------------*/

    /**
     * Creates a styled tab button.
     */
    private JButton createTab(String text, boolean isActive) {
        JButton tabButton = new JButton(text);
        tabButton.setFont(FONT_TAB);
        tabButton.setBorderPainted(false);
        tabButton.setContentAreaFilled(false);
        tabButton.setFocusPainted(false);
        tabButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tabButton.setOpaque(true); // Needed for background color
        tabButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding within tab

        if (isActive) {
            tabButton.setBackground(COLOR_PRIMARY_ACCENT);
            tabButton.setForeground(Color.WHITE);
        } else {
            tabButton.setBackground(COLOR_BACKGROUND_COMPONENT); // Or a very light gray
            tabButton.setForeground(COLOR_PRIMARY_ACCENT.darker());
            // Hover effect for inactive tabs
            tabButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tabButton.setBackground(COLOR_PRIMARY_ACCENT_LIGHT.brighter().brighter()); // Very light blue/gray
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    tabButton.setBackground(COLOR_BACKGROUND_COMPONENT);
                }
            });
        }
        return tabButton;
    }

    /**
     * Creates a styled card panel with a title and shadow.
     */
    private JPanel createCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout(10, 10)); // Gaps
        card.setBackground(COLOR_BACKGROUND_COMPONENT);
        // Shadow (outer) + Line Border (inner) + Padding (innermost)
        card.setBorder(new CompoundBorder(
                new FadingBottomShadowBorder(), // Custom shadow
                new CompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE), // Subtle line border
                        BorderFactory.createEmptyBorder(20, 20, 20, 20) // Inner padding
                )
        ));

        JLabel lblCardTitle = new JLabel(titleText);
        lblCardTitle.setFont(FONT_CARD_TITLE);
        lblCardTitle.setForeground(COLOR_PRIMARY_ACCENT.darker());
        lblCardTitle.setBorder(BorderFactory.createEmptyBorder(0,0,10,0)); // Space below title

        card.add(lblCardTitle, BorderLayout.NORTH);
        return card;
    }

    /**
     * Creates a panel for a single appointment entry.
     */
    private JPanel createAppointmentEntry(String patientName, String timeDetails, String reasonForVisit) {
        JPanel entryPanel = new JPanel(new BorderLayout(15, 0)); // Add horizontal gap
        entryPanel.setOpaque(false); // Transparent background
        entryPanel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, COLOR_BORDER_SUBTLE), // Bottom separator line
            BorderFactory.createEmptyBorder(10, 10, 10, 10)) // Padding
        );


        // Info Panel (Patient Name, Time, Reason)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblPatientName = new JLabel(patientName);
        lblPatientName.setFont(FONT_TEXT_BOLD);
        lblPatientName.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblTimeDetails = new JLabel(timeDetails);
        lblTimeDetails.setFont(FONT_TEXT_NORMAL);
        lblTimeDetails.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblReason = new JLabel("Reason: " + reasonForVisit);
        lblReason.setFont(FONT_TEXT_NORMAL);
        lblReason.setForeground(COLOR_TEXT_SECONDARY);

        infoPanel.add(lblPatientName);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblTimeDetails);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblReason);

        // Actions Panel (Edit, Cancel Buttons)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton btnEdit = new JButton(ICON_EDIT + " Edit");
        styleLinkButton(btnEdit, false);
        JButton btnCancel = new JButton(ICON_CANCEL + " Cancel");
        styleLinkButton(btnCancel, false); // false = standard link color

        actionsPanel.add(btnEdit);
        actionsPanel.add(btnCancel);

        entryPanel.add(infoPanel, BorderLayout.CENTER);
        entryPanel.add(actionsPanel, BorderLayout.EAST);

        return entryPanel;
    }

    /*-------------------------------------------------------------------------*/
    /* Button Styling Helpers                                                  */
    /*-------------------------------------------------------------------------*/

    /**
     * Styles a JButton as a primary action button.
     */
    private void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY_ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        // Rounded border and padding
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_ACCENT.darker(), 1), // Subtle border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effect
        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_PRIMARY_ACCENT_LIGHT;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
    }

    /**
     * Styles a JButton to look like a hyperlink.
     */
    private void styleLinkButton(JButton button, boolean usePrimaryAccent) {
        button.setFont(FONT_LINK_BUTTON);
        Color baseColor = usePrimaryAccent ? COLOR_PRIMARY_ACCENT : COLOR_TEXT_SECONDARY.darker();
        Color hoverColor = usePrimaryAccent ? COLOR_PRIMARY_ACCENT_LIGHT : COLOR_PRIMARY_ACCENT;

        button.setForeground(baseColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(5,5,5,5)); 

        // Store original font for underline toggling
        Font originalFont = button.getFont();
        @SuppressWarnings("unchecked")
        Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) originalFont.getAttributes();


        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(hoverColor);
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                button.setFont(originalFont.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(baseColor);
                attributes.put(TextAttribute.UNDERLINE, -1); // No underline
                button.setFont(originalFont.deriveFont(attributes));
            }
        });
    }

    /*-------------------------------------------------------------------------*/
    /* Main Method                                                             */
    /*-------------------------------------------------------------------------*/
    public static void main(String[] args) {
        // Apply a modern Look and Feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set System Look and Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new PhysicianDashboardView().setVisible(true));
    }
}

/**
 * FadingBottomShadowBorder
 *
 * Renders a soft, fading shadow effect primarily at the bottom of a component.
 * This border provides its own insets for the shadow space.
 */
class FadingBottomShadowBorder extends AbstractBorder {
    private static final int SHADOW_HEIGHT = 6; 
    private static final int MAX_ALPHA = 45;   
    public FadingBottomShadowBorder() {
        new Insets(1, 1, SHADOW_HEIGHT + 1, SHADOW_HEIGHT + 1);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,SHADOW_HEIGHT,0); // Only bottom space for shadow
    }
     @Override
    public Insets getBorderInsets(Component c, Insets newInsets) {
        newInsets.top = 0;
        newInsets.left = 0;
        newInsets.bottom = SHADOW_HEIGHT;
        newInsets.right = 0;
        return newInsets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false; // The shadow is semi-transparent
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int componentHeight) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // The starting Y position of the shadow, at the bottom edge of the component
        int shadowStartY = y + componentHeight - SHADOW_HEIGHT;

        for (int i = 0; i < SHADOW_HEIGHT; i++) {
            // Alpha decreases as we move away (downwards) from the component's edge
            int currentAlpha = MAX_ALPHA - (i * (MAX_ALPHA / SHADOW_HEIGHT));
            if (currentAlpha < 0) currentAlpha = 0;
            if (currentAlpha > 255) currentAlpha = 255; // Should not happen with MAX_ALPHA <= 255

            g2.setColor(new Color(0, 0, 0, currentAlpha));
            // Draw a horizontal line for the shadow.
            // The shadow "grows" downwards from the component's bottom edge.
            g2.drawLine(x + 1, shadowStartY + i, x + width - 2, shadowStartY + i);
        }
        g2.dispose();
    }
}