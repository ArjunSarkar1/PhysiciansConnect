package physicianconnect;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.presentation.LoginScreen;
import physicianconnect.presentation.PhysicianApp;
import physicianconnect.presentation.ReceptionistApp;

import javax.swing.*;

public class AppController {
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final ReceptionistManager receptionistManager;
    private final AppointmentController appointmentController;

    public AppController(PhysicianManager physicianManager, AppointmentManager appointmentManager, ReceptionistManager receptionistManager) {
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        this.receptionistManager = receptionistManager;
        this.appointmentController = new AppointmentController(appointmentManager);
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
                appointmentController,
                this::showLoginScreen
        ));
    }

    public void showReceptionistApp(physicianconnect.objects.Receptionist receptionist) {
        SwingUtilities.invokeLater(() -> new ReceptionistApp(
                receptionist,
                physicianManager,
                appointmentManager,
                receptionistManager,
                appointmentController,
                this::showLoginScreen
        ));
    }
}