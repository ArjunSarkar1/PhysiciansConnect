package physicianconnect.presentation.physician;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.MessageService;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.controller.MessageController;
import physicianconnect.logic.controller.PatientHistoryController;
import physicianconnect.logic.controller.PrescriptionController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.presentation.AddAppointmentDialog;
import physicianconnect.presentation.DailyAvailabilityPanel;
import physicianconnect.presentation.MessageButton;
import physicianconnect.presentation.MessagePanel;
import physicianconnect.presentation.ViewAppointmentDialog;
import physicianconnect.presentation.WeeklyAvailabilityPanel;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;
import physicianconnect.presentation.util.ProfileImageUtil;

/**
 * Main window for a logged-in physician.
 * UI layer talks to services via AppointmentController where appropriate.
 */
public class PhysicianApp {

    /*------------------------------------------------------------------*/
    /* Instance fields */
    /*------------------------------------------------------------------*/
    private JFrame frame;
    private DefaultListModel<Appointment> appointmentListModel;

    private final Physician loggedIn;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final AppointmentController appointmentController; // unified controller
    private final ReceptionistManager receptionistManager;
    private final MessageService messageService;
    private final MessageController messageController;
    private final ReferralManager referralManager;

    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;

    private LocalDate selectedDate; // for daily navigation (e.g. today, yesterday, tomorrow, …)
    private LocalDate weekStart; // Monday of the currently shown week

    private MessageButton messageButton;
    private Timer messageRefreshTimer;

    private JButton profilePicButton;

    private final Runnable logoutCallback;

    /*------------------------------------------------------------------*/
    /* Constructor */
    /*------------------------------------------------------------------*/
    public PhysicianApp(Physician loggedIn, PhysicianManager physicianManager, AppointmentManager appointmentManager,
            ReceptionistManager receptionistManager, Runnable logoutCallback) {
        this.loggedIn = loggedIn;
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        this.receptionistManager = receptionistManager;
        this.logoutCallback = () -> {
            frame.dispose(); // Dispose main UI frame
            logoutCallback.run(); // Then run the actual logout logic (e.g. showLoginScreen)
        };
        this.messageService = new MessageService(PersistenceFactory.getMessageRepository());
        this.messageController = new MessageController(messageService);
        this.appointmentController = new AppointmentController(appointmentManager);
        this.referralManager = new ReferralManager(PersistenceFactory.getReferralPersistence());
        initializeUI();
    }

    /*------------------------------------------------------------------*/
    /* UI setup */
    /*------------------------------------------------------------------*/
    private void initializeUI() {
        frame = new JFrame(UIConfig.APP_TITLE + " - " + loggedIn.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        frame.setLayout(new BorderLayout(10, 10));

        /*---------------- Top panel (welcome + notifications) ---------*/
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel(UIConfig.WELCOME_PREFIX + loggedIn.getName());
        welcome.setFont(UITheme.HEADER_FONT);
        welcome.setForeground(UITheme.TEXT_COLOR);
        topPanel.add(welcome, BorderLayout.WEST);

        ImageIcon profileIcon = ProfileImageUtil.getProfileIcon(loggedIn.getId(), true);
        profilePicButton = new JButton(profileIcon);
        profilePicButton.setToolTipText(UIConfig.PROFILE_BUTTON_TEXT);
        profilePicButton.setPreferredSize(new Dimension(40, 40));
        profilePicButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        profilePicButton.setContentAreaFilled(false);
        profilePicButton.setFocusPainted(false);
        profilePicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePicButton.addActionListener(e -> openProfileDialog());

        messageButton = new MessageButton();
        messageButton.setOnAction(e -> showMessageDialog());

        // Right-side panel for profile and message buttons
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        rightButtonPanel.add(messageButton);
        rightButtonPanel.add(profilePicButton);

        topPanel.add(rightButtonPanel, BorderLayout.EAST);

        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(UITheme.LABEL_FONT);
        dateTimeLabel.setForeground(UITheme.TEXT_COLOR);
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(dateTimeLabel, BorderLayout.CENTER);

        Timer clock = new Timer(1000, e -> dateTimeLabel.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        clock.start();

        messageRefreshTimer = new Timer(5000, e -> refreshMessageCount());
        messageRefreshTimer.start();

        frame.add(topPanel, BorderLayout.NORTH);

        /*---------------- Appointments list ---------------------------*/
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
        appointmentScroll.setBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        /*---------------- Button bar ----------------------------------*/
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addAppointmentBtn = createStyledButton(UIConfig.ADD_APPOINTMENT_BUTTON_TEXT);
        JButton viewAppointmentBtn = createStyledButton(UIConfig.VIEW_APPOINTMENTS_BUTTON_TEXT);
        JButton historyBtn = createStyledButton(UIConfig.PATIENT_HISTORY_BUTTON_TEXT);
        JButton prescribeBtn = createStyledButton(UIConfig.PRESCRIBE_MEDICINE_BUTTON);
        JButton referralBtn = createStyledButton(UIConfig.CREATE_REFERRAL_BUTTON_TEXT);
        JButton logoutBtn = createStyledButton(UIConfig.LOGOUT_BUTTON_TEXT);

        /*------ Add-Appointment action --------------------------------*/
        addAppointmentBtn.addActionListener(e -> {
            AddAppointmentDialog dlg = new AddAppointmentDialog(
                    frame,
                    appointmentController, // controller not manager
                    loggedIn.getId(),
                    () -> {
                        dailyPanel.loadSlotsForDate(selectedDate);
                        weeklyPanel.loadWeek(weekStart);
                    });
            dlg.setVisible(true);
            refreshAppointments();
        });

        /*------ View-Appointment action ------------------------------*/
        viewAppointmentBtn.addActionListener(e -> {
            Appointment sel = appointmentListDisplay.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame,
                        UIConfig.ERROR_NO_APPOINTMENT_SELECTED,
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ViewAppointmentDialog d = new ViewAppointmentDialog(
                    frame, appointmentController, sel,
                    () -> {
                        dailyPanel.loadSlotsForDate(selectedDate);
                        weeklyPanel.loadWeek(weekStart);
                    });
            d.setVisible(true);
            refreshAppointments();
        });

        /*------ History, prescriptions, referrals, logout ------------*/
        historyBtn.addActionListener(e -> openHistoryDialog());
        prescribeBtn.addActionListener(e -> openPrescribeDialog());
        referralBtn.addActionListener(e -> openReferralDialog());
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            logoutCallback.run();
        });

        buttonPanel.add(addAppointmentBtn);
        buttonPanel.add(viewAppointmentBtn);
        buttonPanel.add(historyBtn);
        buttonPanel.add(prescribeBtn);
        buttonPanel.add(referralBtn);
        buttonPanel.add(logoutBtn);

        /*---------------- Availability panels (daily/weekly) ----------*/
        // DB + service
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:prod.db");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,
                    UIConfig.ERROR_DATABASE_OPEN + "\n" + ex.getMessage(),
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        AvailabilityService availabilityService = new AvailabilityService(new AppointmentDB(conn));

        selectedDate = LocalDate.now();
        weekStart = selectedDate.with(DayOfWeek.MONDAY);
        String physicianId = loggedIn.getId();

        weeklyPanel = new WeeklyAvailabilityPanel(
                physicianId, availabilityService, appointmentController,
                weekStart, () -> dailyPanel.loadSlotsForDate(dailyPanel.getCurrentDate()));

        dailyPanel = new DailyAvailabilityPanel(
                physicianId, availabilityService, appointmentController,
                selectedDate, () -> weeklyPanel.loadWeek(
                        dailyPanel.getCurrentDate().with(DayOfWeek.MONDAY)));

        /*------ Navigation bars --------------------------------------*/
        JPanel dailyContainer = buildDailyContainer();
        JPanel weeklyContainer = buildWeeklyContainer();

        /*---------------- Split pane ----------------------------------*/
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.LABEL_FONT);
        tabs.addTab(UIConfig.TAB_DAILY_VIEW, dailyContainer);
        tabs.addTab(UIConfig.TAB_WEEKLY_VIEW, weeklyContainer);
        tabs.setPreferredSize(new Dimension(600, 500));

        JSplitPane center = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, appointmentsPanel, tabs);
        center.setOneTouchExpandable(false);

        frame.add(center, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        refreshAppointments();
        appointmentManager.addChangeListener(this::refreshAppointments);

        frame.setVisible(true);
    }

    /*------------------------------------------------------------------*/
    /* Helper: create day view container with navigation buttons */
    /*------------------------------------------------------------------*/
    private JPanel buildDailyContainer() {
        JButton prev = createStyledButton(UIConfig.PREV_DAY_BUTTON_TEXT);
        JButton next = createStyledButton(UIConfig.NEXT_DAY_BUTTON_TEXT);
        JLabel lbl = new JLabel(UIConfig.LABEL_SHOW_DATE + selectedDate);
        lbl.setFont(UITheme.LABEL_FONT);
        lbl.setForeground(UITheme.TEXT_COLOR);

        prev.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
            lbl.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
            weeklyPanel.loadWeek(selectedDate.with(DayOfWeek.MONDAY));
        });
        next.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
            lbl.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
            weeklyPanel.loadWeek(selectedDate.with(DayOfWeek.MONDAY));
        });

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        nav.setBackground(UITheme.BACKGROUND_COLOR);
        nav.add(prev);
        nav.add(lbl);
        nav.add(next);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.BACKGROUND_COLOR);
        container.add(nav, BorderLayout.NORTH);
        container.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);
        return container;
    }

    /*------------------------------------------------------------------*/
    private JPanel buildWeeklyContainer() {
        JButton prev = createStyledButton(UIConfig.PREV_WEEK_BUTTON_TEXT);
        JButton next = createStyledButton(UIConfig.NEXT_WEEK_BUTTON_TEXT);
        JLabel lbl = new JLabel(UIConfig.LABEL_WEEK_OF + weekStart);
        lbl.setFont(UITheme.LABEL_FONT);
        lbl.setForeground(UITheme.TEXT_COLOR);

        prev.addActionListener(e -> {
            weekStart = weekStart.minusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
            lbl.setText(UIConfig.LABEL_WEEK_OF + weekStart);
            selectedDate = weekStart;
            dailyPanel.loadSlotsForDate(selectedDate);
        });
        next.addActionListener(e -> {
            weekStart = weekStart.plusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
            lbl.setText(UIConfig.LABEL_WEEK_OF + weekStart);
            selectedDate = weekStart;
            dailyPanel.loadSlotsForDate(selectedDate);
        });

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        nav.setBackground(UITheme.BACKGROUND_COLOR);
        nav.add(prev);
        nav.add(lbl);
        nav.add(next);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.BACKGROUND_COLOR);
        container.add(nav, BorderLayout.NORTH);
        container.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);
        return container;
    }

    /*------------------------------------------------------------------*/
    private JButton createStyledButton(String txt) {
        JButton b = new JButton(txt);
        b.setFont(UITheme.BUTTON_FONT);
        b.setForeground(UITheme.BACKGROUND_COLOR);
        b.setBackground(UITheme.PRIMARY_COLOR);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        UITheme.applyHoverEffect(b);
        return b;
    }

    /*------------------------------------------------------------------*/
    private void refreshAppointments() {
        appointmentListModel.clear();
        appointmentManager.getAppointmentsForPhysician(loggedIn.getId())
                .forEach(appointmentListModel::addElement);
    }

    private void showMessageDialog() {
        JDialog dlg = new JDialog(frame, UIConfig.MESSAGES_DIALOG_TITLE, true);

        // Combine all physicians and all receptionists
        List<Object> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(physicianManager.getAllPhysicians());
        allUsers.addAll(receptionistManager.getAllReceptionists());

        MessagePanel messagePanel = new MessagePanel(messageController, loggedIn.getId(), "physician", allUsers);
        dlg.setContentPane(messagePanel);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
        refreshMessageCount();
    }

    private void refreshMessageCount() {
        int unreadCount = messageService.getUnreadMessageCount(loggedIn.getId(), "physician");
        messageButton.updateNotificationCount(unreadCount);
    }

    /*------------------------------------------------------------------*/

    public static void launchSingleUser(Physician loggedIn, PhysicianManager physicianManager,
            AppointmentManager appointmentManager, ReceptionistManager receptionistManager, Runnable logoutCallback) {
        try {
            SwingUtilities.invokeLater(() -> {
                new PhysicianApp(loggedIn, physicianManager, appointmentManager, receptionistManager, logoutCallback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error launching application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /*------------------------------------------------------------------*/
    /* Light wrappers for other dialogs */
    /*------------------------------------------------------------------*/
    private void openHistoryDialog() {
        var historyController = new PatientHistoryController(
                appointmentManager,
                PersistenceFactory.getPrescriptionPersistence(),
                referralManager);

        PatientHistoryPanel panel = new PatientHistoryPanel(
                appointmentManager, historyController, loggedIn.getId());

        JDialog dlg = new JDialog(frame, UIConfig.PATIENT_HISTORY_DIALOG_TITLE, true);
        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    private void openPrescribeDialog() {
        var prescriptionController = new PrescriptionController(PersistenceFactory.getPrescriptionPersistence());

        JDialog dlg = new JDialog(frame, UIConfig.PRESCRIBE_MEDICINE_TITLE, true);
        dlg.setContentPane(new PrescribeMedicinePanel(
                appointmentManager,
                PersistenceFactory.getMedicationPersistence(),
                prescriptionController,
                loggedIn.getId(),
                null));
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    private void openReferralDialog() {
        List<String> patientNames = appointmentManager.getAppointmentsForPhysician(loggedIn.getId())
                .stream().map(Appointment::getPatientName)
                .distinct().toList();

        JDialog dlg = new JDialog(frame, UIConfig.REFERRAL_DIALOG_TITLE, true);
        dlg.setContentPane(new ReferralPanel(
                referralManager, loggedIn.getId(), patientNames));
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    private void openProfileDialog() {
        JDialog dlg = new JDialog(frame, UIConfig.PROFILE_DIALOG_TITLE, true);
        PhysicianProfilePanel profilePanel = new PhysicianProfilePanel(
                loggedIn,
                physicianManager,
                appointmentManager,
                null, // appController not used here
                () -> {
                    Physician refreshed = physicianManager.getPhysicianById(loggedIn.getId());
                    if (refreshed != null) {
                        frame.setTitle(UIConfig.APP_TITLE + " - " + refreshed.getName());
                        for (Component comp : frame.getContentPane().getComponents()) {
                            if (comp instanceof JPanel topPanel) {
                                for (Component c : topPanel.getComponents()) {
                                    if (c instanceof JLabel label
                                            && label.getText().startsWith(UIConfig.WELCOME_PREFIX)) {
                                        label.setText(UIConfig.WELCOME_PREFIX + refreshed.getName());
                                    }
                                }
                            }
                        }
                        ImageIcon updatedIcon = ProfileImageUtil.getProfileIcon(refreshed.getId(), true);
                        profilePicButton.setIcon(updatedIcon);
                    }
                },
                logoutCallback // <-- pass the real logout logic
        );
        dlg.setContentPane(profilePanel);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    /*------------------------------------------------------------------*/
    private static class ListCardRenderer<T> extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            isSelected ? UITheme.PRIMARY_COLOR : UITheme.ACCENT_LIGHT_COLOR, 2),
                    new EmptyBorder(10, 10, 10, 10)));
            lbl.setBackground(isSelected ? UITheme.SELECTION_COLOR : UITheme.BACKGROUND_COLOR);
            lbl.setForeground(isSelected ? UITheme.BACKGROUND_COLOR : UITheme.TEXT_COLOR);
            lbl.setOpaque(true);
            lbl.setFont(UITheme.LABEL_FONT);
            return lbl;
        }
    }
}
