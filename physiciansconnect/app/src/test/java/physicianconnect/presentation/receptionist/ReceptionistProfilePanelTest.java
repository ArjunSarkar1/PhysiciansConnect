package physicianconnect.presentation.receptionist;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.objects.Receptionist;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

class ReceptionistProfilePanelTest {

    Receptionist receptionist;
    ReceptionistManager receptionistManager;

    @BeforeEach
    void setup() {
        receptionist = mock(Receptionist.class);
        receptionistManager = mock(ReceptionistManager.class);
        when(receptionist.getId()).thenReturn("123");
        when(receptionist.getName()).thenReturn("Jane Doe");
        when(receptionist.getEmail()).thenReturn("jane@example.com");
    }

    @Test
    void testInitialState() {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JTextField nameField = (JTextField) getField(panel, "nameField");
        JTextField emailField = (JTextField) getField(panel, "emailField");
        JButton editButton = (JButton) getField(panel, "editButton");
        JButton saveButton = (JButton) getField(panel, "saveButton");
        JButton cancelButton = (JButton) getField(panel, "cancelButton");

        assertEquals("Jane Doe", nameField.getText());
        assertEquals("jane@example.com", emailField.getText());
        assertFalse(nameField.isEditable());
        assertFalse(saveButton.isVisible());
        assertFalse(cancelButton.isVisible());
        assertTrue(editButton.isVisible());
    }

    @Test
    void testEditAndCancel() {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JButton editButton = (JButton) getField(panel, "editButton");
        JButton cancelButton = (JButton) getField(panel, "cancelButton");
        JButton saveButton = (JButton) getField(panel, "saveButton");
        JTextField nameField = (JTextField) getField(panel, "nameField");

        // Click edit
        editButton.doClick();
        assertTrue(nameField.isEditable());
        assertTrue(saveButton.isVisible());
        assertTrue(cancelButton.isVisible());
        assertFalse(editButton.isVisible());

        // Change name and cancel
        nameField.setText("Changed Name");
        cancelButton.doClick();
        assertEquals("Jane Doe", nameField.getText());
        assertFalse(nameField.isEditable());
        assertFalse(saveButton.isVisible());
        assertFalse(cancelButton.isVisible());
        assertTrue(editButton.isVisible());
    }

    @Test
    void testEditAndSaveValid() {
        Runnable profileUpdated = mock(Runnable.class);
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, profileUpdated);

        JButton editButton = (JButton) getField(panel, "editButton");
        JButton saveButton = (JButton) getField(panel, "saveButton");
        JTextField nameField = (JTextField) getField(panel, "nameField");

        editButton.doClick();
        nameField.setText("New Name");

        doNothing().when(receptionistManager).validateAndUpdateReceptionist(receptionist, "New Name", true, true, true);

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            paneMock.when(() -> JOptionPane.showMessageDialog(any(), eq(UIConfig.PROFILE_UPDATED_MESSAGE))).thenAnswer(inv -> null);
            saveButton.doClick();
            verify(receptionistManager).validateAndUpdateReceptionist(receptionist, "New Name", true, true, true);
            verify(profileUpdated).run();
            assertFalse(nameField.isEditable());
        }
    }

    @Test
    void testEditAndSaveInvalid() {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JButton editButton = (JButton) getField(panel, "editButton");
        JButton saveButton = (JButton) getField(panel, "saveButton");
        JTextField nameField = (JTextField) getField(panel, "nameField");

        editButton.doClick();
        nameField.setText("Invalid Name");

        doThrow(new IllegalArgumentException("Invalid name")).when(receptionistManager)
                .validateAndUpdateReceptionist(receptionist, "Invalid Name", true, true, true);

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            paneMock.when(() -> JOptionPane.showMessageDialog(any(), eq("Invalid name"), eq(UIConfig.VALIDATION_ERROR_TITLE), eq(JOptionPane.ERROR_MESSAGE)))
                    .thenAnswer(inv -> null);
            saveButton.doClick();
            verify(receptionistManager).validateAndUpdateReceptionist(receptionist, "Invalid Name", true, true, true);
            paneMock.verify(() -> JOptionPane.showMessageDialog(any(), eq("Invalid name"), eq(UIConfig.VALIDATION_ERROR_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
            assertTrue(nameField.isEditable());
        }
    }

    @Test
    void testSignOutButtonCallsLogoutCallback() {
        Runnable logoutCallback = mock(Runnable.class);
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, logoutCallback, null);

        JButton signOutButton = (JButton) getField(panel, "signOutButton");
        signOutButton.doClick();
        verify(logoutCallback).run();
    }

    @Test
    void testSignOutButtonDisposesWindowAndCallsLogout() {
        Runnable logoutCallback = mock(Runnable.class);
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, logoutCallback, null);

        // Create a fake window and add the panel to it
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        JButton signOutButton = (JButton) getField(panel, "signOutButton");
        signOutButton.doClick();

        // The frame should be disposed and callback called
        assertFalse(frame.isDisplayable());
        verify(logoutCallback).run();
    }

    @Test
    void testChooseAndUploadPhotoApproveAndSuccess() throws Exception {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JFileChooser chooser = mock(JFileChooser.class);
        File file = File.createTempFile("r_123", ".png");
        file.deleteOnExit();

        // Mock JFileChooser to return APPROVE_OPTION and a file
        try (MockedStatic<JFileChooser> chooserMock = mockStatic(JFileChooser.class, CALLS_REAL_METHODS)) {
            chooserMock.when(() -> new JFileChooser(anyString())).thenReturn(chooser);
            when(chooser.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
            when(chooser.getSelectedFile()).thenReturn(file);

            // Mock uploadProfilePhoto to do nothing
            doNothing().when(receptionistManager).uploadProfilePhoto(eq("123"), any(InputStream.class));

            // Call private method via reflection
            Method m = ReceptionistProfilePanel.class.getDeclaredMethod("chooseAndUploadPhoto");
            m.setAccessible(true);
            m.invoke(panel);

            verify(receptionistManager).uploadProfilePhoto(eq("123"), any(InputStream.class));
        }
    }

    @Test
    void testChooseAndUploadPhotoApproveAndIOException() throws Exception {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JFileChooser chooser = mock(JFileChooser.class);
        File file = File.createTempFile("r_123", ".png");
        file.deleteOnExit();

        try (MockedStatic<JFileChooser> chooserMock = mockStatic(JFileChooser.class, CALLS_REAL_METHODS);
             MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            chooserMock.when(() -> new JFileChooser(anyString())).thenReturn(chooser);
            when(chooser.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
            when(chooser.getSelectedFile()).thenReturn(file);

            // Simulate IOException on FileInputStream
            Method m = ReceptionistProfilePanel.class.getDeclaredMethod("chooseAndUploadPhoto");
            m.setAccessible(true);

            // Replace FileInputStream with a throwing version using PowerMockito or similar if needed,
            // or just simulate by throwing in uploadProfilePhoto
            doThrow(new IOException("fail")).when(receptionistManager).uploadProfilePhoto(anyString(), any(InputStream.class));

            m.invoke(panel);

            paneMock.verify(() -> JOptionPane.showMessageDialog(any(), contains(UIConfig.PHOTO_UPLOAD_FAILED_MSG), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testLoadProfilePhotoFileExists() throws Exception {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        // Create a temp file to simulate the photo
        File dir = new File("src/main/resources/profile_photos");
        dir.mkdirs();
        File file = new File(dir, "r_123.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(new byte[10]);
        }

        Method m = ReceptionistProfilePanel.class.getDeclaredMethod("loadProfilePhoto", String.class);
        m.setAccessible(true);
        m.invoke(panel, "123");

        JLabel photoLabel = (JLabel) getField(panel, "photoLabel");
        assertNotNull(photoLabel.getIcon());

        file.delete();
    }

    @Test
    void testLoadProfilePhotoFileNotExists() throws Exception {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        // Ensure file does not exist
        File file = new File("src/main/resources/profile_photos/r_123.png");
        if (file.exists()) file.delete();

        Method m = ReceptionistProfilePanel.class.getDeclaredMethod("loadProfilePhoto", String.class);
        m.setAccessible(true);
        m.invoke(panel, "123");

        JLabel photoLabel = (JLabel) getField(panel, "photoLabel");
        assertNotNull(photoLabel.getIcon());
    }

    // --- Helpers ---
    private Object getField(Object obj, String name) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}