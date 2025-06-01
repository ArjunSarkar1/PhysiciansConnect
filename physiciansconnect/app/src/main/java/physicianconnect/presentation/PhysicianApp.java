package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReferralManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.interfaces.MedicationPersistence;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;
import physicianconnect.persistence.sqlite.AppointmentDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PhysicianApp {
    private JFrame frame;
    private DefaultListModel<Appointment> appointmentListModel;
    private final Physician loggedIn;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;
    private LocalDate selectedDate;     // for daily navigation (e.g. today, yesterday, tomorrow, …)
    private LocalDate weekStart;        // Monday of the currently shown week

    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color POSITIVE_COLOR = new Color(76, 175, 80);
    private static final Color DESTRUCTIVE_COLOR = new Color(244, 67, 54);
    private static final Color SELECTION_COLOR = new Color(30, 41, 59);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(34, 40, 49);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public PhysicianApp(Physician loggedIn, PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        this.loggedIn = loggedIn;
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Dashboard - " + loggedIn.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel("Welcome, " + loggedIn.getName());
        welcome.setFont(TITLE_FONT);
        welcome.setForeground(TEXT_COLOR);
    topPanel.add(welcome, BorderLayout.WEST);

    JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(LABEL_FONT);
        dateTimeLabel.setForeground(TEXT_COLOR);
    dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    topPanel.add(dateTimeLabel, BorderLayout.EAST);

    Timer timer = new Timer(1000, e -> {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dateTimeLabel.setText(now);
    });
    timer.start();

        frame.add(topPanel, BorderLayout.NORTH);

        // Appointments Panel
        JPanel appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(BACKGROUND_COLOR);
        appointmentsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel appointmentsTitle = new JLabel("Your Appointments");
        appointmentsTitle.setFont(TITLE_FONT);
        appointmentsTitle.setForeground(TEXT_COLOR);
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);

        appointmentListModel = new DefaultListModel<>();
        JList<Appointment> appointmentListDisplay = new JList<>(appointmentListModel);
        appointmentListDisplay.setFont(LABEL_FONT);
        appointmentListDisplay.setBackground(Color.WHITE);
        appointmentListDisplay.setForeground(TEXT_COLOR);
        appointmentListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentListDisplay.setCellRenderer(new ListCardRenderer<>());
        
        JScrollPane appointmentScroll = new JScrollPane(appointmentListDisplay);
        appointmentScroll.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        frame.add(appointmentsPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addAppointmentButton = createStyledButton("📅 Add Appointment");
        JButton viewAppointmentButton = createStyledButton("🔍 View Appointment");
        JButton historyButton = createStyledButton("🗂 Patient History");
        JButton prescribeButton = createStyledButton("💊 Prescribe Medicine");
        JButton referralButton = createStyledButton("📄 Manage Referrals");
        JButton signOutButton = createStyledButton("🚪 Sign Out");

        addAppointmentButton.addActionListener(e -> {
            new AddAppointmentDialog(frame, appointmentManager, loggedIn.getId()).setVisible(true);
            refreshAppointments();
        });

        viewAppointmentButton.addActionListener(e -> {
            Appointment selected = appointmentListDisplay.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Please select an appointment to view.",
                        "No Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new ViewAppointmentDialog(frame, appointmentManager, selected).setVisible(true);
            refreshAppointments();
        });

        historyButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Patient Medical History", true);
            PatientHistoryPanel historyPanel = new PatientHistoryPanel(
                    appointmentManager,
                    PersistenceFactory.getPrescriptionPersistence(),
                    loggedIn.getId()
            );
            dialog.setContentPane(historyPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        prescribeButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Prescribe Medicine", true);
            dialog.setContentPane(new PrescribeMedicinePanel(
                    appointmentManager,
                    PersistenceFactory.getMedicationPersistence(),
                    PersistenceFactory.getPrescriptionPersistence(),
                    loggedIn.getId(),
                    null
            ));
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

        signOutButton.addActionListener(e -> {
            frame.dispose();
            new LoginScreen(physicianManager, appointmentManager);
        });

        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(prescribeButton);
        buttonPanel.add(referralButton);
        buttonPanel.add(signOutButton);

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:prod.db");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not open the database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return; // abort UI init if we cannot connect
        }

        // 2) Construct your AppointmentDB (requires a Connection):
        AppointmentDB apptDb = new AppointmentDB(conn);

        // 3) Build AvailabilityService with that AppointmentDB:
        AvailabilityService availabilityService = new AvailabilityService(apptDb);


        // 3) Initialize the dates for navigation
        selectedDate = LocalDate.now();
        weekStart    = LocalDate.now().with(DayOfWeek.MONDAY);

        // 4) Create the two panels, passing an int physicianId
        int docId = Integer.parseInt(loggedIn.getId());
        dailyPanel  = new DailyAvailabilityPanel(docId, availabilityService, selectedDate);
        weeklyPanel = new WeeklyAvailabilityPanel(docId, availabilityService, weekStart);

        // 5) “Prev/Next Day” buttons
        JButton prevDayBtn = new JButton("← Prev Day");
        JButton nextDayBtn = new JButton("Next Day →");


        prevDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
        });
        nextDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
        });

        JPanel dayNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dayNav.setBackground(BACKGROUND_COLOR);
        JLabel dayLabel = new JLabel("Show Date: " + selectedDate);
        dayLabel.setFont(LABEL_FONT);
        dayLabel.setForeground(TEXT_COLOR);

        prevDayBtn.addActionListener(e -> dayLabel.setText("Show Date: " + selectedDate));
        nextDayBtn.addActionListener(e -> dayLabel.setText("Show Date: " + selectedDate));

        dayNav.add(prevDayBtn);
        dayNav.add(dayLabel);
        dayNav.add(nextDayBtn);

        JPanel dailyContainer = new JPanel(new BorderLayout());
        dailyContainer.setBackground(BACKGROUND_COLOR);
        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        // 6) “Prev/Next Week” buttons
        JButton prevWeekBtn = new JButton("← Prev Week");
        JButton nextWeekBtn = new JButton("Next Week →");


        prevWeekBtn.addActionListener(e -> {
            weekStart = weekStart.minusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
        });
        nextWeekBtn.addActionListener(e -> {
            weekStart = weekStart.plusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
        });

        JPanel weekNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        weekNav.setBackground(BACKGROUND_COLOR);
        JLabel weekLabel = new JLabel("Week of: " + weekStart);
        weekLabel.setFont(LABEL_FONT);
        weekLabel.setForeground(TEXT_COLOR);

        prevWeekBtn.addActionListener(e -> weekLabel.setText("Week of: " + weekStart));
        nextWeekBtn.addActionListener(e -> weekLabel.setText("Week of: " + weekStart));

        weekNav.add(prevWeekBtn);
        weekNav.add(weekLabel);
        weekNav.add(nextWeekBtn);

        JPanel weeklyContainer = new JPanel(new BorderLayout());
        weeklyContainer.setBackground(BACKGROUND_COLOR);
        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        // 7) Combine into a JTabbedPane
        JTabbedPane availabilityTabs = new JTabbedPane();
        availabilityTabs.setFont(LABEL_FONT);
        availabilityTabs.addTab("Daily View", dailyContainer);
        availabilityTabs.addTab("Weekly View", weeklyContainer);
        availabilityTabs.setPreferredSize(new Dimension(600, 500));

        // 8) Use JSplitPane to show appointments on left, availability on right
        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                appointmentsPanel,
                availabilityTabs
        );
        centerSplit.setResizeWeight(0.3);
        centerSplit.setBackground(BACKGROUND_COLOR);
        centerSplit.setOneTouchExpandable(true);

        frame.add(centerSplit, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        refreshAppointments();
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.brighter());
            }
        });

        return button;
    }

    private void refreshAppointments() {
        appointmentListModel.clear();
        List<Appointment> appointments = appointmentManager.getAppointmentsForPhysician(loggedIn.getId());
        for (Appointment a : appointments) {
            appointmentListModel.addElement(a);
        }
    }

    public static void launchSingleUser(Physician loggedIn, PhysicianManager physicianManager,
                                        AppointmentManager appointmentManager) {
        new PhysicianApp(loggedIn, physicianManager, appointmentManager);
    }

    private static class ListCardRenderer<T> extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isSelected ? PRIMARY_COLOR : Color.LIGHT_GRAY, 2),
                    new EmptyBorder(10, 10, 10, 10)));
            label.setBackground(isSelected ? SELECTION_COLOR : Color.WHITE);
            label.setForeground(isSelected ? Color.WHITE : TEXT_COLOR);
            label.setOpaque(true);
            label.setFont(LABEL_FONT);
            return label;
        }
    }
}