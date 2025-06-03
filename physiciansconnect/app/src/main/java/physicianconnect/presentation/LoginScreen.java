package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Physician;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class LoginScreen extends JFrame {
    public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        setTitle("PhysicianConnect Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        emailField.setName("emailField");

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);
        passField.setName("passwordField");

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");
        loginBtn.setName("loginBtn");
        createBtn.setName("createBtn");

        JLabel testInfo = new JLabel("Test login: test@email.com / test123");
        testInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        testInfo.setForeground(new Color(0, 0, 0));

        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(loginBtn);

        createBtn.setBackground(new Color(76, 175, 80));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createBtn.setOpaque(true);
        createBtn.setBorderPainted(false);
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(createBtn);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            Physician user = physicianManager.login(email, pass);
            if (user != null) {
                dispose(); // close login screen
                PhysicianApp.launchSingleUser(user, physicianManager, appointmentManager);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        createBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Create Account", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField nameField = new JTextField(20);
            JTextField regEmailField = new JTextField(20);
            JPasswordField passwordField = new JPasswordField(20);
            JPasswordField confirmPasswordField = new JPasswordField(20);

            gbc.gridx = 0;
            gbc.gridy = 0;
            dialog.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            dialog.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            dialog.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            dialog.add(regEmailField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            dialog.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            dialog.add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            dialog.add(new JLabel("Confirm Password:"), gbc);
            gbc.gridx = 1;
            dialog.add(confirmPasswordField, gbc);

            JButton registerBtn = new JButton("Register");
            registerBtn.setBackground(new Color(76, 175, 80));
            registerBtn.setForeground(Color.WHITE);
            registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            registerBtn.setOpaque(true);
            registerBtn.setBorderPainted(false);
            registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addHoverEffect(registerBtn);

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

                // Validation
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if email already exists
                if (physicianManager.getPhysicianByEmail(email) != null) {
                    JOptionPane.showMessageDialog(dialog, "An account with this email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String id = java.util.UUID.randomUUID().toString();
                Physician newPhysician = new Physician(id, name, email, password);
                physicianManager.addPhysician(newPhysician);
                
                JOptionPane.showMessageDialog(dialog, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Log in the newly created user and launch the main application
                dispose(); // close login screen
                try {
                    SwingUtilities.invokeLater(() -> {
                        PhysicianApp.launchSingleUser(newPhysician, physicianManager, appointmentManager);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error launching application: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        JPanel panel = new JPanel(new GridLayout(5, 1));
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

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.brighter());
            }
        });
    }
}
