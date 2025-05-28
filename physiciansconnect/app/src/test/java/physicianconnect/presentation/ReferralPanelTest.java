package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import physicianconnect.logic.ReferralManager;
import physicianconnect.objects.Referral;
import physicianconnect.persistence.stub.ReferralPersistenceStub;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReferralPanelTest {

    private ReferralPanel panel;
    private ReferralManager manager;

    @BeforeEach
    public void setup() {
        manager = new ReferralManager(new ReferralPersistenceStub(false));
        panel = new ReferralPanel(manager, "doc1", List.of("Patient A", "Patient B"));
    }

    @Test
    public void testCreateReferralUpdatesList() {
        // Simulate entering data and clicking create
        JComboBox<?> patientCombo = (JComboBox<?>) TestUtils.getChildNamed(panel, "patientCombo");
        JTextField typeField = (JTextField) TestUtils.getChildNamed(panel, "typeField");
        JTextArea detailsArea = (JTextArea) TestUtils.getChildNamed(panel, "detailsArea");
        JButton createButton = (JButton) TestUtils.getChildNamed(panel, "createButton");
        JTextArea referralListArea = (JTextArea) TestUtils.getChildNamed(panel, "referralListArea");

        patientCombo.setSelectedItem("Patient A");
        typeField.setText("Lab Test");
        detailsArea.setText("Fasting required");

        // Simulate button click
        createButton.doClick();

        // Check that the referral appears in the list area
        String text = referralListArea.getText();
        assertTrue(text.contains("Lab Test"));
        assertTrue(text.contains("Fasting required"));
    }
}

// Utility for finding named components in a container
class TestUtils {
    public static Component getChildNamed(Container parent, String name) {
        for (Component c : parent.getComponents()) {
            if (name.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component child = getChildNamed((Container) c, name);
                if (child != null) return child;
            }
        }
        return null;
    }
}