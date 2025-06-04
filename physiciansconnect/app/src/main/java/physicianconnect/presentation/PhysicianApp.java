package physicianconnect.presentation;

import physicianconnect.logic.controller.MessageController;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.MessageService;
import physicianconnect.logic.ReferralManager;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.awt.image.BufferedImage;

// ─── Presentation‐layer imports ───
import physicianconnect.presentation.AddAppointmentDialog;
import physicianconnect.presentation.ViewAppointmentDialog;
import physicianconnect.presentation.PatientHistoryPanel;
import physicianconnect.presentation.PrescribeMedicinePanel;
import physicianconnect.presentation.ReferralPanel;
import physicianconnect.presentation.MessageButton;
import physicianconnect.presentation.MessagePanel;
import physicianconnect.presentation.DailyAvailabilityPanel;
import physicianconnect.presentation.WeeklyAvailabilityPanel;

/**
 * Main application window for a logged-in physician.
 * Business logic is still delegated to managers/services, not yet controllers.
 */
public class PhysicianApp {
    private JFrame frame;
    private DefaultListModel<Appointment> appointmentListModel;
    private final Physician loggedIn;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final MessageService messageService;
    private final ReferralManager referralManager;
    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;
    private LocalDate selectedDate; // for daily navigation (e.g. today, yesterday, tomorrow, …)
    private LocalDate weekStart; // Monday of the currently shown week
    private MessageButton messageButton;
    private Timer messageRefreshTimer;

    public PhysicianApp(Physician loggedIn,
                        PhysicianManager physicianManager,
                        AppointmentManager appointmentManager) {
        this.loggedIn = loggedIn;
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;

        // Use MessageService directly
        this.messageService = new MessageService(
                PersistenceFactory.getMessageRepository()
        );

        // Use ReferralManager directly
        this.referralManager = new ReferralManager(
                PersistenceFactory.getReferralPersistence()
        );

        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame(UIConfig.APP_TITLE + " - " + loggedIn.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        frame.setLayout(new BorderLayout(10, 10));

        // ─────────── Top Panel ───────────
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel(UIConfig.WELCOME_PREFIX + loggedIn.getName());
        welcome.setFont(UITheme.HEADER_FONT);
        welcome.setForeground(UITheme.TEXT_COLOR);
        topPanel.add(welcome, BorderLayout.WEST);

        ImageIcon profileIcon = getProfileIcon(loggedIn.getId());
        JButton profilePicButton = new JButton(profileIcon);
        profilePicButton.setToolTipText("Profile Management");
        profilePicButton.setPreferredSize(new Dimension(40, 40));
        profilePicButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        profilePicButton.setContentAreaFilled(false);
        profilePicButton.setFocusPainted(false);
        profilePicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePicButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Profile Management", true);
            dialog.setContentPane(new ProfilePanel(loggedIn, physicianManager, appointmentManager));
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        // Add message button to top panel
        messageButton = new MessageButton();
        messageButton.setOnAction(e -> showMessageDialog());

        // Create a sub-panel to hold profile and message buttons
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtonPanel.setBackground(BACKGROUND_COLOR);
        rightButtonPanel.add(messageButton);
        rightButtonPanel.add(profilePicButton);

        // Add the button panel to the top panel's EAST
        topPanel.add(rightButtonPanel, BorderLayout.EAST);

        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(UITheme.LABEL_FONT);
        dateTimeLabel.setForeground(UITheme.TEXT_COLOR);
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(dateTimeLabel, BorderLayout.CENTER);

        Timer timer = new Timer(1000, e -> {
            String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        });
        timer.start();

        messageRefreshTimer = new Timer(5000, e -> refreshMessageCount());
        messageRefreshTimer.start();

        frame.add(topPanel, BorderLayout.NORTH);

        // ─────────── Appointments Panel ───────────
        JPanel appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(UITheme.BACKGROUND_COLOR);
        appointmentsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel appointmentsTitle = new JLabel(UIConfig.APPOINTMENTS_TITLE);
        appointmentsTitle.setFont(UITheme.HEADER_FONT);
        appointmentsTitle.setForeground(UITheme.TEXT_COLOR);
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);

        appointmentListModel = new DefaultListModel<>();
        JList<Appointment> appointmentListDisplay = new JList<>(appointmentListModel);
        appointmentListDisplay.setFont(UITheme.LABEL_FONT);
        appointmentListDisplay.setBackground(Color.WHITE);
        appointmentListDisplay.setForeground(UITheme.TEXT_COLOR);
        appointmentListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentListDisplay.setCellRenderer(new ListCardRenderer<>());

        JScrollPane appointmentScroll = new JScrollPane(appointmentListDisplay);
        appointmentScroll.setBorder(BorderFactory.createLineBorder(
                UITheme.PRIMARY_COLOR, 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        // ─────────── Button Panel ───────────
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addAppointmentButton = createStyledButton("📅 Add Appointment");
        JButton viewAppointmentButton = createStyledButton("🔍 View Appointment");
        JButton historyButton = createStyledButton("🗂 Patient History");
        JButton prescribeButton = createStyledButton("💊 Prescribe Medicine");
        JButton referralButton = createStyledButton("📄 Manage Referrals");

        // ─────────── Action Listeners ───────────
        addAppointmentButton.addActionListener(e -> {
            AddAppointmentDialog dlg = new AddAppointmentDialog(
                    frame,
                    appointmentManager,                // now passing AppointmentManager
                    loggedIn.getId(),
                    () -> {
                        dailyPanel.loadSlotsForDate(selectedDate);
                        weeklyPanel.loadWeek(weekStart);
                    });
            dlg.setVisible(true);
            refreshAppointments();
        });

        viewAppointmentButton.addActionListener(e -> {
            Appointment selectedAppt = appointmentListDisplay.getSelectedValue();
            if (selectedAppt == null) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an appointment to view.",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                    frame,
                    appointmentManager,                // passing AppointmentManager here as well
                    selectedAppt,
                    () -> {
                        dailyPanel.loadSlotsForDate(selectedDate);
                        weeklyPanel.loadWeek(weekStart);
                    });
            viewDlg.setVisible(true);
            refreshAppointments();
        });

        historyButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame,
                    UIConfig.PATIENT_HISTORY_DIALOG_TITLE, true);
            PatientHistoryPanel historyPanel = new PatientHistoryPanel(
                    appointmentManager,
                    PersistenceFactory.getPrescriptionPersistence(),
                    loggedIn.getId());
            dialog.setContentPane(historyPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        prescribeButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame,
                    UIConfig.PRESCRIBE_MEDICINE_TITLE, true);
            dialog.setContentPane(new PrescribeMedicinePanel(
                    appointmentManager,
                    PersistenceFactory.getMedicationPersistence(),
                    PersistenceFactory.getPrescriptionPersistence(),
                    loggedIn.getId(),
                    null));
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        referralButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Manage Referrals", true);
            List<String> patientNames = appointmentManager.getAppointmentsForPhysician(loggedIn.getId())
                    .stream().map(a -> a.getPatientName()).distinct().toList();
            ReferralManager referralManager = new ReferralManager(PersistenceFactory.getReferralPersistence());
            dialog.setContentPane(new ReferralPanel(referralManager, loggedIn.getId(), patientNames));
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(prescribeButton);
        buttonPanel.add(referralButton);

        // ─────────── Database Initialization & Availability ───────────
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:prod.db");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not open the database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return; // abort UI init if we cannot connect
        }

        AppointmentDB apptDb = new AppointmentDB(conn);
        AvailabilityService availabilityService = new AvailabilityService(apptDb);

        // 4) Initialize dates
        selectedDate = LocalDate.now();
        weekStart = selectedDate.with(java.time.DayOfWeek.MONDAY);

        String docId = loggedIn.getId();
        int physicianId;
        try {
            physicianId = Integer.parseInt(docId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Invalid physician ID: must be a number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6) Create the weeklyPanel field first, referring to this.dailyPanel in the
        // lambda:
        this.weeklyPanel = new WeeklyAvailabilityPanel(
                physicianId,
                availabilityService,
                appointmentManager,   // passing AppointmentManager
                weekStart,
                () -> {
                    LocalDate dayInView = this.dailyPanel.getCurrentDate();
                    this.dailyPanel.loadSlotsForDate(dayInView);
                });

        // 7) Then create the dailyPanel field, referring to this.weeklyPanel in its
        // lambda:
        this.dailyPanel = new DailyAvailabilityPanel(
                physicianId,
                availabilityService,
                appointmentManager,   // passing AppointmentManager
                selectedDate,
                () -> {
                    LocalDate dayInView = this.dailyPanel.getCurrentDate();
                    LocalDate mondayOfThatDay = dayInView.with(DayOfWeek.MONDAY);
                    this.weeklyPanel.loadWeek(mondayOfThatDay);
                });
        // 8) “Prev/Next Day” buttons (also keep weekly in sync when changing days)
        JButton prevDayBtn = new JButton("← Prev Day");
        JButton nextDayBtn = new JButton("Next Day →");
        JLabel dayLabel = new JLabel("Show Date: " + selectedDate);
        dayLabel.setFont(LABEL_FONT);
        dayLabel.setForeground(TEXT_COLOR);

        prevDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
            dayLabel.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
            LocalDate monday = selectedDate.with(DayOfWeek.MONDAY);
            weeklyPanel.loadWeek(monday);
        });
        nextDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
            dayLabel.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
            LocalDate monday = selectedDate.with(DayOfWeek.MONDAY);
            weeklyPanel.loadWeek(monday);
        });

        JPanel dayNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dayNav.setBackground(UITheme.BACKGROUND_COLOR);
        dayNav.add(prevDayBtn);
        dayNav.add(dayLabel);
        dayNav.add(nextDayBtn);

        JPanel dailyContainer = new JPanel(new BorderLayout());
        dailyContainer.setBackground(UITheme.BACKGROUND_COLOR);
        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        // 9) “Prev/Next Week” buttons (also keep daily in sync if you want):
        JButton prevWeekBtn = new JButton("← Prev Week");
        JButton nextWeekBtn = new JButton("Next Week →");
        JLabel weekLabel = new JLabel("Week of: " + weekStart);
        weekLabel.setFont(LABEL_FONT);
        weekLabel.setForeground(TEXT_COLOR);

        prevWeekBtn.addActionListener(e -> {
            weekStart = weekStart.minusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
            weekLabel.setText(UIConfig.LABEL_WEEK_OF + weekStart);
            selectedDate = weekStart;
            dailyPanel.loadSlotsForDate(selectedDate);
        });
        nextWeekBtn.addActionListener(e -> {
            weekStart = weekStart.plusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
            weekLabel.setText(UIConfig.LABEL_WEEK_OF + weekStart);
            selectedDate = weekStart;
            dailyPanel.loadSlotsForDate(selectedDate);
        });

        JPanel weekNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        weekNav.setBackground(UITheme.BACKGROUND_COLOR);
        weekNav.add(prevWeekBtn);
        weekNav.add(weekLabel);
        weekNav.add(nextWeekBtn);

        JPanel weeklyContainer = new JPanel(new BorderLayout());
        weeklyContainer.setBackground(UITheme.BACKGROUND_COLOR);
        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        // ─────────── Split Pane (Appointments vs. Availability) ───────────
        JTabbedPane availabilityTabs = new JTabbedPane();
        availabilityTabs.setFont(UITheme.LABEL_FONT);
        availabilityTabs.addTab(UIConfig.TAB_DAILY_VIEW, dailyContainer);
        availabilityTabs.addTab(UIConfig.TAB_WEEKLY_VIEW, weeklyContainer);
        availabilityTabs.setPreferredSize(new Dimension(600, 500));

        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                appointmentsPanel,
                availabilityTabs);
        centerSplit.setOneTouchExpandable(false);
        frame.add(centerSplit, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        refreshAppointments();
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(UITheme.BACKGROUND_COLOR);
        button.setBackground(UITheme.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        UITheme.applyHoverEffect(button);
        return button;
    }

    private void refreshAppointments() {
        appointmentListModel.clear();
        List<Appointment> appointments = appointmentManager
                .getAppointmentsForPhysician(loggedIn.getId());
        for (Appointment a : appointments) {
            appointmentListModel.addElement(a);
        }
    }

    private void showMessageDialog() {
        JDialog dialog = new JDialog(frame, UIConfig.MESSAGES_DIALOG_TITLE, true);
        MessageController messageController = new MessageController(messageService);
        MessagePanel messagePanel = new MessagePanel(
                messageController,
                loggedIn.getId(),
                physicianManager.getAllPhysicians());
        dialog.setContentPane(messagePanel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        refreshMessageCount();
    }

    private void refreshMessageCount() {
        int unreadCount = messageService.getUnreadMessageCount(loggedIn.getId());
        messageButton.updateNotificationCount(unreadCount);
    }

    public static void launchSingleUser(Physician loggedIn, PhysicianManager physicianManager,
            AppointmentManager appointmentManager) {
        try {
            SwingUtilities.invokeLater(() -> {
                new PhysicianApp(loggedIn, physicianManager, appointmentManager);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error launching application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ListCardRenderer<T> extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            isSelected ? UITheme.PRIMARY_COLOR : UITheme.ACCENT_LIGHT_COLOR,
                            2
                    ),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            label.setBackground(isSelected
                    ? UITheme.SELECTION_COLOR
                    : UITheme.BACKGROUND_COLOR
            );
            label.setForeground(isSelected
                    ? UITheme.BACKGROUND_COLOR
                    : UITheme.TEXT_COLOR
            );
            label.setOpaque(true);
            label.setFont(UITheme.LABEL_FONT);
            return label;
        }
    }

    private ImageIcon getProfileIcon(String physicianId) {
        File photoFile = new File("src/main/java/physicianconnect/src/profile_photos", physicianId + ".png");
        if (photoFile.exists()) {
            ImageIcon icon = new ImageIcon(photoFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            BufferedImage placeholder = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, 40, 40);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("👤", 10, 25);
            g2.dispose();
            return new ImageIcon(placeholder);
        }
    }

}