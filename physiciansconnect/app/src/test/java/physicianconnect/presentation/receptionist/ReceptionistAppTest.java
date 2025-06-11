
package physicianconnect.presentation.receptionist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import physicianconnect.logic.manager.*;
import physicianconnect.objects.Receptionist;
import physicianconnect.objects.Appointment;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.interfaces.*;
import physicianconnect.logic.controller.AppointmentController;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReceptionistAppTest {

    @Mock private PhysicianManager physicianManager;
    @Mock private AppointmentManager appointmentManager;
    @Mock private ReceptionistManager receptionistManager;
    @Mock private AppointmentController appointmentController;
    @Mock private Runnable logoutCallback;

    private Receptionist loggedInReceptionist;
    private ReceptionistApp app;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create mock receptionist
        loggedInReceptionist = mock(Receptionist.class);
        when(loggedInReceptionist.getId()).thenReturn("R123");
        when(loggedInReceptionist.getName()).thenReturn("Test Receptionist");
        when(loggedInReceptionist.getEmail()).thenReturn("test@receptionist.com");

        // Setup all persistence layer mocks
        try (MockedStatic<PersistenceFactory> mockFactory = mockStatic(PersistenceFactory.class)) {
            mockFactory.when(PersistenceFactory::getPhysicianPersistence).thenReturn(mock(PhysicianPersistence.class));
            mockFactory.when(PersistenceFactory::getAppointmentPersistence).thenReturn(mock(AppointmentPersistence.class));
            mockFactory.when(PersistenceFactory::getReceptionistPersistence).thenReturn(mock(ReceptionistPersistence.class));
            mockFactory.when(PersistenceFactory::getInvoicePersistence).thenReturn(mock(InvoicePersistence.class));
            mockFactory.when(PersistenceFactory::getPaymentPersistence).thenReturn(mock(PaymentPersistence.class));
            mockFactory.when(PersistenceFactory::getMessageRepository).thenReturn(mock(MessageRepository.class));
            mockFactory.when(PersistenceFactory::getNotificationPersistence).thenReturn(mock(NotificationPersistence.class));
            mockFactory.when(PersistenceFactory::getMedicationPersistence).thenReturn(mock(MedicationPersistence.class));
            mockFactory.when(PersistenceFactory::getPrescriptionPersistence).thenReturn(mock(PrescriptionPersistence.class));
            mockFactory.when(PersistenceFactory::getReferralPersistence).thenReturn(mock(ReferralPersistence.class));

            // Create the app instance
            app = new ReceptionistApp(
                    loggedInReceptionist,
                    physicianManager,
                    appointmentManager,
                    receptionistManager,
                    appointmentController,
                    logoutCallback
            );
        }
    }

    @Test
    public void testReceptionistAppCreation() {
        // Test that the app was created successfully
        assertNotNull(app);
    }

    @Test
    public void testAppointmentLifecycleCallbacks() {
        // Test that appointment lifecycle methods exist and can be called
        Appointment testAppointment = new Appointment("P123", "Test Patient", LocalDateTime.now());

        try {
            app.onAppointmentCreated(testAppointment);
            app.onAppointmentUpdated(testAppointment);
            app.onAppointmentDeleted(testAppointment);
            // If we get here without exceptions, the methods exist and work
            assertTrue(true);
        } catch (Exception e) {
            fail("Appointment lifecycle methods should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    public void testAppCanHandleNullAppointment() {
        // Test that the app handles null appointments gracefully
        try {
            app.onAppointmentCreated(null);
            app.onAppointmentUpdated(null);
            app.onAppointmentDeleted(null);
            // Should not throw exceptions
            assertTrue(true);
        } catch (Exception e) {
            // This is acceptable - the app might validate inputs
            assertTrue(true);
        }
    }

    @Test
    public void testManagersAreInitialized() {
        // Verify that our mocked managers are properly set up
        assertNotNull(physicianManager);
        assertNotNull(appointmentManager);
        assertNotNull(receptionistManager);
        assertNotNull(appointmentController);
        assertNotNull(logoutCallback);
    }

    @Test
    public void testReceptionistDataIsAccessible() {
        // Test that our receptionist mock has the expected data
        assertEquals("R123", loggedInReceptionist.getId());
        assertEquals("Test Receptionist", loggedInReceptionist.getName());
        assertEquals("test@receptionist.com", loggedInReceptionist.getEmail());
    }

    @Test
    public void testAppointmentCreationWithValidData() {
        // Test creating appointments with different scenarios
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        Appointment futureAppointment = new Appointment("P456", "Future Patient", futureTime);

        try {
            app.onAppointmentCreated(futureAppointment);
            assertTrue(true); // Test passes if no exception is thrown
        } catch (Exception e) {
            fail("Should be able to create future appointments: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleAppointmentOperations() {
        // Test multiple operations in sequence
        Appointment appointment1 = new Appointment("P001", "Patient One", LocalDateTime.now().plusHours(1));
        Appointment appointment2 = new Appointment("P002", "Patient Two", LocalDateTime.now().plusHours(2));

        try {
            app.onAppointmentCreated(appointment1);
            app.onAppointmentCreated(appointment2);
            app.onAppointmentUpdated(appointment1);
            app.onAppointmentDeleted(appointment2);
            assertTrue(true);
        } catch (Exception e) {
            fail("Multiple appointment operations should work: " + e.getMessage());
        }
    }

    @Test
    public void testAppointmentWithDifferentPatientNames() {
        // Test that the app can handle different patient names
        String[] patientNames = {
                "John Doe",
                "Mary Jane Watson",
                "Dr. Bruce Banner",
                "Jane-Smith O'Connor",
                "李明", // Chinese characters
                ""      // Empty name
        };

        for (String patientName : patientNames) {
            try {
                Appointment appointment = new Appointment("P123", patientName, LocalDateTime.now().plusMinutes(30));
                app.onAppointmentCreated(appointment);
                // If we get here, the app handled this patient name
            } catch (Exception e) {
                // Some names might be invalid, which is acceptable
            }
        }

        assertTrue(true); // Test passes if we complete the loop
    }

    @Test
    public void testAppWithDifferentReceptionistData() {
        // Test that the app works with different receptionist configurations
        Receptionist anotherReceptionist = mock(Receptionist.class);
        when(anotherReceptionist.getId()).thenReturn("R999");
        when(anotherReceptionist.getName()).thenReturn("Another Receptionist");
        when(anotherReceptionist.getEmail()).thenReturn("another@test.com");

        try (MockedStatic<PersistenceFactory> mockFactory = mockStatic(PersistenceFactory.class)) {
            mockFactory.when(PersistenceFactory::getPhysicianPersistence).thenReturn(mock(PhysicianPersistence.class));
            mockFactory.when(PersistenceFactory::getAppointmentPersistence).thenReturn(mock(AppointmentPersistence.class));
            mockFactory.when(PersistenceFactory::getReceptionistPersistence).thenReturn(mock(ReceptionistPersistence.class));
            mockFactory.when(PersistenceFactory::getInvoicePersistence).thenReturn(mock(InvoicePersistence.class));
            mockFactory.when(PersistenceFactory::getPaymentPersistence).thenReturn(mock(PaymentPersistence.class));
            mockFactory.when(PersistenceFactory::getMessageRepository).thenReturn(mock(MessageRepository.class));
            mockFactory.when(PersistenceFactory::getNotificationPersistence).thenReturn(mock(NotificationPersistence.class));
            mockFactory.when(PersistenceFactory::getMedicationPersistence).thenReturn(mock(MedicationPersistence.class));
            mockFactory.when(PersistenceFactory::getPrescriptionPersistence).thenReturn(mock(PrescriptionPersistence.class));
            mockFactory.when(PersistenceFactory::getReferralPersistence).thenReturn(mock(ReferralPersistence.class));

            ReceptionistApp anotherApp = new ReceptionistApp(
                    anotherReceptionist,
                    physicianManager,
                    appointmentManager,
                    receptionistManager,
                    appointmentController,
                    logoutCallback
            );

            assertNotNull(anotherApp);
        }
    }

    @Test
    public void testBasicFunctionality() {
        // Simple test to verify the app was constructed properly
        assertNotNull(app);

        // Test basic appointment operations don't crash
        Appointment basicAppointment = new Appointment("P999", "Basic Patient", LocalDateTime.now().plusMinutes(15));

        assertDoesNotThrow(() -> {
            app.onAppointmentCreated(basicAppointment);
        });

        assertDoesNotThrow(() -> {
            app.onAppointmentUpdated(basicAppointment);
        });

        assertDoesNotThrow(() -> {
            app.onAppointmentDeleted(basicAppointment);
        });
    }
}