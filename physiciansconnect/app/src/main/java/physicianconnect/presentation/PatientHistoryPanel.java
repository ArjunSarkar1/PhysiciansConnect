package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.ReferralManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Prescription;
import physicianconnect.objects.Referral;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * PatientHistoryPanel displays the history (appointments, prescriptions, referrals)
 * for a selected patient of the current physician.
 */
public class PatientHistoryPanel extends JPanel {
    private final AppointmentManager appointmentManager;
    private final PrescriptionPersistence prescriptionPersistence;
    private final ReferralManager referralManager;
    private final String physicianId;
    private JComboBox<String> patientCombo;
    private JTextArea historyArea;

    // Constructor for dependency injection (for testing)
    public PatientHistoryPanel(AppointmentManager appointmentManager,
                               PrescriptionPersistence prescriptionPersistence,
                               ReferralManager referralManager,
                               String physicianId) {
        this.appointmentManager      = appointmentManager;
        this.prescriptionPersistence = prescriptionPersistence;
        this.referralManager         = referralManager;
        this.physicianId             = physicianId;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);

        // Populate patient names into a sorted set
        Set<String> patientNames = appointmentManager.getAppointmentsForPhysician(physicianId)
                .stream()
                .map(Appointment::getPatientName)
                .collect(Collectors.toCollection(TreeSet::new));

        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));
        patientCombo.setName("patientCombo");
        patientCombo.setFont(UITheme.LABEL_FONT);
        patientCombo.setBackground(UITheme.BACKGROUND_COLOR);
        patientCombo.setForeground(UITheme.TEXT_COLOR);
        patientCombo.addActionListener(e -> updateHistory());

        historyArea = new JTextArea(15, 40);
        historyArea.setEditable(false);
        historyArea.setName("historyArea");
        historyArea.setFont(UITheme.TEXTFIELD_FONT);
        historyArea.setBackground(UITheme.BACKGROUND_COLOR);
        historyArea.setForeground(UITheme.TEXT_COLOR);

        add(patientCombo, BorderLayout.NORTH);
        add(new JScrollPane(historyArea), BorderLayout.CENTER);

        if (!patientNames.isEmpty()) {
            patientCombo.setSelectedIndex(0);
            updateHistory();
        }
    }

    // Original constructor for production use
    public PatientHistoryPanel(AppointmentManager appointmentManager,
                               PrescriptionPersistence prescriptionPersistence,
                               String physicianId) {
        this(appointmentManager,
                prescriptionPersistence,
                new ReferralManager(
                        physicianconnect.persistence.PersistenceFactory.getReferralPersistence()
                ),
                physicianId);
    }

    public void updateHistory() {
        String patient = (String) patientCombo.getSelectedItem();
        if (patient == null) {
            historyArea.setText("");
            return;
        }

        List<Appointment> appointments = appointmentManager.getAppointmentsForPhysician(physicianId)
                .stream()
                .filter(a -> a.getPatientName().equals(patient))
                .collect(Collectors.toList());

        List<Prescription> prescriptions = prescriptionPersistence.getPrescriptionsForPatient(patient);
        List<Referral> referrals = referralManager.getReferralsForPatient(patient);

        StringBuilder sb = new StringBuilder();
        sb.append(UIConfig.HISTORY_SECTION_APPOINTMENTS).append("\n");
        for (Appointment a : appointments) {
            sb.append("  ")
                    .append(a.getDateTime().format(UIConfig.HISTORY_DATE_FORMATTER))
                    .append("\n");
            if (a.getNotes() != null && !a.getNotes().trim().isEmpty()) {
                sb.append("    ")
                        .append(UIConfig.HISTORY_LABEL_NOTES)
                        .append(a.getNotes())
                        .append("\n");
            }
        }

        sb.append("\n").append(UIConfig.HISTORY_SECTION_PRESCRIPTIONS).append("\n");
        for (Prescription p : prescriptions) {
            sb.append("  ").append(p.toString()).append("\n");
        }

        sb.append("\n").append(UIConfig.HISTORY_SECTION_REFERRALS).append("\n");
        for (Referral r : referrals) {
            sb.append("  [")
                    .append(r.getDateCreated())  // no longer calling .format(...)
                    .append("] ")
                    .append(r.getReferralType())
                    .append(" - ")
                    .append(r.getDetails())
                    .append("\n");
        }

        historyArea.setText(sb.toString());
    }
}