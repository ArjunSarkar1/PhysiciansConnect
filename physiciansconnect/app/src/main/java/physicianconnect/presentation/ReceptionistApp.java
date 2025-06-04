package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.logic.MessageService;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.PersistenceFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReceptionistApp {
    private final Receptionist loggedIn;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final ReceptionistManager receptionistManager;
    private final Runnable logoutCallback;
    private final MessageService messageService;
    private final AvailabilityService availabilityService;
    private JFrame frame;
    private JComboBox<Physician> physicianCombo;
    private DefaultListModel<Appointment> appointmentListModel;
    private JList<Appointment> appointmentListDisplay;
    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;
    private LocalDate selectedDate;
    private LocalDate weekStart;
    private JLabel dayLabel;
    private JLabel weekLabel;
    private Timer dateTimeTimer;
    private String currentPhysicianId;
    // Add these as fields for reuse in updateCalendarPanels
    private JPanel dailyContainer;
    private JPanel weeklyContainer;
    private JPanel dayNav;
    private JPanel weekNav;
    private MessageButton messageButton;

public ReceptionistApp(Receptionist loggedIn, PhysicianManager physicianManager,
        AppointmentManager appointmentManager, ReceptionistManager receptionistManager, Runnable logoutCallback) {
    this.loggedIn = loggedIn;
    this.physicianManager = physicianManager;
    this.appointmentManager = appointmentManager;
    this.receptionistManager = receptionistManager;
        this.logoutCallback = logoutCallback;
        this.messageService = new MessageService(PersistenceFactory.getMessageRepository());
        this.availabilityService = new AvailabilityService(
                (physicianconnect.persistence.sqlite.AppointmentDB) PersistenceFactory.getAppointmentPersistence());
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Receptionist Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Welcome
        JLabel welcome = new JLabel("Welcome, " + loggedIn.getName());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcome.setForeground(new Color(34, 40, 49));
        topPanel.add(welcome, BorderLayout.WEST);

        // Date/Time Label
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setForeground(new Color(34, 40, 49));
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(dateTimeLabel, BorderLayout.CENTER);

        dateTimeTimer = new Timer(1000, e -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        });
        dateTimeTimer.start();

        // Message Button
        messageButton = new MessageButton();
        messageButton.setOnAction(e -> showMessageDialog());
        topPanel.add(messageButton, BorderLayout.EAST);

        // Sign Out Button
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.setBackground(new Color(244, 67, 54));
        signOutButton.setForeground(Color.WHITE);
        signOutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signOutButton.setFocusPainted(false);
        signOutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutButton.addActionListener(e -> {
            frame.dispose();
            if (logoutCallback != null)
                logoutCallback.run();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(signOutButton);
        topPanel.add(rightPanel, BorderLayout.SOUTH);

        // Physician dropdown
        List<Physician> physicians = physicianManager.getAllPhysicians();
        physicianCombo = new JComboBox<>();
        physicianCombo.addItem(null); // "All"
        for (Physician p : physicians)
            physicianCombo.addItem(p);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Physician:"));
        filterPanel.add(physicianCombo);
        topPanel.add(filterPanel, BorderLayout.NORTH);

        frame.add(topPanel, BorderLayout.NORTH);

        // Appointments Panel
        JPanel appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(new Color(245, 247, 250));
        appointmentsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel appointmentsTitle = new JLabel("Upcoming Appointments");
        appointmentsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appointmentsTitle.setForeground(new Color(34, 40, 49));
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);

        appointmentListModel = new DefaultListModel<>();
        appointmentListDisplay = new JList<>(appointmentListModel);
        appointmentListDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentListDisplay.setBackground(Color.WHITE);
        appointmentListDisplay.setForeground(new Color(34, 40, 49));
        appointmentListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentListDisplay.setCellRenderer(new AppointmentListRenderer(physicianManager));

        JScrollPane appointmentScroll = new JScrollPane(appointmentListDisplay);
        appointmentScroll.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        // Calendar Panels
        selectedDate = LocalDate.now();
        weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        // When creating panels:
        dailyPanel = new DailyAvailabilityPanel(
                currentPhysicianId,
                availabilityService, // <-- pass the real service
                appointmentManager,
                selectedDate);
        weeklyPanel = new WeeklyAvailabilityPanel(
                currentPhysicianId,
                availabilityService, // <-- pass the real service
                appointmentManager,
                weekStart);

        // Navigation for calendar
        JButton prevDayBtn = new JButton("← Prev Day");
        JButton nextDayBtn = new JButton("Next Day →");
        dayLabel = new JLabel("Show Date: " + selectedDate);
        dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dayLabel.setForeground(new Color(34, 40, 49));

        prevDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            updateCalendarPanels();
        });
        nextDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            updateCalendarPanels();
        });

        dayNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dayNav.setBackground(new Color(245, 247, 250));
        dayNav.add(prevDayBtn);
        dayNav.add(dayLabel);
        dayNav.add(nextDayBtn);

        dailyContainer = new JPanel(new BorderLayout());
        dailyContainer.setBackground(new Color(245, 247, 250));
        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        JButton prevWeekBtn = new JButton("← Prev Week");
        JButton nextWeekBtn = new JButton("Next Week →");
        weekLabel = new JLabel("Week of: " + weekStart);
        weekLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weekLabel.setForeground(new Color(34, 40, 49));

        prevWeekBtn.addActionListener(e -> {
            weekStart = weekStart.minusWeeks(1);
            updateCalendarPanels();
        });
        nextWeekBtn.addActionListener(e -> {
            weekStart = weekStart.plusWeeks(1);
            updateCalendarPanels();
        });

        weekNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        weekNav.setBackground(new Color(245, 247, 250));
        weekNav.add(prevWeekBtn);
        weekNav.add(weekLabel);
        weekNav.add(nextWeekBtn);

        weeklyContainer = new JPanel(new BorderLayout());
        weeklyContainer.setBackground(new Color(245, 247, 250));
        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        JTabbedPane calendarTabs = new JTabbedPane();
        calendarTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        calendarTabs.addTab("Daily View", dailyContainer);
        calendarTabs.addTab("Weekly View", weeklyContainer);
        calendarTabs.setPreferredSize(new Dimension(600, 500));

        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                appointmentsPanel,
                calendarTabs);
        centerSplit.setOneTouchExpandable(false);
        frame.add(centerSplit, BorderLayout.CENTER);

        // Listeners
        physicianCombo.addActionListener(e -> {
            updateAppointments();
            updateCalendarPanels();
        });

        // Initial load
        updateAppointments();
        updateCalendarPanels();

        frame.setVisible(true);
    }

    private void updateAppointments() {
        Physician selectedPhysician = (Physician) physicianCombo.getSelectedItem();
        List<Appointment> allAppointments;
        if (selectedPhysician == null) {
            allAppointments = physicianManager.getAllPhysicians().stream()
                    .flatMap(p -> appointmentManager.getAppointmentsForPhysician(p.getId()).stream())
                    .collect(Collectors.toList());
        } else {
            allAppointments = appointmentManager.getAppointmentsForPhysician(selectedPhysician.getId());
        }
        appointmentListModel.clear();
        List<Appointment> filtered = allAppointments.stream()
                .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                .collect(Collectors.toList());
        filtered.forEach(appointmentListModel::addElement);
    }

    private void updateCalendarPanels() {
        Physician selectedPhysician = (Physician) physicianCombo.getSelectedItem();
        currentPhysicianId = (selectedPhysician != null) ? selectedPhysician.getId() : null;

        // Remove old panels
        dailyContainer.removeAll();
        weeklyContainer.removeAll();


// Recreate panels with the new physicianId
dailyPanel = new DailyAvailabilityPanel(
    currentPhysicianId,
    availabilityService, // <-- FIXED
    appointmentManager,
    selectedDate
);
weeklyPanel = new WeeklyAvailabilityPanel(
    currentPhysicianId,
    availabilityService, // <-- FIXED
    appointmentManager,
    weekStart
);


        // Add navigation and new panels back
        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        // Update labels
        dayLabel.setText("Show Date: " + selectedDate);
        weekLabel.setText("Week of: " + weekStart);

        // Refresh UI
        dailyContainer.revalidate();
        dailyContainer.repaint();
        weeklyContainer.revalidate();
        weeklyContainer.repaint();
    }

private void showMessageDialog() {
    JDialog dialog = new JDialog(frame, "Messages", true);

    // Combine physicians and receptionists into one list
    List<Object> allUsers = new java.util.ArrayList<>();
    allUsers.addAll(physicianManager.getAllPhysicians());
    allUsers.addAll(receptionistManager.getAllReceptionists()); // Or use your ReceptionistManager if you have one

    MessagePanel messagePanel = new MessagePanel(messageService, loggedIn.getId(), "receptionist", allUsers);
    dialog.setContentPane(messagePanel);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);
    refreshMessageCount();
}

    private void refreshMessageCount() {
        int unreadCount = messageService.getUnreadMessageCount(loggedIn.getId(), "receptionist");
        messageButton.updateNotificationCount(unreadCount);
    }

    // Custom renderer for appointment list
    private static class AppointmentListRenderer extends DefaultListCellRenderer {
        private final PhysicianManager physicianManager;

        public AppointmentListRenderer(PhysicianManager physicianManager) {
            this.physicianManager = physicianManager;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Appointment a) {
                Physician p = physicianManager.getPhysicianById(a.getPhysicianId());
                String physicianName = (p != null) ? p.getName() : "Unknown";
                label.setText("Appointment with " + a.getPatientName() +
                        " (Physician: " + physicianName + ") on " +
                        a.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")));
            }
            return label;
        }
    }
}