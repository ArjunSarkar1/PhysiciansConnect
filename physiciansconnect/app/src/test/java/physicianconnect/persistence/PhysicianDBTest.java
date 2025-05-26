package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.sqlite.PhysicianDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

public class PhysicianDBTest {

    private Connection conn;
    private PhysicianDB db;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new PhysicianDB(conn);
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndFetchPhysician() {
        Physician p = new Physician("abc123", "Dr. Watson", "watson@email.com", "secret");
        db.addPhysician(p);

        Physician fetched = db.getPhysicianById("abc123");
        assertNotNull(fetched);
        assertEquals("Dr. Watson", fetched.getName());
        assertEquals("secret", fetched.getPassword());
    }

    @Test
    public void testDuplicatePhysicianIsIgnored() {
        Physician p1 = new Physician("x", "A", "a@email.com", "pw1");
        Physician p2 = new Physician("x", "B", "b@email.com", "pw2");

        db.addPhysician(p1);
        db.addPhysician(p2); // Duplicate ID, should be ignored

        Physician result = db.getPhysicianById("x");
        assertNotNull(result);
        assertEquals("A", result.getName()); // Should still be p1
        assertEquals("pw1", result.getPassword());
    }

    @Test
    public void testDeletePhysician() {
        Physician p = new Physician("delme", "Dr. Doom", "doom@latveria.com", "mask");
        db.addPhysician(p);

        db.deletePhysicianById("delme");
        assertNull(db.getPhysicianById("delme"));
    }
}
