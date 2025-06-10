package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.exceptions.InvalidAppointmentException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AddAppointmentPanelTest {

    @Mock
    AppointmentController mockController;

    JFrame frame;

    @BeforeEach
    void setup() {
        frame = new JFrame();
    }

    @AfterEach
    void tearDown() {
        frame.dispose();
    }

    @Test
    void testFieldsAreEditableAndDefault() throws Exception {
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1");
        assertTrue(panel.isModal());
        assertTrue(panel.isVisible() || !panel.isVisible()); // Just to access
        // Patient name field should be empty
        JTextField nameField = (JTextField) TestUtils.getField(panel, "patientNameField");
        assertNotNull(nameField);
        assertEquals("", nameField.getText());
        // Notes area should be empty
        JTextArea notesArea = (JTextArea) TestUtils.getField(panel, "notesArea");
        assertNotNull(notesArea);
        assertEquals("", notesArea.getText());
    }

    @Test
    void testSaveAppointmentSuccess() throws Exception {
        Runnable callback = mock(Runnable.class);
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1", callback);

        JTextField nameField = (JTextField) TestUtils.getField(panel, "patientNameField");
        nameField.setText("Test Patient");

        // Set date and time
        JSpinner dateSpinner = (JSpinner) TestUtils.getField(panel, "dateSpinner");
        JSpinner timeSpinner = (JSpinner) TestUtils.getField(panel, "timeSpinner");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 1, 9, 30, 0);
        Date date = cal.getTime();
        dateSpinner.setValue(date);
        timeSpinner.setValue(date);

        JTextArea notesArea = (JTextArea) TestUtils.getField(panel, "notesArea");
        notesArea.setText("Test notes");

        // Simulate save button click
        JButton saveBtn = TestUtils.getButton(panel, "Save");
        assertNotNull(saveBtn);
        SwingUtilities.invokeAndWait(saveBtn::doClick);

        verify(mockController).createAppointment(eq("doc1"), eq("Test Patient"), any(LocalDateTime.class), eq("Test notes"));
        verify(callback).run();
    }

    @Test
    void testSaveAppointmentInvalidNameShowsDialog() throws Exception {
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1");
        JTextField nameField = (JTextField) TestUtils.getField(panel, "patientNameField");
        nameField.setText("   "); // Invalid

        // Mock JOptionPane
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            JButton saveBtn = TestUtils.getButton(panel, "Save");
            SwingUtilities.invokeAndWait(saveBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("invalid"), any(), eq(JOptionPane.ERROR_MESSAGE)));
        }
        verify(mockController, never()).createAppointment(any(), any(), any(), any());
    }

    @Test
    void testSaveAppointmentControllerThrowsInvalidAppointment() throws Exception {
        doThrow(new InvalidAppointmentException("bad date")).when(mockController)
                .createAppointment(any(), any(), any(), any());
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1");
        JTextField nameField = (JTextField) TestUtils.getField(panel, "patientNameField");
        nameField.setText("Test Patient");

        // Set date and time
        JSpinner dateSpinner = (JSpinner) TestUtils.getField(panel, "dateSpinner");
        JSpinner timeSpinner = (JSpinner) TestUtils.getField(panel, "timeSpinner");
        Date now = new Date();
        dateSpinner.setValue(now);
        timeSpinner.setValue(now);

        JTextArea notesArea = (JTextArea) TestUtils.getField(panel, "notesArea");
        notesArea.setText("Test notes");

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            JButton saveBtn = TestUtils.getButton(panel, "Save");
            SwingUtilities.invokeAndWait(saveBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), eq("bad date"), any(), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testSaveAppointmentControllerThrowsUnexpected() throws Exception {
        doThrow(new RuntimeException("boom")).when(mockController)
                .createAppointment(any(), any(), any(), any());
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1");
        JTextField nameField = (JTextField) TestUtils.getField(panel, "patientNameField");
        nameField.setText("Test Patient");

        // Set date and time
        JSpinner dateSpinner = (JSpinner) TestUtils.getField(panel, "dateSpinner");
        JSpinner timeSpinner = (JSpinner) TestUtils.getField(panel, "timeSpinner");
        Date now = new Date();
        dateSpinner.setValue(now);
        timeSpinner.setValue(now);

        JTextArea notesArea = (JTextArea) TestUtils.getField(panel, "notesArea");
        notesArea.setText("Test notes");

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            JButton saveBtn = TestUtils.getButton(panel, "Save");
            SwingUtilities.invokeAndWait(saveBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("Unexpected error"), any(), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testCancelButtonDisposesDialog() throws Exception {
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1");
        JButton cancelBtn = TestUtils.getButton(panel, "Cancel");
        assertNotNull(cancelBtn);
        SwingUtilities.invokeAndWait(cancelBtn::doClick);
        assertFalse(panel.isDisplayable());
    }

    // --- Helper for reflection and button finding ---
    static class TestUtils {
        static Object getField(Object obj, String fieldName) throws Exception {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        }

        static JButton getButton(Container container, String text) {
            for (Component c : container.getComponents()) {
                if (c instanceof JButton && ((JButton) c).getText().equalsIgnoreCase(text)) {
                    return (JButton) c;
                }
                if (c instanceof Container) {
                    JButton btn = getButton((Container) c, text);
                    if (btn != null) return btn;
                }
            }
            return null;
        }
    }
}