package physicianconnect.presentation;

import javax.swing.*;
import javax.swing.border.*;

import physicianconnect.logic.stub.AppointmentLogic;
import physicianconnect.logic.stub.PhysicianLogic;
import physicianconnect.objects.Physician;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.awt.font.TextAttribute;

// Changed from JFrame to JPanel
public class PhysicianDashboardView extends JPanel {

    // Constants for dimensions, colors, fonts, icons
    public static final int FRAME_W = 1300;
    public static final int FRAME_H = 800;

    // Palette
    private static final Color COLOR_PRIMARY_ACCENT = new Color(0x1976D2);
    private static final Color COLOR_PRIMARY_ACCENT_LIGHT = new Color(0x63A4FF);
    private static final Color COLOR_BACKGROUND_MAIN = new Color(0xF5F5F5);
    private static final Color COLOR_BACKGROUND_COMPONENT = Color.WHITE;
    private static final Color COLOR_TEXT_PRIMARY = new Color(0x212121);
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x757575);
    private static final Color COLOR_BORDER_SUBTLE = new Color(0xE0E0E0);

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

    // Icons
    private static final String ICON_LOGOUT = "🚪";
    private static final String ICON_EDIT = "✏️";
    private static final String ICON_CANCEL = "❌";
    private static final String ICON_DASHBOARD = "📊";
    private static final String ICON_MANAGE_APPTS = "📅";
    private static final String ICON_PATIENT_HISTORY = "📜";
    private static final String ICON_PRESCRIBE = "℞";

    // CardLayout for managing content panels
    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(contentCardLayout);

    // Constants for the content cards
    private static final String DASHBOARD_CONTENT_CARD = "dashboard_content";
    private static final String APPOINTMENT_BOOKING_CARD = "appointment_booking";
    private static final String MEDICAL_HISTORY_CARD = "medical_history";
    private static final String PRESCRIPTION_CARD = "prescription";

    private JButton btnDashboardTab, btnManageTab, btnHistoryTab, btnPrescribeTab, btnLogoutHeader;

    private int physicianId;
    private PhysicianLogic logic = new PhysicianLogic();
    AppointmentLogic appointmentLogic = new AppointmentLogic();
    Physician physician = logic.getPhysicianById(physicianId);

    private Runnable onLogout;

    // Panels for each tab
    private JPanel dashboardContentPanel;
    private AppointmentBookingPanel appointmentBookingPanel;
    private MedicalHistoryPanel medicalHistoryPanel;
    private PrescriptionPanel prescriptionPanel;

    private JLabel lblHeaderTitle;

    public PhysicianDashboardView(int physicianId, Runnable onLogoutCallback) {
        this.physicianId = physicianId;
        Physician physician = logic.getPhysicianById(physicianId);

        this.onLogout = onLogoutCallback;

        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND_MAIN);

        initComponents();
        setActiveTab(btnDashboardTab); // Set initial active tab and show its card
        showCard(DASHBOARD_CONTENT_CARD);
        lblHeaderTitle.setText("DASHBOARD");
    }

    private void initComponents() {
        JPanel headerPanel = createHeaderPanel();
        JPanel tabBarPanel = createTabBarPanel();

        dashboardContentPanel = createDashboardContentPanel();
        appointmentBookingPanel = new AppointmentBookingPanel();
        medicalHistoryPanel = new MedicalHistoryPanel();
        prescriptionPanel = new PrescriptionPanel();

        contentCards.add(dashboardContentPanel, DASHBOARD_CONTENT_CARD);
        contentCards.add(appointmentBookingPanel, APPOINTMENT_BOOKING_CARD);
        contentCards.add(medicalHistoryPanel, MEDICAL_HISTORY_CARD);
        contentCards.add(prescriptionPanel, PRESCRIPTION_CARD);
        contentCards.setOpaque(false);

        JPanel topSectionPanel = new JPanel(new BorderLayout());
        topSectionPanel.setOpaque(false);
        topSectionPanel.add(headerPanel, BorderLayout.NORTH);
        topSectionPanel.add(tabBarPanel, BorderLayout.CENTER);

        add(topSectionPanel, BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        contentWrapper.add(contentCards, BorderLayout.CENTER);

        add(contentWrapper, BorderLayout.CENTER);

        btnDashboardTab.addActionListener(e -> {
            showCard(DASHBOARD_CONTENT_CARD);
            lblHeaderTitle.setText("DASHBOARD");
            setActiveTab(btnDashboardTab);
        });
        btnManageTab.addActionListener(e -> {
            showCard(APPOINTMENT_BOOKING_CARD);
            lblHeaderTitle.setText("APPOINTMENT MANAGEMENT");
            setActiveTab(btnManageTab);
        });
        btnHistoryTab.addActionListener(e -> {
            showCard(MEDICAL_HISTORY_CARD);
            lblHeaderTitle.setText("PATIENT MEDICAL HISTORY");
            setActiveTab(btnHistoryTab);
        });
        btnPrescribeTab.addActionListener(e -> {
            showCard(PRESCRIPTION_CARD);
            lblHeaderTitle.setText("PRESCRIBE MEDICATION");
            setActiveTab(btnPrescribeTab);
        });
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(COLOR_BACKGROUND_COMPONENT);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        Physician physician = logic.getPhysicianById(physicianId);
        String fullName = physician.getFirstName() + " " + physician.getLastName();
        String userInfoText = String.format(
                "<html><b>Physician:</b> Dr. %s<br/><font color='#757575'>%s</font></html>",
                fullName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a")));

        JLabel lblUserInfo = new JLabel(userInfoText);
        lblUserInfo.setFont(FONT_SUBTITLE);
        lblUserInfo.setForeground(COLOR_TEXT_PRIMARY);
        header.add(lblUserInfo, BorderLayout.WEST);

        lblHeaderTitle = new JLabel("DASHBOARD", SwingConstants.CENTER);
        lblHeaderTitle.setFont(FONT_TITLE);
        lblHeaderTitle.setForeground(COLOR_PRIMARY_ACCENT);
        header.add(lblHeaderTitle, BorderLayout.CENTER);

        btnLogoutHeader = new JButton(ICON_LOGOUT + " Logout");
        styleLinkButton(btnLogoutHeader, true);
        btnLogoutHeader.addActionListener(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setOpaque(false);
        logoutPanel.add(btnLogoutHeader);
        header.add(logoutPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTabBarPanel() {
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(COLOR_BACKGROUND_COMPONENT);
        tabBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));

        btnDashboardTab = createTabButton(ICON_DASHBOARD + " Dashboard");
        btnManageTab = createTabButton(ICON_MANAGE_APPTS + " Manage Appointments");
        btnHistoryTab = createTabButton(ICON_PATIENT_HISTORY + " Patient History");
        btnPrescribeTab = createTabButton(ICON_PRESCRIBE + " Prescribe Medication");

        Dimension tabSpacing = new Dimension(5, 0);
        tabBar.add(btnDashboardTab);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnManageTab);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnHistoryTab);
        tabBar.add(Box.createRigidArea(tabSpacing));
        tabBar.add(btnPrescribeTab);

        JPanel tabBarWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        tabBarWrapper.setOpaque(false);
        tabBarWrapper.add(tabBar);

        return tabBarWrapper;
    }

    private void setActiveTab(JButton activeTab) {
        JButton[] tabs = { btnDashboardTab, btnManageTab, btnHistoryTab, btnPrescribeTab };
        for (JButton tab : tabs) {
            if (tab == null)
                continue; // Guard against null if a tab isn't initialized
            if (tab == activeTab) {
                tab.setBackground(COLOR_PRIMARY_ACCENT);
                tab.setForeground(Color.WHITE);
            } else {
                tab.setBackground(COLOR_BACKGROUND_COMPONENT);
                tab.setForeground(COLOR_PRIMARY_ACCENT.darker());
            }
        }
    }

    private JButton createTabButton(String text) {
        JButton tabButton = new JButton(text);
        tabButton.setFont(FONT_TAB);
        tabButton.setBorderPainted(false);
        tabButton.setContentAreaFilled(true);
        tabButton.setFocusPainted(false);
        tabButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tabButton.setOpaque(true);
        tabButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        tabButton.setBackground(COLOR_BACKGROUND_COMPONENT);
        tabButton.setForeground(COLOR_PRIMARY_ACCENT.darker());

        Color hoverBg = COLOR_PRIMARY_ACCENT_LIGHT.brighter().brighter();
        Color normalBg = COLOR_BACKGROUND_COMPONENT;

        tabButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!tabButton.getBackground().equals(COLOR_PRIMARY_ACCENT)) {
                    tabButton.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!tabButton.getBackground().equals(COLOR_PRIMARY_ACCENT)) {
                    tabButton.setBackground(normalBg);
                }
            }
        });
        return tabButton;
    }

    private JPanel createDashboardContentPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(FRAME_W * 2 / 3 - 100);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setOpaque(false);

        JPanel appointmentsPanel = createSubCard("Your Upcoming Appointments");
        JPanel appointmentsListPanel = new JPanel();
        appointmentsListPanel.setLayout(new BoxLayout(appointmentsListPanel, BoxLayout.Y_AXIS));
        appointmentsListPanel.setOpaque(false);
        appointmentsListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        appointmentsListPanel.add(
                createAppointmentEntry("Alice Smith", "21 Jul 2025 • 10:30 – 12:00", "Cataract Surgery Follow-up"));
        appointmentsListPanel.add(Box.createVerticalStrut(15));
        appointmentsListPanel.add(
                createAppointmentEntry("Bob Johnson", "21 Jul 2025 • 12:30 – 14:00", "Annual Physical Examination"));

        JScrollPane appointmentsScrollPane = new JScrollPane(appointmentsListPanel);
        appointmentsScrollPane.setBorder(null);
        appointmentsScrollPane.getViewport().setOpaque(false);
        appointmentsPanel.add(appointmentsScrollPane, BorderLayout.CENTER);

        JPanel availabilityPanel = createSubCard("Your Availability");
        JPanel availabilityListPanel = new JPanel();
        availabilityListPanel.setLayout(new BoxLayout(availabilityListPanel, BoxLayout.Y_AXIS));
        availabilityListPanel.setOpaque(false);
        availabilityListPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        String[] availabilityDays = {
                "Monday    • 09:00 – 17:00", "Tuesday   • 09:00 – 17:00",
                "Wednesday • 09:00 – 17:00", "Thursday  • 09:00 – 13:00 (Half Day)",
                "Friday    • 09:00 – 17:00", "Saturday  • Unavailable", "Sunday    • Unavailable"
        };
        for (String dayInfo : availabilityDays) {
            JLabel lblDay = new JLabel(dayInfo);
            lblDay.setFont(FONT_TEXT_NORMAL);
            lblDay.setForeground(COLOR_TEXT_PRIMARY);
            if (dayInfo.contains("Unavailable")) {
                lblDay.setForeground(COLOR_TEXT_SECONDARY);
            }
            availabilityListPanel.add(lblDay);
            availabilityListPanel.add(Box.createVerticalStrut(10));
        }
        JScrollPane availabilityScrollPane = new JScrollPane(availabilityListPanel);
        availabilityScrollPane.setBorder(null);
        availabilityScrollPane.getViewport().setOpaque(false);
        availabilityPanel.add(availabilityScrollPane, BorderLayout.CENTER);

        JButton btnChangeAvailability = new JButton("Change Availability");
        stylePrimaryButton(btnChangeAvailability); // Assuming stylePrimaryButton is defined
        JPanel changeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changeButtonPanel.setOpaque(false);
        changeButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        changeButtonPanel.add(btnChangeAvailability);
        availabilityPanel.add(changeButtonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(appointmentsPanel);
        splitPane.setRightComponent(availabilityPanel);

        JPanel dashboardContentContainer = new JPanel(new BorderLayout());
        dashboardContentContainer.setOpaque(false);
        dashboardContentContainer.add(splitPane, BorderLayout.CENTER);
        return dashboardContentContainer;
    }

    private JPanel createSubCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND_COMPONENT);
        card.setBorder(new CompoundBorder(
                new FadingBottomShadowBorder(),
                new CompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15))));

        JLabel lblCardTitle = new JLabel(titleText);
        lblCardTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        lblCardTitle.setForeground(COLOR_PRIMARY_ACCENT.darker());
        lblCardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(lblCardTitle, BorderLayout.NORTH);
        return card;
    }

    private JPanel createAppointmentEntry(String patientName, String timeDetails, String reasonForVisit) {
        JPanel entryPanel = new JPanel(new BorderLayout(15, 0));
        entryPanel.setOpaque(false);
        entryPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

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

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        JButton btnEdit = new JButton(ICON_EDIT + " Edit");
        styleLinkButton(btnEdit, false);
        JButton btnCancel = new JButton(ICON_CANCEL + " Cancel");
        styleLinkButton(btnCancel, false);
        actionsPanel.add(btnEdit);
        actionsPanel.add(btnCancel);

        entryPanel.add(infoPanel, BorderLayout.CENTER);
        entryPanel.add(actionsPanel, BorderLayout.EAST);
        return entryPanel;
    }

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

    private void showCard(String cardName) {
        contentCardLayout.show(contentCards, cardName);
    }
}
