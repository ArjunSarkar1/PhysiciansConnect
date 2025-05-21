import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.awt.font.TextAttribute;
import java.util.Calendar;
import java.util.Date;

/**
 * AppointmentBookingView
 *
 * Provides a UI for physicians to create or edit appointments.
 * Includes fields for patient name, reason, date, available time slots,
 * custom time selection, and action buttons.
 * Adheres to the styling and structure of the PhysicianDashboardView.
 */
public class AppointmentBookingView extends JFrame {

    /*-------------------------------------------------------------------------*/
    /* Constants - Modernized Look & Feel (aligned with PhysicianDashboardView) */
    /*-------------------------------------------------------------------------*/
    private static final int FRAME_W = 1300;
    private static final int FRAME_H = 800;

    // Palette
    private static final Color COLOR_PRIMARY_ACCENT = new Color(0x1976D2); // Deep Blue
    private static final Color COLOR_PRIMARY_ACCENT_LIGHT = new Color(0x63A4FF); // Lighter blue for hover/highlights
    private static final Color COLOR_BACKGROUND_MAIN = new Color(0xF5F5F5); // Light Gray
    private static final Color COLOR_BACKGROUND_COMPONENT = Color.WHITE; // Cards, pop-ups
    private static final Color COLOR_TEXT_PRIMARY = new Color(0x212121); // Dark Gray
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x757575); // Medium Gray
    private static final Color COLOR_BORDER_SUBTLE = new Color(0xE0E0E0); // Even lighter gray
    private static final Color COLOR_BUTTON_SECONDARY_BG = new Color(0xDEDEDE); // Light gray for secondary button
    private static final Color COLOR_BUTTON_SECONDARY_HOVER_BG = new Color(0xCFCFCF); // Darker gray for secondary hover

    // Fonts
    private static final String FONT_FAMILY = Font.SANS_SERIF;
    private static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 26);
    private static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 15);
    private static final Font FONT_TAB = new Font(FONT_FAMILY, Font.BOLD, 15);
    private static final Font FONT_CARD_TITLE = new Font(FONT_FAMILY, Font.BOLD, 20);
    private static final Font FONT_TEXT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_TEXT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_LINK_BUTTON = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font(FONT_FAMILY, Font.PLAIN, 14); // Added for form fields

    // Icons
    private static final String ICON_LOGOUT = "🚪";
    private static final String ICON_DASHBOARD = "📊";
    private static final String ICON_MANAGE_APPTS = "📅"; // Used for the active tab
    private static final String ICON_PATIENT_HISTORY = "📜";
    private static final String ICON_PRESCRIBE = "℞";
    private static final String ICON_CREATE = "✔️"; // For Create Appointment button
    private static final String ICON_DISCARD = "❌"; // For Discard button

    /*-------------------------------------------------------------------------*/
    /* Fields */
    /*-------------------------------------------------------------------------*/
    private JButton btnDashboard, btnManageAppointments, btnPatientHistory, btnPrescribe, btnLogout;
    private JTextField patientField;
    private JTextField reasonField;
    private JSpinner dateSpinner;
    private JList<String> slotList;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JButton createAppointmentButton;
    private JButton discardButton;

    /*-------------------------------------------------------------------------*/
    /* Constructor */
    /*-------------------------------------------------------------------------*/
    public AppointmentBookingView() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        setTitle("PhysicianConnect – Appointment Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_W, FRAME_H);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    /*-------------------------------------------------------------------------*/
    /* UI Initialization */
    /*-------------------------------------------------------------------------*/
    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BACKGROUND_MAIN);

        JPanel headerPanel = createHeaderPanel();
        JPanel contentWrapperPanel = new JPanel(new BorderLayout());
        contentWrapperPanel.setOpaque(false);

        JPanel tabBarPanel = createTabBarPanel();
        JPanel formCardPanel = createCard("Book / Edit Appointment"); // The form will be inside a card
        formCardPanel.add(createFormPanel(), BorderLayout.CENTER);
        // To prevent the card from taking full height, wrap it
        JPanel formCardHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20)); // Add some top margin
        formCardHolder.setOpaque(false);
        formCardHolder.add(formCardPanel);

        contentWrapperPanel.add(tabBarPanel, BorderLayout.NORTH);
        contentWrapperPanel.add(formCardHolder, BorderLayout.CENTER); // Add holder instead of direct card
        contentWrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(contentWrapperPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    /**
     * Creates the header panel.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(COLOR_BACKGROUND_COMPONENT);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        String userInfoText = String.format(
                "<html><b>Physician:</b> Dr. Eleanor Vance<br/><font color='#757575'>%s</font></html>",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a")));
        JLabel lblUserInfo = new JLabel(userInfoText);
        lblUserInfo.setFont(FONT_SUBTITLE);
        lblUserInfo.setForeground(COLOR_TEXT_PRIMARY);
        header.add(lblUserInfo, BorderLayout.WEST);

        JLabel lblTitle = new JLabel("APPOINTMENT MANAGEMENT", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY_ACCENT);
        header.add(lblTitle, BorderLayout.CENTER);

        btnLogout = new JButton(ICON_LOGOUT + " Logout");
        styleLinkButton(btnLogout, true);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setOpaque(false);
        logoutPanel.add(btnLogout);
        header.add(logoutPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Creates the tab bar panel.
     */
    private JPanel createTabBarPanel() {
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(COLOR_BACKGROUND_COMPONENT);
        tabBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));

        btnDashboard = createTab(ICON_DASHBOARD + " Dashboard", false);
        btnManageAppointments = createTab(ICON_MANAGE_APPTS + " Manage Appointments", true); // Active tab
        btnPatientHistory = createTab(ICON_PATIENT_HISTORY + " Patient History", false);
        btnPrescribe = createTab(ICON_PRESCRIBE + " Prescribe Medication", false);

        Dimension tabSpacing = new Dimension(5, 0);
        tabBar.add(btnDashboard);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnManageAppointments);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnPatientHistory);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnPrescribe);

        JPanel tabBarWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBarWrapper.setOpaque(false);
        tabBarWrapper.add(tabBar);

        return tabBarWrapper;
    }

    /**
     * Builds the central form panel for booking.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Transparent as it's inside a card
        // panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        // for form content
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8); // Uniform padding
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST; // Align components to the left

        // Patient Name
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1; // Label column weight
        panel.add(makeLabel("Patient Name:"), c);
        c.gridx = 1;
        c.weightx = 0.9; // Field column weight
        patientField = new JTextField(25); // Set preferred column width
        styleFormField(patientField);
        panel.add(patientField, c);

        // Reason
        c.gridx = 0;
        c.gridy = 1;
        panel.add(makeLabel("Reason for Visit:"), c);
        c.gridx = 1;
        reasonField = new JTextField(25);
        styleFormField(reasonField);
        panel.add(reasonField, c);

        // Date
        c.gridx = 0;
        c.gridy = 2;
        panel.add(makeLabel("Appointment Date:"), c);
        c.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd MMM yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(FONT_FIELD);
        panel.add(dateSpinner, c);

        // Available Slots
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        panel.add(makeLabel("Available Time Slots (suggested):"), c);
        c.gridy = 4;
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("09:00 – 10:00");
        listModel.addElement("10:30 – 11:30");
        listModel.addElement("14:00 – 15:00");
        listModel.addElement("15:30 - 16:30");
        slotList = new JList<>(listModel);
        slotList.setFont(FONT_TEXT_NORMAL);
        slotList.setVisibleRowCount(4);
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(slotList);
        scrollPane.setPreferredSize(new Dimension(300, 80)); // Adjusted size
        panel.add(scrollPane, c);

        // Custom Time Selection
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridx = 0;
        panel.add(makeLabel("Or Select Custom Time:"), c);
        c.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setOpaque(false);

        SpinnerDateModel fromTimeModel = new SpinnerDateModel();
        fromSpinner = new JSpinner(fromTimeModel);
        fromSpinner.setEditor(new JSpinner.DateEditor(fromSpinner, "HH:mm"));
        fromSpinner.setFont(FONT_FIELD);

        SpinnerDateModel toTimeModel = new SpinnerDateModel();
        toSpinner = new JSpinner(toTimeModel);
        toSpinner.setEditor(new JSpinner.DateEditor(toSpinner, "HH:mm"));
        toSpinner.setFont(FONT_FIELD);

        timePanel.add(new JLabel("From:"));
        timePanel.add(fromSpinner);
        timePanel.add(Box.createHorizontalStrut(10));
        timePanel.add(new JLabel("To:"));
        timePanel.add(toSpinner);
        panel.add(timePanel, c);

        // Action Buttons
        c.gridy = 6;
        c.gridx = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER; // Center the button panel
        c.fill = GridBagConstraints.NONE; // Don't stretch the button panel
        c.insets = new Insets(20, 10, 10, 10); // More top padding for buttons

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        createAppointmentButton = new JButton(ICON_CREATE + " Create Appointment");
        stylePrimaryButton(createAppointmentButton);
        buttonPanel.add(createAppointmentButton);

        discardButton = new JButton(ICON_DISCARD + " Discard Changes");
        styleSecondaryButton(discardButton); // Using a distinct secondary style
        buttonPanel.add(discardButton);

        panel.add(buttonPanel, c);

        // Add a bottom filler to push content up if panel is taller
        c.gridy = 7;
        c.weighty = 1.0;
        panel.add(Box.createGlue(), c);

        return panel;
    }

    /*-------------------------------------------------------------------------*/
    /* UI Element Creation Helpers (aligned with PhysicianDashboardView) */
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
        tabButton.setOpaque(true);
        tabButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        if (isActive) {
            tabButton.setBackground(COLOR_PRIMARY_ACCENT);
            tabButton.setForeground(Color.WHITE);
        } else {
            tabButton.setBackground(COLOR_BACKGROUND_COMPONENT);
            tabButton.setForeground(COLOR_PRIMARY_ACCENT.darker());
            tabButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tabButton.setBackground(COLOR_PRIMARY_ACCENT_LIGHT.brighter().brighter());
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
     * Creates a styled card panel.
     */
    private JPanel createCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND_COMPONENT);
        card.setBorder(new CompoundBorder(
                new FadingBottomShadowBorder(),
                new CompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20))));
        card.setPreferredSize(new Dimension(FRAME_W * 2 / 3, FRAME_H * 2 / 3)); // Give card a preferred size

        JLabel lblCardTitle = new JLabel(titleText);
        lblCardTitle.setFont(FONT_CARD_TITLE);
        lblCardTitle.setForeground(COLOR_PRIMARY_ACCENT.darker());
        lblCardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Space below title

        card.add(lblCardTitle, BorderLayout.NORTH);
        return card;
    }

    /**
     * Creates a JLabel with standard styling.
     */
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT_BOLD); // Using bold for labels for emphasis
        lbl.setForeground(COLOR_TEXT_PRIMARY);
        return lbl;
    }

    /**
     * Styles a JTextField or JFormattedTextField.
     */
    private void styleFormField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setForeground(COLOR_TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10) // Padding inside the field
        ));
        field.setCaretColor(COLOR_PRIMARY_ACCENT); // For better visibility
    }

    /*-------------------------------------------------------------------------*/
    /* Button Styling Helpers (aligned with PhysicianDashboardView) */
    /*-------------------------------------------------------------------------*/

    private void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY_ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_ACCENT.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

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

    private void styleSecondaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_BUTTON_SECONDARY_BG); // A light gray or distinct color
        button.setForeground(COLOR_TEXT_PRIMARY); // Dark text
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_TEXT_SECONDARY, 1), // Subtle border
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_BUTTON_SECONDARY_HOVER_BG; // Slightly darker gray on hover
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

    private void styleLinkButton(JButton button, boolean usePrimaryAccent) {
        button.setFont(FONT_LINK_BUTTON);
        Color baseColor = usePrimaryAccent ? COLOR_PRIMARY_ACCENT : COLOR_TEXT_SECONDARY.darker();
        Color hoverColor = usePrimaryAccent ? COLOR_PRIMARY_ACCENT_LIGHT : COLOR_PRIMARY_ACCENT;

        button.setForeground(baseColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(5, 5, 5, 5));

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
                attributes.put(TextAttribute.UNDERLINE, -1);
                button.setFont(originalFont.deriveFont(attributes));
            }
        });
    }

    /*-------------------------------------------------------------------------*/
    /* Main Method (for standalone preview) */
    /*-------------------------------------------------------------------------*/
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set System Look and Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new AppointmentBookingView().setVisible(true));
    }
}

/**
 * FadingBottomShadowBorder (Copied from PhysicianDashboardView for card
 * styling)
 * Renders a soft, fading shadow effect primarily at the bottom of a component.
 */
class FadingBottomShadowBorder extends AbstractBorder {
    private static final int SHADOW_HEIGHT = 6;
    private static final int MAX_ALPHA = 45;

    public FadingBottomShadowBorder() {
        // Constructor can be empty or initialize insets if needed by superclass
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, SHADOW_HEIGHT, 0);
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
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int componentHeight) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shadowStartY = y + componentHeight - SHADOW_HEIGHT;

        for (int i = 0; i < SHADOW_HEIGHT; i++) {
            int currentAlpha = MAX_ALPHA - (i * (MAX_ALPHA / SHADOW_HEIGHT));
            if (currentAlpha < 0)
                currentAlpha = 0;

            g2.setColor(new Color(0, 0, 0, currentAlpha));
            g2.drawLine(x + 1, shadowStartY + i, x + width - 2, shadowStartY + i);
        }
        g2.dispose();
    }
}