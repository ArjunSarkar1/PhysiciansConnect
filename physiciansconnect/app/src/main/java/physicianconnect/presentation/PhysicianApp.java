package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PhysicianApp {
    private JFrame frame;
    private JList<Physician> physicianListDisplay;
    private DefaultListModel<Physician> physicianListModel;

    private JList<Appointment> appointmentListDisplay;
    private DefaultListModel<Appointment> appointmentListModel;

    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;

    public PhysicianApp(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("PhysicianConnect");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        frame.setContentPane(contentPanel);

        // Custom font
        Font baseFont = new Font("SansSerif", Font.PLAIN, 14);

        // Physicians list
        physicianListModel = new DefaultListModel<>();
        physicianListDisplay = new JList<>(physicianListModel);
        physicianListDisplay.setFont(baseFont);
        physicianListDisplay.setCellRenderer(new ListCardRenderer<>());
        physicianListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane physicianScroll = new JScrollPane(physicianListDisplay);
        physicianScroll.setBorder(BorderFactory.createTitledBorder("Physicians"));

        // Appointments list
        appointmentListModel = new DefaultListModel<>();
        appointmentListDisplay = new JList<>(appointmentListModel);
        appointmentListDisplay.setFont(baseFont);
        appointmentListDisplay.setCellRenderer(new ListCardRenderer<>());
        appointmentListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane appointmentScroll = new JScrollPane(appointmentListDisplay);
        appointmentScroll.setBorder(BorderFactory.createTitledBorder("Appointments"));

        // Sync appointments
        physicianListDisplay.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Physician selected = physicianListDisplay.getSelectedValue();
                if (selected != null) {
                    refreshAppointmentsFor(selected);
                }
            }
        });

        // Buttons
        JButton addPhysicianButton = createButton("➕ Add Physician");
        JButton addAppointmentButton = createButton("📅 Add Appointment");
        JButton viewAppointmentButton = createButton("🔍 View Appointment");

        addPhysicianButton.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("Physician ID:");
            String name = JOptionPane.showInputDialog("Physician Name:");
            String email = JOptionPane.showInputDialog("Physician Email:");
            if (id != null && name != null && email != null) {
                physicianManager.addPhysician(new Physician(id, name, email));
                refreshPhysicians();
            }
        });

        addAppointmentButton.addActionListener(e -> {
            Physician selected = physicianListDisplay.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select a physician first.");
                return;
            }
            new AddAppointmentDialog(frame, appointmentManager, selected.getId()).setVisible(true);
            refreshAppointmentsFor(selected);
        });

        viewAppointmentButton.addActionListener(e -> {
            Appointment selectedAppointment = appointmentListDisplay.getSelectedValue();
            Physician selectedPhysician = physicianListDisplay.getSelectedValue();
            if (selectedAppointment == null || selectedPhysician == null) {
                JOptionPane.showMessageDialog(frame, "Select an appointment to view.");
                return;
            }
            new ViewAppointmentDialog(frame, appointmentManager, selectedAppointment).setVisible(true);
            refreshAppointmentsFor(selectedPhysician);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(addPhysicianButton);
        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, physicianScroll, appointmentScroll);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshPhysicians();
        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshPhysicians() {
        physicianListModel.clear();
        List<Physician> all = physicianManager.getAllPhysicians();
        for (Physician p : all) {
            physicianListModel.addElement(p);
        }
        if (!physicianListModel.isEmpty()) {
            physicianListDisplay.setSelectedIndex(0);
        }
    }

    private void refreshAppointmentsFor(Physician physician) {
        appointmentListModel.clear();
        List<Appointment> appointments = appointmentManager.getAppointmentsForPhysician(physician.getId());
        for (Appointment a : appointments) {
            appointmentListModel.addElement(a);
        }
    }

    public static void launch(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        new PhysicianApp(physicianManager, appointmentManager);
    }

    // Simple card-style renderer
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
