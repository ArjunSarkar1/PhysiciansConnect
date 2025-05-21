package logic;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import logic.stub.AppointmentLogic;
import objects.Appointment;
import objects.Physician;
import objects.Patient;
import java.util.Map;

public class AppointmentLogicTest {
    private AppointmentLogic appointmentLogic;
    private Appointment testAppointment;
    private Physician testPhysician;
    private Patient testPatient;

    @Before
    public void setUp() {
        appointmentLogic = new AppointmentLogic();

        // Create test physician
        testPhysician = new Physician(1, "John", "Doe", "john.doe@example.com", 123);

        // Create test patient
        testPatient = new Patient(1, "Jane", "Smith", "jane.smith@example.com", "password123");

        // Create test appointment
        testAppointment = new Appointment(
                0, // ID will be assigned by the stub
                testPhysician,
                testPatient,
                1, // officeId
                30, // duration in minutes
                "Regular checkup",
                "No special notes",
                "No feedback yet",
                "No referral",
                "PENDING");
    }

    @Test
    public void testGetAppointmentById_ValidId() {
        // Add an appointment first
        Appointment added = appointmentLogic.addAppointment(testAppointment);
        assertNotNull("Added appointment should not be null", added);

        // Test getting the appointment
        Appointment retrieved = appointmentLogic.getAppointmentById(added.getAppointmentId());
        assertNotNull("Retrieved appointment should not be null", retrieved);
        assertEquals("Appointment IDs should match", added.getAppointmentId(), retrieved.getAppointmentId());
    }

    @Test
    public void testGetAppointmentById_InvalidId() {
        Appointment retrieved = appointmentLogic.getAppointmentById(-1);
        assertNull("Retrieved appointment should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllAppointments() {
        // Add multiple appointments
        Appointment appointment1 = appointmentLogic.addAppointment(testAppointment);
        Appointment appointment2 = appointmentLogic.addAppointment(testAppointment);

        Map<Integer, Appointment> allAppointments = appointmentLogic.getAllAppointments();
        assertNotNull("All appointments map should not be null", allAppointments);
        assertTrue("Should contain at least 2 appointments", allAppointments.size() >= 2);
    }

    @Test
    public void testAddAppointment_Valid() {
        Appointment added = appointmentLogic.addAppointment(testAppointment);
        assertNotNull("Added appointment should not be null", added);
        assertTrue("Appointment ID should be positive", added.getAppointmentId() > 0);
    }

    @Test
    public void testAddAppointment_Null() {
        Appointment added = appointmentLogic.addAppointment(null);
        assertNull("Added appointment should be null for null input", added);
    }

    @Test
    public void testUpdateAppointment_Valid() {
        // Add an appointment first
        Appointment added = appointmentLogic.addAppointment(testAppointment);
        assertNotNull("Added appointment should not be null", added);

        // Update the appointment
        added.setReasonForVisit("Updated reason");
        Appointment updated = appointmentLogic.updateAppointment(added);
        assertNotNull("Updated appointment should not be null", updated);
        assertEquals("Reason should be updated", "Updated reason", updated.getReasonForVisit());
    }

    @Test
    public void testUpdateAppointment_Invalid() {
        Appointment updated = appointmentLogic.updateAppointment(null);
        assertNull("Updated appointment should be null for null input", updated);
    }

    @Test
    public void testDeleteAppointment_Valid() {
        // Add an appointment first
        Appointment added = appointmentLogic.addAppointment(testAppointment);
        assertNotNull("Added appointment should not be null", added);

        // Delete the appointment
        Appointment deleted = appointmentLogic.deleteAppointment(added.getAppointmentId());
        assertNotNull("Deleted appointment should not be null", deleted);
        assertEquals("Deleted appointment ID should match", added.getAppointmentId(), deleted.getAppointmentId());

        // Verify it's deleted
        Appointment retrieved = appointmentLogic.getAppointmentById(added.getAppointmentId());
        assertNull("Retrieved appointment should be null after deletion", retrieved);
    }

    @Test
    public void testDeleteAppointment_Invalid() {
        Appointment deleted = appointmentLogic.deleteAppointment(-1);
        assertNull("Deleted appointment should be null for invalid ID", deleted);
    }
}