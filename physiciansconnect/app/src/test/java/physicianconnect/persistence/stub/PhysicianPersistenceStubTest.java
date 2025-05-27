package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Physician;

import static org.junit.jupiter.api.Assertions.*;

public class PhysicianPersistenceStubTest {

    private PhysicianPersistenceStub stub;

    @BeforeEach
    public void setup() {
        stub = new PhysicianPersistenceStub(true);
    }

    @Test
    public void testAddAndGetPhysician() {
        Physician p = new Physician("stubid", "Stub Doc", "stub@doc.com", "pw");
        stub.addPhysician(p);
        assertEquals("Stub Doc", stub.getPhysicianById("stubid").getName());
    }
}