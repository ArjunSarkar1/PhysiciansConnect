package physicianconnect.presentation;

import physicianconnect.logic.ReferralManager;
import physicianconnect.objects.Referral;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ReferralPanel extends JPanel {
    private final ReferralManager referralManager;
    private final String physicianId;
    private JComboBox<String> patientCombo;
    private JTextField typeField;
    private JTextArea detailsArea;
    private JButton createButton;
    private JTextArea referralListArea;

    public ReferralPanel(ReferralManager referralManager, String physicianId, List<String> patientNames) {
        this.referralManager = referralManager;
        this.physicianId = physicianId;
        setLayout(new BorderLayout(10, 10));

        // Top: Create Referral
        JPanel createPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));
        typeField = new JTextField(15);
        detailsArea = new JTextArea(3, 20);
        createButton = new JButton("Create Referral");

        gbc.gridx = 0; gbc.gridy = 0; createPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1; createPanel.add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; createPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; createPanel.add(typeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; createPanel.add(new JLabel("Details:"), gbc);
        gbc.gridx = 1; createPanel.add(new JScrollPane(detailsArea), gbc);

        gbc.gridx = 1; gbc.gridy = 3; createPanel.add(createButton, gbc);

        // Center: List of Referrals
        referralListArea = new JTextArea(10, 40);
        referralListArea.setEditable(false);

        add(createPanel, BorderLayout.NORTH);
        add(new JScrollPane(referralListArea), BorderLayout.CENTER);

        createButton.addActionListener(e -> createReferral());
        patientCombo.addActionListener(e -> updateReferralList());

        if (patientCombo.getItemCount() > 0) {
            patientCombo.setSelectedIndex(0);
            updateReferralList();
        }
    }

    private void createReferral() {
        String patient = (String) patientCombo.getSelectedItem();
        String type = typeField.getText().trim();
        String details = detailsArea.getText().trim();
        String date = LocalDate.now().toString();

        if (patient == null || type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Patient and type are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Referral referral = new Referral(0, physicianId, patient, type, details, date);
        referralManager.addReferral(referral);
        JOptionPane.showMessageDialog(this, "Referral created for " + patient + ".");
        typeField.setText("");
        detailsArea.setText("");
        updateReferralList();
    }

    private void updateReferralList() {
        String patient = (String) patientCombo.getSelectedItem();
        if (patient == null) {
            referralListArea.setText("");
            return;
        }
        List<Referral> referrals = referralManager.getReferralsForPhysician(physicianId)
                .stream().filter(r -> r.getPatientName().equals(patient)).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("Referrals for ").append(patient).append(":\n");
        for (Referral r : referrals) {
            sb.append("[").append(r.getDateCreated()).append("] ")
              .append(r.getReferralType()).append(": ")
              .append(r.getDetails()).append("\n");
        }
        referralListArea.setText(sb.toString());
    }
}