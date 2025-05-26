package physicianconnect.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import physicianconnect.exceptions.InvalidAppointmentException;
import physicianconnect.objects.Appointment;

public class AppointmentValidatorTest {

    @Test
    public void testValidAppointmentPasses() {
        Appointment valid = new Appointment("doc-id", "Patient X", LocalDateTime.now());
        assertDoesNotThrow(() -> AppointmentValidator.validate(valid));
    }

    @Test
    public void testNullAppointmentThrows() {
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(null));
    }

    @Test
    public void testBlankPhysicianIdThrows() {
        Appointment a = new Appointment(" ", "Patient Y", LocalDateTime.now());
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a));
    }

    @Test
    public void testBlankPatientNameThrows() {
        Appointment a = new Appointment("doc-id", "   ", LocalDateTime.now());
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a));
    }

    @Test
    public void testNullDateTimeThrows() {
        Appointment a = new Appointment("doc-id", "Patient Z", null);
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a));
    }
}
