package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.AppController;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginScreenTest {

    @Mock
    PhysicianManager physicianManager;
    @Mock
    AppointmentManager appointmentManager;
    @Mock
    ReceptionistManager receptionistManager;
    @Mock
    AppController appController;

    JFrame frame;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        frame = new JFrame();
    }

    @AfterEach
    void tearDown() {
        frame.dispose();
    }

    @Test
    void testFieldsAndButtonsPresent() {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JTextField emailField = (JTextField) getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) getComponentByName(screen, "loginBtn");
        JButton createBtn = (JButton) getComponentByName(screen, "createBtn");
        assertNotNull(emailField);
        assertNotNull(passField);
        assertNotNull(loginBtn);
        assertNotNull(createBtn);
        screen.dispose();
    }

    @Test
    void testPhysicianLoginSuccess() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JTextField emailField = (JTextField) getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) getComponentByName(screen, "loginBtn");

        emailField.setText("doc@a.com");
        passField.setText("pw");

        Physician mockPhysician = mock(Physician.class);

        try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                (mock, context) -> when(mock.login(anyString(), anyString())).thenReturn(mockPhysician))) {
            SwingUtilities.invokeAndWait(loginBtn::doClick);
            verify(appController).showPhysicianApp(mockPhysician);
        }
        screen.dispose();
    }

    @Test
    void testReceptionistLoginSuccess() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JTextField emailField = (JTextField) getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) getComponentByName(screen, "loginBtn");

        emailField.setText("rec@a.com");
        passField.setText("pw");

        try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                (mock, context) -> when(mock.login(anyString(), anyString()))
                        .thenThrow(new InvalidCredentialException("not a physician")))) {
            Receptionist mockReceptionist = mock(Receptionist.class);
            try (MockedConstruction<ReceptionistController> mockReceptionistCtor = mockConstruction(ReceptionistController.class,
                    (mock, context) -> when(mock.login(anyString(), anyString())).thenReturn(mockReceptionist))) {
                SwingUtilities.invokeAndWait(loginBtn::doClick);
                verify(appController).showReceptionistApp(mockReceptionist);
            }
        }
        screen.dispose();
    }

    @Test
    void testLoginFailureShowsDialog() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JTextField emailField = (JTextField) getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) getComponentByName(screen, "loginBtn");

        emailField.setText("fail@a.com");
        passField.setText("pw");

        try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                (mock, context) -> when(mock.login(anyString(), anyString()))
                        .thenThrow(new InvalidCredentialException("not a physician")))) {
            try (MockedConstruction<ReceptionistController> mockReceptionistCtor = mockConstruction(ReceptionistController.class,
                    (mock, context) -> when(mock.login(anyString(), anyString()))
                            .thenThrow(new InvalidCredentialException("not a receptionist")))) {
                try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                    SwingUtilities.invokeAndWait(loginBtn::doClick);
                    mockedPane.verify(() -> JOptionPane.showMessageDialog(
                            any(), eq("not a receptionist"), any(), eq(JOptionPane.ERROR_MESSAGE)));
                }
            }
        }
        screen.dispose();
    }

    @Test
    void testCreateAccountDialogValidationAndSuccess() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JButton createBtn = (JButton) getComponentByName(screen, "createBtn");

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            // Simulate user entering mismatched passwords
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), contains("email"))).thenReturn("new@user.com");
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), contains("name"))).thenReturn("New User");
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), contains("password"))).thenReturn("pw1");
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), contains("again"))).thenReturn("pw2");
            mockedPane.when(() -> JOptionPane.showConfirmDialog(any(), contains("Physician"), any(), anyInt()))
                    .thenReturn(JOptionPane.YES_OPTION);

            SwingUtilities.invokeAndWait(createBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("Passwords do not match"), any(), eq(JOptionPane.ERROR_MESSAGE)));

            // Now simulate matching passwords and successful registration as physician
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), contains("password"))).thenReturn("pw");
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), contains("again"))).thenReturn("pw");
            mockedPane.when(() -> JOptionPane.showConfirmDialog(any(), contains("Physician"), any(), anyInt()))
                    .thenReturn(JOptionPane.YES_OPTION);

            Physician mockPhysician = mock(Physician.class);
            when(physicianManager.getPhysicianByEmail("new@user.com")).thenReturn(null);

            try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                    (mock, context) -> when(mock.register(anyString(), anyString(), anyString(), anyString())).thenReturn(mockPhysician))) {
                SwingUtilities.invokeAndWait(createBtn::doClick);
                mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("Account created"), any(), eq(JOptionPane.INFORMATION_MESSAGE)));
            }

            // Now simulate duplicate email
            when(physicianManager.getPhysicianByEmail("new@user.com")).thenReturn(mockPhysician);
            SwingUtilities.invokeAndWait(createBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("already exists"), any(), eq(JOptionPane.ERROR_MESSAGE)));
        }
        screen.dispose();
    }

    

    
    @Test
    void testPhysicianLoginSuccessDirect() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JTextField emailField = (JTextField) getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) getComponentByName(screen, "loginBtn");
    
        emailField.setText("doc@a.com");
        passField.setText("pw");
    
        Physician mockPhysician = mock(Physician.class);
    
        try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                (mock, context) -> when(mock.login(anyString(), anyString())).thenReturn(mockPhysician))) {
            SwingUtilities.invokeAndWait(loginBtn::doClick);
            verify(appController).showPhysicianApp(mockPhysician);
        }
        screen.dispose();
    }
    
    @Test
    void testReceptionistLoginSuccessAfterPhysicianFail() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JTextField emailField = (JTextField) getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) getComponentByName(screen, "loginBtn");
    
        emailField.setText("rec@a.com");
        passField.setText("pw");
    
        try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                (mock, context) -> when(mock.login(anyString(), anyString()))
                        .thenThrow(new InvalidCredentialException("not a physician")))) {
            Receptionist mockReceptionist = mock(Receptionist.class);
            try (MockedConstruction<ReceptionistController> mockReceptionistCtor = mockConstruction(ReceptionistController.class,
                    (mock, context) -> when(mock.login(anyString(), anyString())).thenReturn(mockReceptionist))) {
                SwingUtilities.invokeAndWait(loginBtn::doClick);
                verify(appController).showReceptionistApp(mockReceptionist);
            }
        }
        screen.dispose();
    }
    
    @Test
    void testRegistrationDialogValidation_invalidEmail() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JButton createBtn = (JButton) getComponentByName(screen, "createBtn");
    
        SwingUtilities.invokeLater(createBtn::doClick);
        // Simulate dialog fields and click register
        JDialog dialog = findDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE);
        JTextField nameField = findField(dialog, JTextField.class, 0);
        JTextField emailField = findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
    
        nameField.setText("Test User");
        emailField.setText("bademail");
        passwordField.setText("abcdef");
        confirmPasswordField.setText("abcdef");
    
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            SwingUtilities.invokeAndWait(registerBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_INVALID_EMAIL, "Error", JOptionPane.ERROR_MESSAGE));
        }
        dialog.dispose();
        screen.dispose();
    }
    
    @Test
    void testRegistrationDialogValidation_shortPassword() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JButton createBtn = (JButton) getComponentByName(screen, "createBtn");
    
        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = findDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE);
        JTextField nameField = findField(dialog, JTextField.class, 0);
        JTextField emailField = findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
    
        nameField.setText("Test User");
        emailField.setText("test@a.com");
        passwordField.setText("abc");
        confirmPasswordField.setText("abc");
    
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            SwingUtilities.invokeAndWait(registerBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_PASSWORD_LENGTH, UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
        dialog.dispose();
        screen.dispose();
    }
    
    @Test
    void testRegistrationDialogValidation_passwordMismatch() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JButton createBtn = (JButton) getComponentByName(screen, "createBtn");
    
        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = findDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE);
        JTextField nameField = findField(dialog, JTextField.class, 0);
        JTextField emailField = findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
    
        nameField.setText("Test User");
        emailField.setText("test@a.com");
        passwordField.setText("abcdef");
        confirmPasswordField.setText("ghijkl");
    
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            SwingUtilities.invokeAndWait(registerBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_PASSWORD_MISMATCH, UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
        dialog.dispose();
        screen.dispose();
    }
    
    @Test
    void testRegistrationDialogValidation_duplicateEmail() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        JButton createBtn = (JButton) getComponentByName(screen, "createBtn");
    
        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = findDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE);
        JTextField nameField = findField(dialog, JTextField.class, 0);
        JTextField emailField = findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
    
        nameField.setText("Test User");
        emailField.setText("test@a.com");
        passwordField.setText("abcdef");
        confirmPasswordField.setText("abcdef");
    
        when(physicianManager.getPhysicianByEmail("test@a.com")).thenReturn(mock(physicianconnect.objects.Physician.class));
    
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            SwingUtilities.invokeAndWait(registerBtn::doClick);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_EMAIL_EXISTS, UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
        dialog.dispose();
        screen.dispose();
    }
    
@Test
void testRegistrationDialogSuccessPhysicianAndReceptionist() throws Exception {
    LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
    JButton createBtn = (JButton) getComponentByName(screen, "createBtn");

    // Physician registration
    SwingUtilities.invokeLater(createBtn::doClick);
    JDialog dialog1 = findDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE);
    JComboBox<?> userTypeCombo1 = findComboBox(dialog1);
    JTextField nameField1 = findField(dialog1, JTextField.class, 0);
    JTextField emailField1 = findField(dialog1, JTextField.class, 1);
    JPasswordField passwordField1 = findField(dialog1, JPasswordField.class, 0);
    JPasswordField confirmPasswordField1 = findField(dialog1, JPasswordField.class, 1);
    JButton registerBtn1 = findButton(dialog1, UIConfig.REGISTER_BUTTON_TEXT);

    userTypeCombo1.setSelectedItem("Physician");
    nameField1.setText("Doc");
    emailField1.setText("doc@a.com");
    passwordField1.setText("abcdef");
    confirmPasswordField1.setText("abcdef");

    when(physicianManager.getPhysicianByEmail("doc@a.com")).thenReturn(null);
    when(receptionistManager.getReceptionistByEmail("doc@a.com")).thenReturn(null);

    Physician mockPhysician = mock(Physician.class);
    try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
            (mock, context) -> when(mock.register(anyString(), anyString(), anyString(), anyString())).thenReturn(mockPhysician));
         MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
        SwingUtilities.invokeAndWait(registerBtn1::doClick);
        mockedPane.verify(() -> JOptionPane.showMessageDialog(dialog1, UIConfig.SUCCESS_ACCOUNT_CREATED, UIConfig.SUCCESS_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE));
        // Optionally verify appController.showPhysicianApp(mockPhysician) via SwingUtilities.invokeLater
    }
    dialog1.dispose();

    // Receptionist registration
    SwingUtilities.invokeLater(createBtn::doClick);
    JDialog dialog2 = findDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE);
    JComboBox<?> userTypeCombo2 = findComboBox(dialog2);
    JTextField nameField2 = findField(dialog2, JTextField.class, 0);
    JTextField emailField2 = findField(dialog2, JTextField.class, 1);
    JPasswordField passwordField2 = findField(dialog2, JPasswordField.class, 0);
    JPasswordField confirmPasswordField2 = findField(dialog2, JPasswordField.class, 1);
    JButton registerBtn2 = findButton(dialog2, UIConfig.REGISTER_BUTTON_TEXT);

    userTypeCombo2.setSelectedItem("Receptionist");
    nameField2.setText("Rec");
    emailField2.setText("rec@a.com");
    passwordField2.setText("abcdef");
    confirmPasswordField2.setText("abcdef");

    when(physicianManager.getPhysicianByEmail("rec@a.com")).thenReturn(null);
    when(receptionistManager.getReceptionistByEmail("rec@a.com")).thenReturn(null);

    Receptionist mockReceptionist = mock(Receptionist.class);
    try (MockedConstruction<ReceptionistController> mockReceptionistCtor = mockConstruction(ReceptionistController.class,
            (mock, context) -> when(mock.register(anyString(), anyString(), anyString(), anyString())).thenReturn(mockReceptionist));
         MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
        SwingUtilities.invokeAndWait(registerBtn2::doClick);
        mockedPane.verify(() -> JOptionPane.showMessageDialog(dialog2, UIConfig.SUCCESS_ACCOUNT_CREATED, UIConfig.SUCCESS_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE));
        // Optionally verify appController.showReceptionistApp(mockReceptionist) via SwingUtilities.invokeLater
    }
    dialog2.dispose();
    screen.dispose();
}
    
    // --- Helper methods for finding components/dialogs ---
    private JDialog findDialogByTitle(String title) {
        for (Window w : Window.getWindows()) {
            if (w instanceof JDialog && w.isVisible() && title.equals(((JDialog) w).getTitle())) {
                return (JDialog) w;
            }
        }
        return null;
    }
    private <T extends JComponent> T findField(Container container, Class<T> clazz, int index) {
        int[] count = {0};
        return findFieldRecursive(container, clazz, index, count);
    }
    private <T extends JComponent> T findFieldRecursive(Container container, Class<T> clazz, int index, int[] count) {
        for (Component c : container.getComponents()) {
            if (clazz.isInstance(c)) {
                if (count[0] == index) return clazz.cast(c);
                count[0]++;
            }
            if (c instanceof Container) {
                T found = findFieldRecursive((Container) c, clazz, index, count);
                if (found != null) return found;
            }
        }
        return null;
    }
    private JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText())) return b;
            if (c instanceof Container) {
                JButton btn = findButton((Container) c, text);
                if (btn != null) return btn;
            }
        }
        return null;
    }
    private JComboBox<?> findComboBox(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComboBox) return (JComboBox<?>) c;
            if (c instanceof Container) {
                JComboBox<?> cb = findComboBox((Container) c);
                if (cb != null) return cb;
            }
        }
        return null;
    }

    // --- Helper to find components by name ---
    private Component getComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name != null && name.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component child = getComponentByName((Container) c, name);
                if (child != null) return child;
            }
        }
        return null;
    }
}