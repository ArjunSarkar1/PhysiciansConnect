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
 * MedicalHistoryView
 *
 * This view provides a user interface for viewing a patient's medical history.
 * It includes tabs for visit summaries, lab results, and exam notes.
 */
public class MedicalHistoryView extends JFrame {

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
    private static final Font FONT_TEXT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_LINK_BUTTON = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_FIELD = new Font(FONT_FAMILY, Font.PLAIN, 14); // Added for form fields

    // Icons
    private static final String ICON_LOGOUT = "🚪";
    private static final String ICON_SEARCH = "🔍";
    private static final String ICON_PDF = "📄";
    private static final String ICON_PRINT = "🖨️";
    private static final String ICON_DASHBOARD = "📊";
    private static final String ICON_MANAGE_APPTS = "📅";
    private static final String ICON_PATIENT_HISTORY = "📜"; // Used for the active tab
    private static final String ICON_PRESCRIBE = "℞";
    
    /*-------------------------------------------------------------------------*/
    /* Fields */
    /*-------------------------------------------------------------------------*/
    private JButton btnDashboard, btnManageAppointments, btnPatientHistory, btnPrescribe, btnLogout;
    private JComboBox<String> patientCombo;
    private JTextField searchField;
    private JButton searchButton, exportButton, printButton;
    private DefaultListModel<String> visitsModel, labsModel, notesModel;

    /*-------------------------------------------------------------------------*/
    /* Constructor */
    /*-------------------------------------------------------------------------*/
    public MedicalHistoryView() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        setTitle("PhysicianConnect – Patient Medical History");
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
        JPanel formCardPanel = createCard("View Patient Medical History"); // The form will be inside a card
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

        JLabel lblTitle = new JLabel("PATIENT MEDICAL HISTORY", SwingConstants.CENTER);
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
        btnManageAppointments = createTab(ICON_MANAGE_APPTS + " Manage Appointments", false);
        btnPatientHistory = createTab(ICON_PATIENT_HISTORY + " Patient History", true); // Active tab
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
     * Builds the patient history form.
     */
    private JPanel createFormPanel() {
        // 1) Initialize components
        patientCombo = new JComboBox<>(new String[] {
                "Alice Smith", "Bob Jones", "Charlie Davis"
        });
        styleComboBox(patientCombo);

        searchField = new JTextField();
        styleFormField(searchField);
        searchButton = new JButton(ICON_SEARCH);
        styleSecondaryButton(searchButton);
        // Match button height to field
        Dimension fldDim = searchField.getPreferredSize();
        searchButton.setPreferredSize(new Dimension(
                60,
                fldDim.height + 2));
        stylePrimaryButton(searchButton);

        // Dummy data
        visitsModel = new DefaultListModel<>();
        visitsModel.addElement("01 Jan 2025 – Annual Checkup");
        visitsModel.addElement("15 Feb 2025 – Follow-up Visit");

        labsModel = new DefaultListModel<>();
        labsModel.addElement("CBC: Normal");
        labsModel.addElement("Lipid Panel: Elevated LDL");

        notesModel = new DefaultListModel<>();
        notesModel.addElement("No new complaints.");
        notesModel.addElement("Continue current meds.");

        exportButton = new JButton(ICON_PDF + " Export PDF");
        styleSecondaryButton(exportButton);
        printButton = new JButton(ICON_PRINT + " Print");
        styleSecondaryButton(printButton);

        // 2) Tabbed history pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_TAB);
        JList<String> visitList = new JList<>(visitsModel);
        JList<String> labList = new JList<>(labsModel);
        JList<String> noteList = new JList<>(notesModel);
        visitList.setFont(FONT_TEXT_BOLD);
        labList.setFont(FONT_TEXT_BOLD);
        noteList.setFont(FONT_TEXT_BOLD);

        tabs.addTab("Visit Summaries", new JScrollPane(visitList));
        tabs.addTab("Lab Results", new JScrollPane(labList));
        tabs.addTab("Exam Notes", new JScrollPane(noteList));

        // 3) Layout with GridBag
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 12, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1.0;
        c.gridx = 0;

        // Row 0: Select Patient (col 0)
        c.gridy = 0;
        c.gridwidth = 1;
        JPanel left = new JPanel(new BorderLayout(4, 2));
        left.setOpaque(false);
        left.add(makeLabel("Select Patient:"), BorderLayout.NORTH);
        left.add(patientCombo, BorderLayout.CENTER);
        p.add(left, c);

        // Row 0: Search field + button (col 1)
        c.gridx = 1;
        JPanel right = new JPanel(new BorderLayout(4, 2));
        right.setOpaque(false);
        right.add(makeLabel("Search History:"), BorderLayout.NORTH);
        JPanel searchRow = new JPanel(new BorderLayout(4, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchButton, BorderLayout.EAST);
        right.add(searchRow, BorderLayout.CENTER);
        p.add(right, c);

        // Row 1: Tabbed pane (full width)
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 2;
        p.add(tabs, c);

        // Row 2: Export/Print buttons
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0.0;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        actions.setOpaque(false);
        actions.add(exportButton);
        actions.add(printButton);
        p.add(actions, c);

        // Row 3: Glue to push up
        c.gridy++;
        c.weighty = 1.0;
        p.add(Box.createVerticalGlue(), c);

        return p;
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

    /**
     * Styles a JComboBox to match the form field appearance.
     */
    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_FIELD);
        cb.setForeground(COLOR_TEXT_PRIMARY);
        cb.setBackground(Color.WHITE);
        cb.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        cb.setPreferredSize(new Dimension(300, cb.getPreferredSize().height + 4));
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
        SwingUtilities.invokeLater(() -> new MedicalHistoryView().setVisible(true));
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
