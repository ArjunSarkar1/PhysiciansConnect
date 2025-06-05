package physicianconnect;

import physicianconnect.config.AppConfig;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.persistence.PersistenceFactory;

public class App {
    public static void main(String[] args) {
        PersistenceFactory.initialize(AppConfig.getPersistenceType(), AppConfig.shouldSeedData());

        PhysicianManager physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        AppointmentManager appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
        ReceptionistManager receptionistManager = new ReceptionistManager(PersistenceFactory.getReceptionistPersistence());

        AppController controller = new AppController(physicianManager, appointmentManager, receptionistManager);
        controller.showLoginScreen();
        controller.showLoginScreen(); //second one to test messaging/ receptionist - physician appointment updates
    }
}