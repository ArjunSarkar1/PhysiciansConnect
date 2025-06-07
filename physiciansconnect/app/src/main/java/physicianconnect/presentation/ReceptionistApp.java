package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.logic.MessageService;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.controller.MessageController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;
import physicianconnect.presentation.util.ProfileImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
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
    private final ReceptionistController receptionistController;
    private final Runnable logoutCallback;
    private final MessageService messageService;
    private final MessageController messageController;
    private final AppointmentController appointmentController;
    private final AvailabilityService availabilityService;

    private JFrame frame;
    private JComboBox<Object> physicianCombo;
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;
    private TableRowSorter<DefaultTableModel> appointmentTableSorter;
    private JTextField appointmentSearchField;
    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;
    private AllPhysiciansDailyPanel allPhysiciansDailyPanel;
    private LocalDate selectedDate;
    private LocalDate weekStart;
    private JLabel dayLabel;
    private JLabel weekLabel;
    private JPanel dailyContainer;
    private JPanel weeklyContainer;
    private JPanel dayNav;
    private JPanel weekNav;
    private MessageButton messageButton;
    private Timer dateTimeTimer;
    private Timer messageRefreshTimer;
    private NotificationPanel notificationPanel;
    private NotificationBanner notificationBanner;
    private JDialog notificationDialog;

    public ReceptionistApp(Receptionist loggedIn, PhysicianManager physicianManager,
                           AppointmentManager appointmentManager, ReceptionistManager receptionistManager, 
                           AppointmentController appointmentController, Runnable logoutCallback) {
        this.loggedIn = loggedIn;
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        this.receptionistManager = receptionistManager;
        this.logoutCallback = logoutCallback;
        this.receptionistController = new ReceptionistController(receptionistManager);
        this.messageService = new MessageService(PersistenceFactory.getMessageRepository());
        this.messageController = new MessageController(messageService);
        this.appointmentController = appointmentController;
        this.availabilityService = new AvailabilityService(
                (physicianconnect.persistence.sqlite.AppointmentDB) PersistenceFactory.getAppointmentPersistence());
        
        // Initialize notification panel
        this.notificationPanel = new NotificationPanel(
            PersistenceFactory.getNotificationPersistence(),
            loggedIn.getId(),
            "receptionist"
        );
        this.notificationDialog = new JDialog(frame, "Notifications", false);
        this.notificationDialog.setContentPane(notificationPanel);
        this.notificationDialog.pack();
        this.notificationDialog.setLocationRelativeTo(frame);
        
        // Register appointment callbacks
        appointmentController.setOnAppointmentCreated(this::onAppointmentCreated);
        appointmentController.setOnAppointmentUpdated(this::onAppointmentUpdated);
        appointmentController.setOnAppointmentDeleted(this::onAppointmentDeleted);
        
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame(UIConfig.RECEPTIONIST_DASHBOARD_TITLE + " - " + loggedIn.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel(UIConfig.WELCOME_PREFIX + loggedIn.getName());
        welcome.setFont(UITheme.HEADER_FONT);
        welcome.setForeground(UITheme.TEXT_COLOR);
        topPanel.add(welcome, BorderLayout.WEST);

        // Profile photo button (optional, if you want to add for receptionists)
        ImageIcon profileIcon = ProfileImageUtil.getProfileIcon(loggedIn.getId());
        JButton profilePicButton = new JButton(profileIcon);
        profilePicButton.setToolTipText(UIConfig.PROFILE_BUTTON_TEXT);
        profilePicButton.setPreferredSize(new Dimension(40, 40));
        profilePicButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        profilePicButton.setContentAreaFilled(false);
        profilePicButton.setFocusPainted(false);
        profilePicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // profilePicButton.addActionListener(e -> openProfileDialog()); // Implement if you have ReceptionistProfilePanel

        // Physician dropdown
        List<Physician> physicians = physicianManager.getAllPhysicians();
        physicianCombo = new JComboBox<>();
        physicianCombo.addItem(UIConfig.ALL_PHYSICIANS_LABEL);
        for (Physician p : physicians)
            physicianCombo.addItem(p);

        // Date/Time Label
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(UITheme.LABEL_FONT);
        dateTimeLabel.setForeground(UITheme.TEXT_COLOR);
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        dateTimeTimer = new Timer(1000, e -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        });
        dateTimeTimer.start();

        // Message Button
        messageButton = new MessageButton();
        messageButton.setOnAction(e -> showMessageDialog());

        // Add notification button
        JButton notificationButton = createStyledButton("Alerts");
        notificationButton.addActionListener(e -> showNotificationPanel());

        // Right-aligned panel for physician dropdown, date/time, and message button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(new JLabel(UIConfig.PHYSICIAN_LABEL));
        rightPanel.add(physicianCombo);
        rightPanel.add(dateTimeLabel);
        rightPanel.add(notificationButton);
        rightPanel.add(messageButton);
        rightPanel.add(profilePicButton);

        topPanel.add(rightPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Appointments Panel
        JPanel appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(UITheme.BACKGROUND_COLOR);
        appointmentsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel appointmentsTitle = new JLabel(UIConfig.APPOINTMENTS_TITLE);
        appointmentsTitle.setFont(UITheme.HEADER_FONT);
        appointmentsTitle.setForeground(UITheme.TEXT_COLOR);
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);

        // Add search bar under the title
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel searchLabel = new JLabel(UIConfig.SEARCH_PATIENT_LABEL);
        searchLabel.setFont(UITheme.LABEL_FONT);
        appointmentSearchField = new JTextField();
        appointmentSearchField.setFont(UITheme.LABEL_FONT);
        appointmentSearchField.putClientProperty("JTextField.placeholderText", UIConfig.SEARCH_PATIENT_PLACEHOLDER);
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(appointmentSearchField, BorderLayout.CENTER);
        appointmentsPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table for appointments
        String[] columns = { UIConfig.PATIENT_LABEL, UIConfig.PHYSICIAN_LABEL, UIConfig.DATE_LABEL, UIConfig.TIME_LABEL };
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setFont(UITheme.LABEL_FONT);
        appointmentTable.setRowHeight(28);
        appointmentTable.getTableHeader().setFont(UITheme.HEADER_FONT);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setAutoCreateRowSorter(true);

        appointmentTableSorter = new TableRowSorter<>(appointmentTableModel);
        appointmentTable.setRowSorter(appointmentTableSorter);

        appointmentSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterAppointments(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterAppointments(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterAppointments(); }
        });

        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentScroll.setPreferredSize(new Dimension(600, 300));
        appointmentScroll.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        // Calendar Panels
        selectedDate = LocalDate.now();
        weekStart = selectedDate.with(java.time.DayOfWeek.MONDAY);

        dailyPanel = new DailyAvailabilityPanel(
                null, // will be set in updateCalendarPanels
                availabilityService,
                appointmentController,
                selectedDate,
                () -> weeklyPanel.loadWeek(selectedDate.with(java.time.DayOfWeek.MONDAY)));
        weeklyPanel = new WeeklyAvailabilityPanel(
                null, // will be set in updateCalendarPanels
                availabilityService,
                appointmentController,
                weekStart,
                () -> dailyPanel.loadSlotsForDate(dailyPanel.getCurrentDate()));

        // Navigation for calendar
        JButton prevDayBtn = new JButton(UIConfig.PREV_DAY_BUTTON_TEXT);
        JButton nextDayBtn = new JButton(UIConfig.NEXT_DAY_BUTTON_TEXT);
        dayLabel = new JLabel(UIConfig.LABEL_SHOW_DATE + selectedDate);
        dayLabel.setFont(UITheme.LABEL_FONT);
        dayLabel.setForeground(UITheme.TEXT_COLOR);

        prevDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            updateCalendarPanels();
        });
        nextDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            updateCalendarPanels();
        });

        dayNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dayNav.setBackground(UITheme.BACKGROUND_COLOR);
        dayNav.add(prevDayBtn);
        dayNav.add(dayLabel);
        dayNav.add(nextDayBtn);

        dailyContainer = new JPanel(new BorderLayout());
        dailyContainer.setBackground(UITheme.BACKGROUND_COLOR);
        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        JButton prevWeekBtn = new JButton(UIConfig.PREV_WEEK_BUTTON_TEXT);
        JButton nextWeekBtn = new JButton(UIConfig.NEXT_WEEK_BUTTON_TEXT);
        weekLabel = new JLabel(UIConfig.LABEL_WEEK_OF + weekStart);
        weekLabel.setFont(UITheme.LABEL_FONT);
        weekLabel.setForeground(UITheme.TEXT_COLOR);

        prevWeekBtn.addActionListener(e -> {
            weekStart = weekStart.minusWeeks(1);
            updateCalendarPanels();
        });
        nextWeekBtn.addActionListener(e -> {
            weekStart = weekStart.plusWeeks(1);
            updateCalendarPanels();
        });

        weekNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        weekNav.setBackground(UITheme.BACKGROUND_COLOR);
        weekNav.add(prevWeekBtn);
        weekNav.add(weekLabel);
        weekNav.add(nextWeekBtn);

        weeklyContainer = new JPanel(new BorderLayout());
        weeklyContainer.setBackground(UITheme.BACKGROUND_COLOR);
        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        JTabbedPane calendarTabs = new JTabbedPane();
        calendarTabs.setFont(UITheme.LABEL_FONT);
        calendarTabs.addTab(UIConfig.TAB_DAILY_VIEW, dailyContainer);
        calendarTabs.addTab(UIConfig.TAB_WEEKLY_VIEW, weeklyContainer);
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
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addAppointmentButton = createStyledButton(UIConfig.ADD_APPOINTMENT_BUTTON_TEXT);
        JButton viewAppointmentButton = createStyledButton(UIConfig.VIEW_APPOINTMENTS_BUTTON_TEXT);
        JButton signOutButton = createStyledButton(UIConfig.LOGOUT_BUTTON_TEXT);

        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);
        buttonPanel.add(signOutButton);

        // Add listeners for buttons
        addAppointmentButton.addActionListener(e -> {
            Object selected = physicianCombo.getSelectedItem();
            Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
            if (selectedPhysician == null) {
                JOptionPane.showMessageDialog(frame, UIConfig.ERROR_NO_PHYSICIAN_SELECTED,
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.WARNING_MESSAGE);
                return;
            }
            AddAppointmentDialog dlg = new AddAppointmentDialog(
                    frame,
                    appointmentController,
                    selectedPhysician.getId(),
                    this::updateAppointments);
            dlg.setVisible(true);
        });

        viewAppointmentButton.addActionListener(e -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, UIConfig.ERROR_NO_APPOINTMENT_SELECTED,
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
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
                String pName = (p != null) ? p.getName() : UIConfig.UNKNOWN_PHYSICIAN_LABEL;
                String date = a.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.DATE_FORMAT));
                String time = a.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.TIME_FORMAT));
                if (a.getPatientName().equals(patientName) && pName.equals(physicianName)
                        && date.equals(dateStr) && time.equals(timeStr)) {
                    selectedAppt = a;
                    break;
                }
            }
            if (selectedAppt == null) {
                JOptionPane.showMessageDialog(frame, UIConfig.ERROR_APPOINTMENT_NOT_FOUND,
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                    frame,
                    appointmentController,
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
        appointmentTableModel.setRowCount(0);
        List<Appointment> appointments;
        
        Object selected = physicianCombo.getSelectedItem();
        if (selected instanceof Physician) {
            appointments = appointmentManager.getAppointmentsForPhysician(((Physician) selected).getId());
        } else {
            // Get all appointments by combining appointments from all physicians
            appointments = physicianManager.getAllPhysicians().stream()
                    .flatMap(p -> appointmentManager.getAppointmentsForPhysician(p.getId()).stream())
                    .collect(Collectors.toList());
        }

        for (Appointment apt : appointments) {
            String physicianName = apt.getPhysicianId() != null
                    ? physicianManager.getPhysicianById(apt.getPhysicianId()).getName()
                    : UIConfig.UNKNOWN_PHYSICIAN_LABEL;
            
            appointmentTableModel.addRow(new Object[] {
                apt.getPatientName(),
                    physicianName,
                apt.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.DATE_FORMAT)),
                apt.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.TIME_FORMAT))
            });
        }
    }

    private void filterAppointments() {
        String searchText = appointmentSearchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            appointmentTableSorter.setRowFilter(null);
        } else {
            appointmentTableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void updateCalendarPanels() {
        if (dailyPanel != null) {
            dailyPanel.revalidate();
            dailyPanel.repaint();
        }
        if (weeklyPanel != null) {
            weeklyPanel.revalidate();
            weeklyPanel.repaint();
        }
        if (allPhysiciansDailyPanel != null) {
            allPhysiciansDailyPanel.revalidate();
            allPhysiciansDailyPanel.repaint();
        }
    }

    private void showNotificationPanel() {
        if (notificationDialog == null) {
            notificationDialog = new JDialog(frame, "Notifications", false);
            notificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                loggedIn.getId(),
                "receptionist"
            );
            notificationDialog.setContentPane(notificationPanel);
            notificationDialog.pack();
            notificationDialog.setLocationRelativeTo(frame);
        }
        notificationDialog.setVisible(true);
    }

    private void showNotificationBanner(String message, java.awt.event.ActionListener onClick) {
        // Only show banner if the frame is visible (user is logged in)
        if (frame != null && frame.isVisible()) {
            if (notificationBanner == null) {
                notificationBanner = new NotificationBanner(frame);
            }
            notificationBanner.show(message, onClick);
        }
    }

    private void showMessageDialog() {
        JDialog dialog = new JDialog(frame, UIConfig.MESSAGES_DIALOG_TITLE, true);

        // Combine physicians and receptionists into one list
        List<Object> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(physicianManager.getAllPhysicians());
        allUsers.addAll(receptionistManager.getAllReceptionists());

        MessagePanel messagePanel = new MessagePanel(messageController, loggedIn.getId(), "receptionist", allUsers);
        dialog.setContentPane(messagePanel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        refreshMessageCount();
    }

    private void refreshMessageCount() {
        int unreadCount = messageService.getUnreadMessageCount(loggedIn.getId(), "receptionist");
        messageButton.updateNotificationCount(unreadCount);
        
        // Show banner for new messages
        if (unreadCount > 0) {
            showNotificationBanner("New message received", e -> showMessageDialog());
            if (notificationPanel != null) {
                notificationPanel.addNotification("New message received", "Message");
            }
        }
    }

    private void notifyAppointmentChange(String message, String type) {
        // Always add to notification panel for persistence
        if (notificationPanel == null) {
            notificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                loggedIn.getId(),
                "receptionist"
            );
        }
        notificationPanel.addNotification(message, type);

        // Only show banner if user is logged in
        if (frame != null && frame.isVisible()) {
            showNotificationBanner(message, e -> {
                // Refresh the calendar views
                if (dailyPanel != null) {
                    dailyPanel.revalidate();
                    dailyPanel.repaint();
                }
                if (weeklyPanel != null) {
                    weeklyPanel.revalidate();
                    weeklyPanel.repaint();
                }
                updateAppointments();
            });
        }
    }

    // Add this method to handle appointment updates from the controller
    public void onAppointmentUpdated(Appointment appointment) {
        String physicianName = physicianManager.getPhysicianById(appointment.getPhysicianId()).getName();
        String message = String.format("Appointment notes for %s and %s has been updated.", 
            physicianName,
            appointment.getPatientName());
        notifyAppointmentChange(message, "Appointment Update!");
        
        // Notify the physician about the update
        Physician physician = physicianManager.getPhysicianById(appointment.getPhysicianId());
        if (physician != null) {
            String physicianMessage = String.format("Appointment with %s has been updated.", 
                appointment.getPatientName());
            
            // Create a new notification panel for the physician to store the notification
            NotificationPanel physicianNotificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                physician.getId(),
                "physician"
            );
            physicianNotificationPanel.addNotification(physicianMessage, "Appointment Update!");
        }
    }

    // Add this method to handle appointment deletions from the controller
    public void onAppointmentDeleted(Appointment appointment) {
        String physicianName = physicianManager.getPhysicianById(appointment.getPhysicianId()).getName();
        String message = String.format("Appointment for %s and %s has been deleted.", 
            physicianName,
            appointment.getPatientName());
        notifyAppointmentChange(message, "Appointment Cancellation!");
        
        // Notify the physician about the deletion
        Physician physician = physicianManager.getPhysicianById(appointment.getPhysicianId());
        if (physician != null) {
            String physicianMessage = String.format("Appointment with %s has been cancelled.", 
                appointment.getPatientName());
            
            // Create a new notification panel for the physician to store the notification
            NotificationPanel physicianNotificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                physician.getId(),
                "physician"
            );
            physicianNotificationPanel.addNotification(physicianMessage, "Appointment Cancellation!");
        }
    }

    // Add this method to handle new appointments from the controller
    public void onAppointmentCreated(Appointment appointment) {
        String physicianName = physicianManager.getPhysicianById(appointment.getPhysicianId()).getName();
        String message = String.format("New appointment set for %s and %s.", 
            physicianName,
            appointment.getPatientName());
        notifyAppointmentChange(message, "New Appointment!");
        
        // Notify the physician about the new appointment
        Physician physician = physicianManager.getPhysicianById(appointment.getPhysicianId());
        if (physician != null) {
            String physicianMessage = String.format("New appointment scheduled with %s.", 
                appointment.getPatientName());
            
            // Create a new notification panel for the physician to store the notification
            NotificationPanel physicianNotificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                physician.getId(),
                "physician"
            );
            physicianNotificationPanel.addNotification(physicianMessage, "New Appointment!");
        }
    }

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
}