package physicianconnect.presentation;

import physicianconnect.AppController;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.objects.Physician;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.objects.Receptionist;

import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
        private final AppController controller;

        public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager,
                        ReceptionistManager receptionistManager, AppController controller) {
                this.controller = controller;

                setTitle(UIConfig.LOGIN_DIALOG_TITLE);
                setSize(400, 250);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(EXIT_ON_CLOSE);

                // Main panel with GridBagLayout
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;

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

                // ─────────── Test Info Label ───────────
                JLabel testInfo = new JLabel(
                                UIConfig.LOADING_MESSAGE.replace("Loading...", "Test login: test@email.com / test123"));
                testInfo.setFont(UITheme.LABEL_FONT);
                testInfo.setForeground(UITheme.TEXT_COLOR);

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
                createBtn.setBackground(UITheme.SUCCESS_BUTTON_COLOR);
                createBtn.setForeground(UITheme.BACKGROUND_COLOR);
                createBtn.setOpaque(true);
                createBtn.setBorderPainted(false);
                createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                UITheme.applyHoverEffect(createBtn);

                // Add components to panel with GridBagLayout
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                panel.add(testInfo, gbc);

                // Email row
                gbc.gridy++;
                gbc.gridwidth = 1;
                gbc.anchor = GridBagConstraints.EAST;
                gbc.gridx = 0;
                panel.add(emailLabel, gbc);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.gridx = 1;
                panel.add(emailField, gbc);

                // Password row
                gbc.gridy++;
                gbc.anchor = GridBagConstraints.EAST;
                gbc.gridx = 0;
                panel.add(passLabel, gbc);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.gridx = 1;
                panel.add(passField, gbc);

                // Buttons row
                JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
                buttons.add(loginBtn);
                buttons.add(createBtn);

                gbc.gridx = 0;
                gbc.gridy++;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                UIConfig.LOADING_MESSAGE.replace("Loading...", "Test login: test@email.com / test123");
                testInfo.setFont(UITheme.LABEL_FONT);
                testInfo.setForeground(UITheme.TEXT_COLOR);

                // ─────────── Action Listeners ───────────
                loginBtn.addActionListener(e -> {
                        String email = emailField.getText().trim();
                        String pass = new String(passField.getPassword());

                        PhysicianController physicianController = new PhysicianController(physicianManager);
                        ReceptionistController receptionistController = new ReceptionistController(receptionistManager);

                        try {
                                Physician user = physicianController.login(email, pass);
                                dispose();
                                controller.showPhysicianApp(user);
                                return;
                        } catch (InvalidCredentialException ex) {
                                // Try receptionist login if physician fails
                                try {
                                        Receptionist receptionist = receptionistController.login(email, pass);
                                        dispose();
                                        controller.showReceptionistApp(receptionist);
                                        return;
                                } catch (InvalidCredentialException rex) {
                                        JOptionPane.showMessageDialog(
                                                        this,
                                                        rex.getMessage(),
                                                        UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                }
                        }
                });

                // Registration logic (show combo box)
                createBtn.addActionListener(e -> {
                        String[] userTypes = { "Physician", "Receptionist" };
                        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);

                        JDialog dialog = new JDialog(this, "Create Account", true);
                        dialog.setLayout(new GridBagLayout());
                        GridBagConstraints regGbc = new GridBagConstraints();
                        regGbc.insets = new Insets(5, 5, 5, 5);
                        regGbc.fill = GridBagConstraints.HORIZONTAL;

                        JTextField nameField = new JTextField(20);
                        JTextField regEmailField = new JTextField(20);
                        JPasswordField passwordField = new JPasswordField(20);
                        JPasswordField confirmPasswordField = new JPasswordField(20);

                        regGbc.gridx = 0;
                        regGbc.gridy = 0;
                        dialog.add(new JLabel("Account Type:"), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(userTypeCombo, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 1;
                        dialog.add(new JLabel("Name:"), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(nameField, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 2;
                        dialog.add(new JLabel("Email:"), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(regEmailField, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 3;
                        dialog.add(new JLabel("Password:"), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(passwordField, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 4;
                        dialog.add(new JLabel("Confirm Password:"), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(confirmPasswordField, regGbc);

                        JButton registerBtn = new JButton("Register");
                        registerBtn.setBackground(new Color(76, 175, 80));
                        registerBtn.setForeground(Color.WHITE);
                        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        registerBtn.setOpaque(true);
                        registerBtn.setBorderPainted(false);
                        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        UITheme.applyHoverEffect(registerBtn);

                        regGbc.gridx = 0;
                        regGbc.gridy = 5;
                        regGbc.gridwidth = 2;
                        regGbc.anchor = GridBagConstraints.CENTER;
                        dialog.add(registerBtn, regGbc);

                        registerBtn.addActionListener(ev -> {
                                String userType = (String) userTypeCombo.getSelectedItem();
                                String name = nameField.getText().trim();
                                String email = regEmailField.getText().trim();
                                String password = new String(passwordField.getPassword());
                                String confirmPassword = new String(confirmPasswordField.getPassword());

                                // Validation
                                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                        JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                                        JOptionPane.showMessageDialog(dialog, "Please enter a valid email address.",
                                                        "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (password.length() < 6) {
                                        JOptionPane.showMessageDialog(dialog,
                                                        "Password must be at least 6 characters long.", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (!password.equals(confirmPassword)) {
                                        JOptionPane.showMessageDialog(dialog, "Passwords do not match.", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (physicianManager.getPhysicianByEmail(email) != null ||
                                                receptionistManager.getReceptionistByEmail(email) != null) {
                                        JOptionPane.showMessageDialog(dialog,
                                                        "An account with this email already exists.", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                // String id = java.util.UUID.randomUUID().toString();

                                PhysicianController physicianController = new PhysicianController(physicianManager);
                                ReceptionistController receptionistController = new ReceptionistController(
                                                receptionistManager);

                                try {
                                        if ("Physician".equals(userType)) {
                                                Physician newPhysician = physicianController.register(name, email,
                                                                password, confirmPassword);
                                                JOptionPane.showMessageDialog(dialog, "Account created successfully!",
                                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                                                dialog.dispose();
                                                SwingUtilities.invokeLater(
                                                                () -> controller.showPhysicianApp(newPhysician));
                                        } else if ("Receptionist".equals(userType)) {
                                                Receptionist newReceptionist = receptionistController.register(name,
                                                                email, password, confirmPassword);
                                                JOptionPane.showMessageDialog(dialog, "Account created successfully!",
                                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                                                dialog.dispose();
                                                SwingUtilities.invokeLater(
                                                                () -> controller.showReceptionistApp(newReceptionist));
                                        }
                                } catch (InvalidCredentialException ex) {
                                        JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                }
                        });

                        dialog.pack();
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                });

                add(panel, BorderLayout.CENTER);
                add(buttons, BorderLayout.SOUTH);
                setVisible(true);
        }
}