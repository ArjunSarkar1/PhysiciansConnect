package physicianconnect.presentation;

import physicianconnect.AppController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Physician;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;

public class ProfilePanel extends JPanel {
    private final AppController appController;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField specialtyField;
    private final JTextField officeHoursField;
    private final JCheckBox notifyAppointments;
    private final JCheckBox notifyBilling;
    private final JCheckBox notifyMessages;
    private final JTextField phoneField;
    private final JTextField addressField;
    private final JButton cancelButton;
    private final JButton editButton;
    private final JButton saveButton;
    private final JButton signOutButton;
    private final JLabel photoLabel;
    private final JButton changePhotoButton;
    private final int MAX_PHOTO_SIZE = 200;
    private final Runnable onProfileUpdated;

    private static final String PHOTO_DIR = new File("src/main/resources/profile_photos")
            .getAbsolutePath();

    public ProfilePanel(Physician physician, PhysicianManager physicianManager, AppointmentManager appointmentManager,
            AppController appController, Runnable onProfileUpdated) {

        this.appController = appController;
        this.onProfileUpdated = onProfileUpdated;

        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Profile photo section
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        loadProfilePhoto(physician.getId());

        changePhotoButton = new JButton("Change Photo");
        changePhotoButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            File photoDir = new File(PHOTO_DIR);
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }
            chooser.setCurrentDirectory(new File(PHOTO_DIR));
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (InputStream in = new FileInputStream(file)) {
                    physicianManager.uploadProfilePhoto(physician.getId(), in);
                    loadProfilePhoto(physician.getId());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to upload photo: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel photoPanel = new JPanel(new BorderLayout(10, 10));
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(changePhotoButton, BorderLayout.SOUTH);

        // Form fields
        nameField = new JTextField(physician.getName());
        emailField = new JTextField(physician.getEmail());
        emailField.setEditable(false);

        specialtyField = new JTextField(physician.getSpecialty());
        officeHoursField = new JTextField(physician.getOfficeHours());
        phoneField = new JTextField(physician.getPhone());
        addressField = new JTextField(physician.getOfficeAddress());

        notifyAppointments = new JCheckBox("Appointments", physician.isNotifyAppointment());
        notifyBilling = new JCheckBox("Billing", physician.isNotifyBilling());
        notifyMessages = new JCheckBox("Messages", physician.isNotifyMessages());

        // Add labels and fields
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email (Contact Info):"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Specialty:"));
        formPanel.add(specialtyField);
        formPanel.add(new JLabel("Office Hours:"));
        formPanel.add(officeHoursField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Office Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Notification Preferences:"));

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.add(notifyAppointments);
        checkboxPanel.add(notifyBilling);
        checkboxPanel.add(notifyMessages);
        formPanel.add(checkboxPanel);

        JPanel paddedFormPanel = new JPanel(new BorderLayout());
        paddedFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedFormPanel.add(formPanel, BorderLayout.CENTER);
        add(paddedFormPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        editButton = new JButton("Edit");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel Changes");
        signOutButton = new JButton("Sign Out");

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(signOutButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel paddedPhotoPanel = new JPanel(new BorderLayout());
        paddedPhotoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        paddedPhotoPanel.add(photoPanel, BorderLayout.NORTH);
        paddedFormPanel.add(paddedPhotoPanel, BorderLayout.WEST);

        // Start in read-only mode
        setEditable(false);

        // Edit button toggles editability
        editButton.addActionListener(e -> setEditable(true));

        // Save action
        saveButton.addActionListener(e -> {
            try {
                physician.setName(nameField.getText());
                physician.setSpecialty(specialtyField.getText());
                physician.setOfficeHours(officeHoursField.getText());
                physician.setPhone(phoneField.getText());
                physician.setOfficeAddress(addressField.getText());

                physician.setNotifyAppointment(notifyAppointments.isSelected());
                physician.setNotifyBilling(notifyBilling.isSelected());
                physician.setNotifyMessages(notifyMessages.isSelected());

                // Validate before saving
                physicianManager.validateBasicInfo(physician);
                physicianManager.updatePhysician(physician);

                // Notify parent that profile was updated
                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }

                JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                setEditable(false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            nameField.setText(physician.getName());
            specialtyField.setText(physician.getSpecialty());
            officeHoursField.setText(physician.getOfficeHours());
            phoneField.setText(physician.getPhone());
            addressField.setText(physician.getOfficeAddress());
            notifyAppointments.setSelected(physician.isNotifyAppointment());
            notifyBilling.setSelected(physician.isNotifyBilling());
            notifyMessages.setSelected(physician.isNotifyMessages());

            setEditable(false);
        });

        // Sign out action
        signOutButton.addActionListener(e -> {
            Window topWindow = SwingUtilities.getWindowAncestor(this);
            if (topWindow != null) {
                topWindow.dispose(); // closes the Profile dialog
            }

            // Dispose ALL windows (in case main app is still open)
            for (Window w : Window.getWindows()) {
                w.dispose();
            }

            // Launch login again
            if (appController != null) {
                appController.showLoginScreen();
            }
        });

        saveButton.setVisible(false);
        cancelButton.setVisible(false);

    }

    private void setEditable(boolean editable) {
        nameField.setEditable(editable);
        specialtyField.setEditable(editable);
        officeHoursField.setEditable(editable);
        phoneField.setEditable(editable);
        addressField.setEditable(editable);
        notifyAppointments.setEnabled(editable);
        notifyBilling.setEnabled(editable);
        notifyMessages.setEnabled(editable);

        // Show Buttons only in edit mode
        saveButton.setVisible(editable);
        cancelButton.setVisible(editable);
        changePhotoButton.setEnabled(editable);

        // Show sign out button only when not editing
        signOutButton.setVisible(!editable);
        editButton.setVisible(!editable);
    }

    private void loadProfilePhoto(String physicianId) {
        File photoFile = new File(PHOTO_DIR, physicianId + ".png");
        if (photoFile.exists()) {
            ImageIcon icon = new ImageIcon(photoFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(img));
        } else {
            BufferedImage placeholder = new BufferedImage(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, MAX_PHOTO_SIZE, MAX_PHOTO_SIZE);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("No Photo", 50, 100);
            g2.dispose();
            photoLabel.setIcon(new ImageIcon(placeholder));
        }
    }

}
