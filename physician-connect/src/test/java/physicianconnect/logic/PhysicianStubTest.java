package persistence;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import persistence.stub.PhysicianStub;
import objects.Physician;
import java.util.Map;

public class PhysicianStubTest {
    private PhysicianStub physicianStub;
    private Physician testPhysician;

    @Before
    public void setUp() {
        physicianStub = PhysicianStub.getInstance();
        
        // Create test physician
        testPhysician = new Physician(
            0, // ID will be assigned by the stub
            "John",
            "Smith",
            "john.smith@example.com",
            1 // officeId
        );
    }

    @Test
    public void testGetPhysician_ValidId() {
        // Add a physician first
        Physician added = physicianStub.addPhysician(testPhysician);
        assertNotNull("Added physician should not be null", added);
        
        // Test getting the physician
        Physician retrieved = physicianStub.getPhysician(added.getUserId());
        assertNotNull("Retrieved physician should not be null", retrieved);
        assertEquals("Physician IDs should match", added.getUserId(), retrieved.getUserId());
        assertEquals("First name should match", "John", retrieved.getFirstName());
        assertEquals("Last name should match", "Smith", retrieved.getLastName());
    }

    @Test
    public void testGetPhysician_InvalidId() {
        Physician retrieved = physicianStub.getPhysician(-1);
        assertNull("Retrieved physician should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllPhysicians() {
        // Add multiple physicians
        Physician physician1 = physicianStub.addPhysician(testPhysician);
        Physician physician2 = physicianStub.addPhysician(testPhysician);
        
        Map<Integer, Physician> allPhysicians = physicianStub.getAllPhysicians();
        assertNotNull("All physicians map should not be null", allPhysicians);
        assertTrue("Should contain at least 2 physicians", allPhysicians.size() >= 2);
    }

    @Test
    public void testAddPhysician_Valid() {
        Physician added = physicianStub.addPhysician(testPhysician);
        assertNotNull("Added physician should not be null", added);
        assertTrue("Physician ID should be positive", added.getUserId() > 0);
        assertEquals("Email should match", "john.smith@example.com", added.getEmail());
        assertEquals("Office ID should match", 1, added.getOfficeId());
    }

    @Test
    public void testAddPhysician_Null() {
        Physician added = physicianStub.addPhysician(null);
        assertNull("Added physician should be null for null input", added);
    }

    @Test
    public void testUpdatePhysician_Valid() {
        // Add a physician first
        Physician added = physicianStub.addPhysician(testPhysician);
        assertNotNull("Added physician should not be null", added);
        
        // Update the physician
        added.setEmail("updated.email@example.com");
        Physician updated = physicianStub.updatePhysician(added);
        assertNotNull("Updated physician should not be null", updated);
        assertEquals("Email should be updated", "updated.email@example.com", updated.getEmail());
    }

    @Test
    public void testUpdatePhysician_Invalid() {
        Physician updated = physicianStub.updatePhysician(null);
        assertNull("Updated physician should be null for null input", updated);
    }

    @Test
    public void testDeletePhysician_Valid() {
        // Add a physician first
        Physician added = physicianStub.addPhysician(testPhysician);
        assertNotNull("Added physician should not be null", added);
        
        // Delete the physician
        Physician deleted = physicianStub.deletePhysician(added.getUserId());
        assertNotNull("Deleted physician should not be null", deleted);
        assertEquals("Deleted physician ID should match", added.getUserId(), deleted.getUserId());
        
        // Verify it's deleted
        Physician retrieved = physicianStub.getPhysician(added.getUserId());
        assertNull("Retrieved physician should be null after deletion", retrieved);
    }

    @Test
    public void testDeletePhysician_Invalid() {
        Physician deleted = physicianStub.deletePhysician(-1);
        assertNull("Deleted physician should be null for invalid ID", deleted);
    }
} 