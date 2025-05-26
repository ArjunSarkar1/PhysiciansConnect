package physicianconnect;

import javax.swing.SwingUtilities;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;
import physicianconnect.presentation.PhysicianApp;

public class App {
    public static void main(String[] args) {
        // Step 1: Initialize shared DB connection
        PersistenceFactory.initialize(PersistenceType.PROD, false);

        // Step 2: Create logic layer
        PhysicianManager physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        AppointmentManager appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());

        // Step 3: Launch the UI (pass managers to the UI)
        SwingUtilities.invokeLater(() -> PhysicianApp.launch(physicianManager, appointmentManager));
    }
}
