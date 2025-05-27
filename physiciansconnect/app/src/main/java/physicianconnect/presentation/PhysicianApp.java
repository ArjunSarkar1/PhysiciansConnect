package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.MedicationPersistence;
import physicianconnect.persistence.PrescriptionPersistence;
import physicianconnect.persistence.PersistenceFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PhysicianApp {
    private JFrame frame;
    private DefaultListModel<Appointment> appointmentListModel;
    private final Physician loggedIn;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;

    public PhysicianApp(Physician loggedIn, PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        this.loggedIn = loggedIn;
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Dashboard - " + loggedIn.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        frame.setContentPane(contentPanel);

        Font baseFont = new Font("SansSerif", Font.PLAIN, 14);

            // Top panel for welcome and clock
    JPanel topPanel = new JPanel(new BorderLayout());
    JLabel welcome = new JLabel("Welcome, " + loggedIn.getName() + " (" + loggedIn.getEmail() + ")");
    welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
    topPanel.add(welcome, BorderLayout.WEST);

    // Date/time label (top right)
    JLabel dateTimeLabel = new JLabel();
    dateTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
    dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    topPanel.add(dateTimeLabel, BorderLayout.EAST);

    // Timer to update the clock every second
    Timer timer = new Timer(1000, e -> {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dateTimeLabel.setText(now);
    });
    timer.start();

    contentPanel.add(topPanel, BorderLayout.NORTH);

        // Appointments list
        appointmentListModel = new DefaultListModel<>();
        JList<Appointment> appointmentListDisplay = new JList<>(appointmentListModel);
        appointmentListDisplay.setFont(baseFont);
        appointmentListDisplay.setCellRenderer(new ListCardRenderer<>());
        appointmentListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane appointmentScroll = new JScrollPane(appointmentListDisplay);
        appointmentScroll.setBorder(BorderFactory.createTitledBorder("Your Appointments"));
        contentPanel.add(appointmentScroll, BorderLayout.CENTER);

        // Persistence instances
        MedicationPersistence medicationPersistence = PersistenceFactory.getMedicationPersistence();
        PrescriptionPersistence prescriptionPersistence = PersistenceFactory.getPrescriptionPersistence();

        // Panels for dialogs
        PatientHistoryPanel[] historyPanelHolder = new PatientHistoryPanel[1]; // for callback access

        // Buttons
        JButton addAppointmentButton = createButton("📅 Add Appointment");
        JButton viewAppointmentButton = createButton("🔍 View Appointment");
        JButton historyButton = createButton("🗂 Patient History");
        JButton prescribeButton = createButton("💊 Prescribe Medicine");
        JButton signOutButton = createButton("🚪 Sign Out");

        addAppointmentButton.addActionListener(e -> {
            new AddAppointmentDialog(frame, appointmentManager, loggedIn.getId()).setVisible(true);
            refreshAppointments();
        });

        viewAppointmentButton.addActionListener(e -> {
            Appointment selected = appointmentListDisplay.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select an appointment to view.");
                return;
            }
            new ViewAppointmentDialog(frame, appointmentManager, selected).setVisible(true);
            refreshAppointments();
        });

        historyButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Patient Medical History", true);
            PatientHistoryPanel historyPanel = new PatientHistoryPanel(
                    appointmentManager,
                    prescriptionPersistence,
                    loggedIn.getId()
            );
            historyPanelHolder[0] = historyPanel;
            dialog.setContentPane(historyPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        prescribeButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Prescribe Medicine", true);
            // Use callback to update history if open
            Runnable onPrescriptionAdded = () -> {
                if (historyPanelHolder[0] != null) {
                    historyPanelHolder[0].updateHistory();
                }
            };
            dialog.setContentPane(new PrescribeMedicinePanel(
                    appointmentManager,
                    medicationPersistence,
                    prescriptionPersistence,
                    loggedIn.getId(),
                    onPrescriptionAdded
            ));
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });

        signOutButton.addActionListener(e -> {
            frame.dispose();
            new LoginScreen(physicianManager, appointmentManager);
        });

        //JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(prescribeButton);
        buttonPanel.add(signOutButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshAppointments();
        frame.setVisible(true);
    }

    private void refreshAppointments() {
        appointmentListModel.clear();
        List<Appointment> appointments = appointmentManager.getAppointmentsForPhysician(loggedIn.getId());
        for (Appointment a : appointments) {
            appointmentListModel.addElement(a);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
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
                    BorderFactory.createLineBorder(isSelected ? Color.BLUE : Color.LIGHT_GRAY, 1),
                    new EmptyBorder(10, 10, 10, 10)));
            label.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);
            label.setOpaque(true);
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            return label;
        }
    }
}