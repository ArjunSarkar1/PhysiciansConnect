package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.stub.AppointmentPersistenceStub;
import physicianconnect.persistence.stub.PhysicianPersistenceStub;

import javax.swing.*;
import java.awt.*;
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

    @Test
    public void testSignOutButtonClosesFrame() throws Exception {
        JFrame frame = (JFrame) getField(app, "frame");
        JPanel contentPanel = (JPanel) frame.getContentPane();
        JButton signOutButton = findButton(contentPanel, "🚪 Sign Out");
        assertNotNull(signOutButton, "Sign Out button should exist");
        // Simulate click
        SwingUtilities.invokeAndWait(signOutButton::doClick);
        assertFalse(frame.isDisplayable(), "Frame should be disposed after sign out");
    }

    // --- Helpers ---

    private Object getField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    private JButton findButton(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(text)) {
                return (JButton) comp;
            } else if (comp instanceof Container) {
                JButton found = findButton((Container) comp, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}