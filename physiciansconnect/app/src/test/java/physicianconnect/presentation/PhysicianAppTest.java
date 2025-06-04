package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.stub.AppointmentPersistenceStub;
import physicianconnect.persistence.stub.PhysicianPersistenceStub;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class PhysicianAppTest {

    private PhysicianApp app;
    private Physician testPhysician;
    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;

    @BeforeEach
    public void setup() {
        PhysicianPersistenceStub physicianStub = new PhysicianPersistenceStub(true);
        AppointmentPersistenceStub appointmentStub = new AppointmentPersistenceStub(true);
        physicianManager = new PhysicianManager(physicianStub);
        appointmentManager = new AppointmentManager(appointmentStub);

        testPhysician = physicianManager.getAllPhysicians().get(0);
        app = new PhysicianApp(testPhysician, physicianManager, appointmentManager);
    }

    @Test
    public void testAppointmentsAreLoaded() throws Exception {
        JFrame frame = (JFrame) getField(app, "frame");
        DefaultListModel<?> model = (DefaultListModel<?>) getField(app, "appointmentListModel");
        assertTrue(model.size() > 0, "Appointment list should be populated");
        assertTrue(frame.isVisible(), "Frame should be visible");
    }

    // --- Helpers ---

    private Object getField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }
}