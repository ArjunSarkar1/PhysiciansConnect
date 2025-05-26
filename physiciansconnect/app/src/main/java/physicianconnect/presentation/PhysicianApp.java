package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;

import javax.swing.*;
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
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Physicians list
        physicianListModel = new DefaultListModel<>();
        physicianListDisplay = new JList<>(physicianListModel);
        JScrollPane physicianScroll = new JScrollPane(physicianListDisplay);
        physicianScroll.setBorder(BorderFactory.createTitledBorder("Physicians"));

        // Appointments list
        appointmentListModel = new DefaultListModel<>();
        appointmentListDisplay = new JList<>(appointmentListModel);
        JScrollPane appointmentScroll = new JScrollPane(appointmentListDisplay);
        appointmentScroll.setBorder(BorderFactory.createTitledBorder("Appointments"));

        // Sync appointments to selected physician
        physicianListDisplay.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Physician selected = physicianListDisplay.getSelectedValue();
                if (selected != null) {
                    refreshAppointmentsFor(selected);
                }
            }
        });

        // Add physician button
        JButton addPhysicianButton = new JButton("Add Physician");
        addPhysicianButton.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("Physician ID:");
            String name = JOptionPane.showInputDialog("Physician Name:");
            String email = JOptionPane.showInputDialog("Physician Email:");
            if (id != null && name != null && email != null) {
                physicianManager.addPhysician(new Physician(id, name, email));
                refreshPhysicians();
            }
        });

        // Add appointment via custom dialog
        JButton addAppointmentButton = new JButton("Add Appointment");
        addAppointmentButton.addActionListener(e -> {
            Physician selected = physicianListDisplay.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select a physician first.");
                return;
            }
            new AddAppointmentDialog(frame, appointmentManager, selected.getId()).setVisible(true);
            refreshAppointmentsFor(selected);
        });

        // View selected appointment in dialog
        JButton viewAppointmentButton = new JButton("View Appointment");
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

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addPhysicianButton);
        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, physicianScroll, appointmentScroll);
        splitPane.setResizeWeight(0.5);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        refreshPhysicians();
        frame.setVisible(true);
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

    // This static launcher is called from App.java
    public static void launch(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        new PhysicianApp(physicianManager, appointmentManager);
    }
}
