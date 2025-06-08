package physicianconnect;

import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.presentation.LoginScreen;
import physicianconnect.presentation.physician.PhysicianApp;
import physicianconnect.presentation.receptionist.ReceptionistApp;

import javax.swing.*;

public class AppController {
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final ReceptionistManager receptionistManager;

    public AppController(PhysicianManager physicianManager, AppointmentManager appointmentManager, ReceptionistManager receptionistManager) {
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        this.receptionistManager = receptionistManager;
    }

    public void showLoginScreen() {
        SwingUtilities.invokeLater(() -> new LoginScreen(
                physicianManager,
                appointmentManager,
                receptionistManager,
                this // Pass controller's own method as logout callback
        ));
    }

    public void showPhysicianApp(physicianconnect.objects.Physician user) {
        SwingUtilities.invokeLater(() -> PhysicianApp.launchSingleUser(
                user,
                physicianManager,
                appointmentManager,
                receptionistManager,
                this::showLoginScreen
        ));
    }

    public void showReceptionistApp(physicianconnect.objects.Receptionist receptionist) {
        SwingUtilities.invokeLater(() -> new ReceptionistApp(
                receptionist,
                physicianManager,
                appointmentManager,
                receptionistManager,
                this::showLoginScreen
        ));
    }
}