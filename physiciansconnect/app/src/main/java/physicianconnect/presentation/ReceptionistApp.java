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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

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
    private JComboBox<Object> physicianCombo;
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;
    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;
    private LocalDate selectedDate;
    private LocalDate weekStart;
    private JLabel dayLabel;
    private JLabel weekLabel;
    private Timer dateTimeTimer;
    private String currentPhysicianId;
    private JPanel dailyContainer;
    private JPanel weeklyContainer;
    private JPanel dayNav;
    private JPanel weekNav;
    private MessageButton messageButton;
    private JTextField appointmentSearchField;
    private TableRowSorter<DefaultTableModel> appointmentTableSorter;
    private AllPhysiciansDailyPanel allPhysiciansDailyPanel;

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

        // Physician dropdown
        List<Physician> physicians = physicianManager.getAllPhysicians();
        physicianCombo = new JComboBox<>();
        physicianCombo.addItem("ALL PHYSICIANS");
        for (Physician p : physicians)
            physicianCombo.addItem(p);

        // Date/Time Label
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setForeground(new Color(34, 40, 49));
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        dateTimeTimer = new Timer(1000, e -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        });
        dateTimeTimer.start();

        // Message Button
        messageButton = new MessageButton();
        messageButton.setOnAction(e -> showMessageDialog());

        // Right-aligned panel for physician dropdown, date/time, and message button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(new JLabel("Physician:"));
        rightPanel.add(physicianCombo);
        rightPanel.add(dateTimeLabel);
        rightPanel.add(messageButton);

        topPanel.add(rightPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Appointments Panel
        JPanel appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(new Color(245, 247, 250));
        appointmentsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel appointmentsTitle = new JLabel("Upcoming Appointments");
        appointmentsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appointmentsTitle.setForeground(new Color(34, 40, 49));
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);

        // Add search bar under the title
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel("Search Patient: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentSearchField = new JTextField();
        appointmentSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentSearchField.putClientProperty("JTextField.placeholderText", "Type patient name...");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(appointmentSearchField, BorderLayout.CENTER);
        appointmentsPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table for appointments
        String[] columns = { "Patient Name", "Physician", "Date", "Time" };
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        appointmentTable.setRowHeight(28);
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setAutoCreateRowSorter(true);

        appointmentTableSorter = new TableRowSorter<>(appointmentTableModel);
        appointmentTable.setRowSorter(appointmentTableSorter);

        appointmentSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterAppointments();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterAppointments();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterAppointments();
            }
        });

        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentScroll.setPreferredSize(new Dimension(600, 300));
        appointmentScroll.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        // Calendar Panels
        selectedDate = LocalDate.now();
        weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        dailyPanel = new DailyAvailabilityPanel(
                currentPhysicianId,
                availabilityService,
                appointmentManager,
                selectedDate);
        weeklyPanel = new WeeklyAvailabilityPanel(
                currentPhysicianId,
                availabilityService,
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
        centerSplit.setDividerLocation(600);

        // --- Bottom Button Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addAppointmentButton = new JButton("Add Appointment");
        JButton viewAppointmentButton = new JButton("View Appointment");
        JButton signOutButton = new JButton("Sign Out");

        addAppointmentButton.setBackground(new Color(33, 150, 243));
        addAppointmentButton.setForeground(Color.WHITE);
        addAppointmentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addAppointmentButton.setFocusPainted(false);

        viewAppointmentButton.setBackground(new Color(76, 175, 80));
        viewAppointmentButton.setForeground(Color.WHITE);
        viewAppointmentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAppointmentButton.setFocusPainted(false);

        signOutButton.setBackground(new Color(244, 67, 54));
        signOutButton.setForeground(Color.WHITE);
        signOutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signOutButton.setFocusPainted(false);

        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);
        buttonPanel.add(signOutButton);

        // Add listeners for buttons
        addAppointmentButton.addActionListener(e -> {
            Object selected = physicianCombo.getSelectedItem();
            Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
            if (selectedPhysician == null) {
                JOptionPane.showMessageDialog(frame, "Please select a physician to add an appointment.",
                        "No Physician Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            AddAppointmentDialog dlg = new AddAppointmentDialog(
                    frame,
                    appointmentManager,
                    selectedPhysician.getId(),
                    this::updateAppointments);
            dlg.setVisible(true);
        });

        viewAppointmentButton.addActionListener(e -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an appointment to view.", "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            String patientName = (String) appointmentTableModel.getValueAt(modelRow, 0);
            String physicianName = (String) appointmentTableModel.getValueAt(modelRow, 1);
            String dateStr = (String) appointmentTableModel.getValueAt(modelRow, 2);
            String timeStr = (String) appointmentTableModel.getValueAt(modelRow, 3);

            // Find the matching Appointment object
            List<Appointment> allAppointments = physicianManager.getAllPhysicians().stream()
                    .flatMap(p -> appointmentManager.getAppointmentsForPhysician(p.getId()).stream())
                    .collect(Collectors.toList());
            Appointment selectedAppt = null;
            for (Appointment a : allAppointments) {
                Physician p = physicianManager.getPhysicianById(a.getPhysicianId());
                String pName = (p != null) ? p.getName() : "Unknown";
                String date = a.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                String time = a.getDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
                if (a.getPatientName().equals(patientName) && pName.equals(physicianName)
                        && date.equals(dateStr) && time.equals(timeStr)) {
                    selectedAppt = a;
                    break;
                }
            }
            if (selectedAppt == null) {
                JOptionPane.showMessageDialog(frame, "Could not find the selected appointment.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                    frame,
                    appointmentManager,
                    selectedAppt,
                    this::updateAppointments);
            viewDlg.setVisible(true);
        });

        signOutButton.addActionListener(e -> {
            frame.dispose();
            if (logoutCallback != null)
                logoutCallback.run();
        });

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        physicianCombo.addActionListener(e -> {
            updateAppointments();
            updateCalendarPanels();
        });

        // Initial load
        updateAppointments();
        updateCalendarPanels();

        appointmentManager.addChangeListener(this::updateAppointments);

        frame.setVisible(true);
    }

    private void updateAppointments() {
        Object selected = physicianCombo.getSelectedItem();
        Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
        List<Appointment> allAppointments;
        if (selectedPhysician == null) {
            allAppointments = physicianManager.getAllPhysicians().stream()
                    .flatMap(p -> appointmentManager.getAppointmentsForPhysician(p.getId()).stream())
                    .collect(Collectors.toList());
        } else {
            allAppointments = appointmentManager.getAppointmentsForPhysician(selectedPhysician.getId());
        }
        appointmentTableModel.setRowCount(0);
        List<Appointment> filtered = allAppointments.stream()
                .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                .collect(Collectors.toList());
        for (Appointment a : filtered) {
            Physician p = physicianManager.getPhysicianById(a.getPhysicianId());
            String physicianName = (p != null) ? p.getName() : "Unknown";
            String date = a.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            String time = a.getDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
            appointmentTableModel.addRow(new Object[] {
                    a.getPatientName(),
                    physicianName,
                    date,
                    time
            });
        }
    }

    private void filterAppointments() {
        String text = appointmentSearchField.getText();
        if (text.trim().length() == 0) {
            appointmentTableSorter.setRowFilter(null);
        } else {
            appointmentTableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0)); // 0 = Patient Name column
        }
    }

    private void updateCalendarPanels() {
        Object selected = physicianCombo.getSelectedItem();
        boolean allPhysiciansSelected = selected instanceof String && "ALL PHYSICIANS".equals(selected);
        Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
        currentPhysicianId = (selectedPhysician != null) ? selectedPhysician.getId() : null;

        // Remove old panels
        dailyContainer.removeAll();
        weeklyContainer.removeAll();

        // Remove all tabs and add only the relevant ones
        JTabbedPane calendarTabs = null;
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JSplitPane split) {
                Component right = split.getRightComponent();
                if (right instanceof JTabbedPane tabs) {
                    calendarTabs = tabs;
                    tabs.removeAll();
                    break;
                }
            }
        }

        if (allPhysiciansSelected) {
            // Show only daily view for all physicians
            allPhysiciansDailyPanel = new AllPhysiciansDailyPanel(physicianManager, appointmentManager,
                    availabilityService, selectedDate, newDate -> {
                        selectedDate = newDate;
                        updateCalendarPanels();
                    });
            if (calendarTabs != null) {
                calendarTabs.addTab("Daily View", allPhysiciansDailyPanel);
            }
        } else {
            // Show daily and weekly for a specific physician
            dailyPanel = new DailyAvailabilityPanel(
                    currentPhysicianId,
                    availabilityService,
                    appointmentManager,
                    selectedDate);
            weeklyPanel = new WeeklyAvailabilityPanel(
                    currentPhysicianId,
                    availabilityService,
                    appointmentManager,
                    weekStart);

            dailyContainer.add(dayNav, BorderLayout.NORTH);
            dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

            weeklyContainer.add(weekNav, BorderLayout.NORTH);
            weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

            if (calendarTabs != null) {
                calendarTabs.addTab("Daily View", dailyContainer);
                calendarTabs.addTab("Weekly View", weeklyContainer);
            }
        }

        // Update labels
        dayLabel.setText("Show Date: " + selectedDate);
        weekLabel.setText("Week of: " + weekStart);

        // Refresh UI
        if (calendarTabs != null) {
            calendarTabs.revalidate();
            calendarTabs.repaint();
        }
    }

    private void showMessageDialog() {
        JDialog dialog = new JDialog(frame, "Messages", true);

        // Combine physicians and receptionists into one list
        List<Object> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(physicianManager.getAllPhysicians());
        allUsers.addAll(receptionistManager.getAllReceptionists());

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
}