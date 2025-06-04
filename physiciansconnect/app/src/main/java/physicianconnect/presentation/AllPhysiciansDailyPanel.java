package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.objects.Physician;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AllPhysiciansDailyPanel extends JPanel {
    private final JPanel panelsContainer;
    private final JTextField searchField;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final AvailabilityService availabilityService;
    private final LocalDate date;
    private List<Physician> allPhysicians;

    public AllPhysiciansDailyPanel(
            PhysicianManager physicianManager,
            AppointmentManager appointmentManager,
            AvailabilityService availabilityService,
            LocalDate date
    ) {
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        this.availabilityService = availabilityService;
        this.date = date;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel("Search Physician: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Type physician name...");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // Container for DailyAvailabilityPanels
        panelsContainer = new JPanel();
        panelsContainer.setLayout(new BoxLayout(panelsContainer, BoxLayout.X_AXIS));
        panelsContainer.setBackground(new Color(245, 247, 250));
        JScrollPane scrollPane = new JScrollPane(panelsContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Load all physicians and display panels
        allPhysicians = physicianManager.getAllPhysicians();
        updatePhysicianPanels(allPhysicians);

        // Search filter
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim().toLowerCase();
                List<Physician> filtered = allPhysicians.stream()
                        .filter(p -> p.getName().toLowerCase().contains(text))
                        .collect(Collectors.toList());
                updatePhysicianPanels(filtered);
            }
        });
    }

    private void updatePhysicianPanels(List<Physician> physicians) {
        panelsContainer.removeAll();
        for (Physician p : physicians) {
            JPanel panelWithLabel = new JPanel(new BorderLayout());
            panelWithLabel.setBackground(new Color(245, 247, 250));
            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setOpaque(true);
            nameLabel.setBackground(new Color(220, 230, 250));
            panelWithLabel.add(nameLabel, BorderLayout.NORTH);

            DailyAvailabilityPanel dailyPanel = new DailyAvailabilityPanel(
                    p.getId(),
                    availabilityService,
                    appointmentManager,
                    date
            );
            panelWithLabel.add(dailyPanel, BorderLayout.CENTER);
            panelsContainer.add(panelWithLabel);
        }
        panelsContainer.revalidate();
        panelsContainer.repaint();
    }
}