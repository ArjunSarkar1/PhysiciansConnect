package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Physician;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        setTitle("PhysicianConnect Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");

        JLabel testInfo = new JLabel("Test: test@email.com / password: test123");

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            Physician user = physicianManager.login(email, pass);
            if (user != null) {
                dispose(); // close login screen
                PhysicianApp.launchSingleUser(user, physicianManager, appointmentManager);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        createBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("ID:");
            String name = JOptionPane.showInputDialog("Name:");
            String email = JOptionPane.showInputDialog("Email:");
            String password = JOptionPane.showInputDialog("Password:");

            if (id != null && name != null && email != null && password != null) {
                physicianManager.addPhysician(new Physician(id, name, email, password));
                JOptionPane.showMessageDialog(this, "Account created!");
            }
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
}
