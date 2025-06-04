package physicianconnect.presentation;

import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Physician;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        setTitle(UIConfig.LOGIN_DIALOG_TITLE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ─────────── Labels & Fields ───────────
        JLabel emailLabel = new JLabel(UIConfig.USER_EMAIL_LABEL);
        emailLabel.setFont(UITheme.LABEL_FONT);
        emailLabel.setForeground(UITheme.TEXT_COLOR);

        JTextField emailField = new JTextField(20);
        emailField.setName("emailField");

        JLabel passLabel = new JLabel(UIConfig.USER_PASSWORD_LABEL);
        passLabel.setFont(UITheme.LABEL_FONT);
        passLabel.setForeground(UITheme.TEXT_COLOR);

        JPasswordField passField = new JPasswordField(20);
        passField.setName("passwordField");

        // ─────────── Buttons ───────────
        JButton loginBtn = new JButton(UIConfig.LOGIN_BUTTON_TEXT);
        loginBtn.setName("loginBtn");
        loginBtn.setFont(UITheme.BUTTON_FONT);
        loginBtn.setBackground(UITheme.PRIMARY_COLOR);
        loginBtn.setForeground(UITheme.BACKGROUND_COLOR);
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        UITheme.applyHoverEffect(loginBtn);

        JButton createBtn = new JButton(UIConfig.CREAT_ACCOUNT_BUTTON_TEXT);
        createBtn.setName("createBtn");
        createBtn.setFont(UITheme.BUTTON_FONT);
        createBtn.setBackground(UITheme.SUCCESS_COLOR);
        createBtn.setForeground(UITheme.BACKGROUND_COLOR);
        createBtn.setOpaque(true);
        createBtn.setBorderPainted(false);
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        UITheme.applyHoverEffect(createBtn);

        // ─────────── Test Info Label ───────────
        JLabel testInfo = new JLabel(
                UIConfig.LOADING_MESSAGE.replace("Loading...", "Test login: test@email.com / test123")
        );
        testInfo.setFont(UITheme.LABEL_FONT);
        testInfo.setForeground(UITheme.TEXT_COLOR);

        // ─────────── Action Listeners ───────────
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            PhysicianController controller = new PhysicianController(physicianManager);
            try {
                Physician user = controller.login(email, pass);
                dispose(); // close login screen
                PhysicianApp.launchSingleUser(user, physicianManager, appointmentManager);
            } catch (InvalidCredentialException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        createBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel(
                    UIConfig.PATIENT_NAME_LABEL.replace("Patient Name:", "Name:")
            );
            nameLabel.setFont(UITheme.LABEL_FONT);
            nameLabel.setForeground(UITheme.TEXT_COLOR);

            JTextField nameField = new JTextField(20);

            JLabel regEmailLabel = new JLabel(UIConfig.USER_EMAIL_LABEL);
            regEmailLabel.setFont(UITheme.LABEL_FONT);
            regEmailLabel.setForeground(UITheme.TEXT_COLOR);

            JTextField regEmailField = new JTextField(20);

            JLabel passwordLabel = new JLabel(
                    UIConfig.USER_PASSWORD_LABEL.replace("Password:", "Password:")
            );
            passwordLabel.setFont(UITheme.LABEL_FONT);
            passwordLabel.setForeground(UITheme.TEXT_COLOR);

            JPasswordField passwordField = new JPasswordField(20);

            JLabel confirmPasswordLabel = new JLabel(
                    UIConfig.USER_PASSWORD_LABEL.replace("Password:", "Confirm Password:")
            );
            confirmPasswordLabel.setFont(UITheme.LABEL_FONT);
            confirmPasswordLabel.setForeground(UITheme.TEXT_COLOR);

            JPasswordField confirmPasswordField = new JPasswordField(20);

            // Row 0: Name
            gbc.gridx = 0;
            gbc.gridy = 0;
            dialog.add(nameLabel, gbc);
            gbc.gridx = 1;
            dialog.add(nameField, gbc);

            // Row 1: Email
            gbc.gridx = 0;
            gbc.gridy = 1;
            dialog.add(regEmailLabel, gbc);
            gbc.gridx = 1;
            dialog.add(regEmailField, gbc);

            // Row 2: Password
            gbc.gridx = 0;
            gbc.gridy = 2;
            dialog.add(passwordLabel, gbc);
            gbc.gridx = 1;
            dialog.add(passwordField, gbc);

            // Row 3: Confirm Password
            gbc.gridx = 0;
            gbc.gridy = 3;
            dialog.add(confirmPasswordLabel, gbc);
            gbc.gridx = 1;
            dialog.add(confirmPasswordField, gbc);

            // Register Button
            JButton registerBtn = new JButton(UIConfig.REGISTER_BUTTON_TEXT);
            registerBtn.setFont(UITheme.BUTTON_FONT);
            registerBtn.setBackground(UITheme.SUCCESS_COLOR);
            registerBtn.setForeground(UITheme.BACKGROUND_COLOR);
            registerBtn.setOpaque(true);
            registerBtn.setBorderPainted(false);
            registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            UITheme.applyHoverEffect(registerBtn);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            dialog.add(registerBtn, gbc);

            registerBtn.addActionListener(ev -> {
                String name = nameField.getText().trim();
                String email = regEmailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                PhysicianController controller = new PhysicianController(physicianManager);
                try {
                    Physician newPhysician = controller.register(name, email, password, confirmPassword);
                    JOptionPane.showMessageDialog(
                            dialog,
                            UIConfig.SUCCESS_ACCOUNT_CREATED,
                            UIConfig.SUCCESS_DIALOG_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dialog.dispose();
                    dispose(); // close login screen
                    SwingUtilities.invokeLater(() ->
                            PhysicianApp.launchSingleUser(newPhysician, physicianManager, appointmentManager)
                    );
                } catch (InvalidCredentialException ex) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            ex.getMessage(),
                            UIConfig.ERROR_DIALOG_TITLE,
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        // ─────────── Layout ───────────
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(testInfo);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passLabel);
        panel.add(passField);

        JPanel buttons = new JPanel();
        buttons.add(loginBtn);
        buttons.add(createBtn);

        add(panel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setVisible(true);
    }
}