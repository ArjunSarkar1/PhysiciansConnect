package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Appointment;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentPersistenceStubTest {

    private AppointmentPersistenceStub stub;

    @BeforeEach
    public void setup() {
        stub = new AppointmentPersistenceStub(true);
    }

    @Test
    public void testGetAppointmentsForPhysician() {
        List<Appointment> list = stub.getAppointmentsForPhysician("1");
        assertFalse(list.isEmpty());
    }

    @Test
    public void testAddAndDeleteAppointment() {
        Appointment a = new Appointment("stubDoc", "Stub Patient", LocalDateTime.now());
        stub.addAppointment(a);
        assertFalse(stub.getAppointmentsForPhysician("stubDoc").isEmpty());
        stub.deleteAppointment(a);
        assertTrue(stub.getAppointmentsForPhysician("stubDoc").isEmpty());
    }

    @Test
    public void testDeleteAllAppointments() {
        stub.deleteAllAppointments();
        assertTrue(stub.getAppointmentsForPhysician("1").isEmpty());
    }
}