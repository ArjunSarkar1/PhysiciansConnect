package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.AppController;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.util.TestUtils;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

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

    private void showAndWait(JFrame frame) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame.setVisible(true);
        });
        Thread.sleep(100);
    }

    // Helper: Wait for a dialog with the given title to appear (max timeoutMs)
    private static JDialog waitForDialogByTitle(String title, int timeoutMs) throws InterruptedException {
        int waited = 0;
        while (waited < timeoutMs) {
            JDialog dlg = TestUtils.findDialogByTitle(title);
            if (dlg != null) return dlg;
            Thread.sleep(20);
            waited += 20;
        }
        return null;
    }

    // Helper: Find the error label in the dialog (assumes it's the last JLabel)
    private static JLabel findErrorLabel(JDialog dialog) {
        for (Component c : ((JPanel)dialog.getContentPane().getComponent(0)).getComponents()) {
            if (c instanceof JLabel lbl && lbl.getForeground() != null && lbl.getForeground().equals(new Color(220, 53, 69))) {
                return lbl;
            }
        }
        // fallback: last JLabel
        JLabel last = null;
        for (Component c : ((JPanel)dialog.getContentPane().getComponent(0)).getComponents()) {
            if (c instanceof JLabel lbl) last = lbl;
        }
        return last;
    }

    // Helper: Find the error label in the login screen
    private static JLabel findLoginErrorLabel(LoginScreen screen) {
        for (Component c : screen.getContentPane().getComponents()) {
            if (c instanceof JPanel panel) {
                for (Component cc : panel.getComponents()) {
                    if (cc instanceof JPanel subPanel) {
                        for (Component ccc : subPanel.getComponents()) {
                            if (ccc instanceof JLabel lbl && lbl.getForeground() != null && lbl.getForeground().equals(new Color(220, 53, 69))) {
                                return lbl;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Test
    void testFieldsAndButtonsPresent() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        showAndWait(screen);

        JTextField emailField = (JTextField) TestUtils.getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) TestUtils.getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) TestUtils.getComponentByName(screen, "loginBtn");
        JButton createBtn = (JButton) TestUtils.getComponentByName(screen, "createBtn");
        assertNotNull(emailField);
        assertNotNull(passField);
        assertNotNull(loginBtn);
        assertNotNull(createBtn);
        screen.dispose();
    }


    @Test
    void testPhysicianLoginInvalidCredentials() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        showAndWait(screen);

        JTextField emailField = (JTextField) TestUtils.getComponentByName(screen, "emailField");
        JPasswordField passField = (JPasswordField) TestUtils.getComponentByName(screen, "passwordField");
        JButton loginBtn = (JButton) TestUtils.getComponentByName(screen, "loginBtn");

        assertNotNull(emailField);
        assertNotNull(passField);
        assertNotNull(loginBtn);

        emailField.setText("wrong@a.com");
        passField.setText("wrongpw");

        try (MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
                (mock, context) -> when(mock.login(anyString(), anyString())).thenThrow(new physicianconnect.logic.exceptions.InvalidCredentialException("Invalid email or password.")));
             MockedConstruction<ReceptionistController> mockReceptionistCtor = mockConstruction(ReceptionistController.class,
                (mock, context) -> when(mock.login(anyString(), anyString())).thenThrow(new physicianconnect.logic.exceptions.InvalidCredentialException("Invalid email or password.")))) {
            SwingUtilities.invokeAndWait(loginBtn::doClick);
            Thread.sleep(100);
            JLabel errorLabel = findLoginErrorLabel(screen);
            assertNotNull(errorLabel);
            assertEquals("Invalid email or password.", errorLabel.getText());
        }
        screen.dispose();
    }

    @Test
    void testRegistrationDialogValidationInvalidEmail() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        showAndWait(screen);

        JButton createBtn = (JButton) TestUtils.getComponentByName(screen, "createBtn");
        assertNotNull(createBtn);

        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = waitForDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, 2000);
        assertNotNull(dialog);

        JTextField nameField = TestUtils.findField(dialog, JTextField.class, 0);
        JTextField emailField = TestUtils.findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = TestUtils.findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = TestUtils.findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = TestUtils.findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
        JLabel errorLabel = findErrorLabel(dialog);

        assertNotNull(nameField);
        assertNotNull(emailField);
        assertNotNull(passwordField);
        assertNotNull(confirmPasswordField);
        assertNotNull(registerBtn);
        assertNotNull(errorLabel);

        nameField.setText("Test User");
        emailField.setText("bademail");
        passwordField.setText("abcdef");
        confirmPasswordField.setText("abcdef");

        SwingUtilities.invokeAndWait(registerBtn::doClick);
        assertEquals("Please check all fields and try again", errorLabel.getText());

        dialog.dispose();
        screen.dispose();
    }

    @Test
    void testRegistrationDialogValidationShortPassword() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        showAndWait(screen);

        JButton createBtn = (JButton) TestUtils.getComponentByName(screen, "createBtn");
        assertNotNull(createBtn);

        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = waitForDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, 2000);
        assertNotNull(dialog);

        JTextField nameField = TestUtils.findField(dialog, JTextField.class, 0);
        JTextField emailField = TestUtils.findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = TestUtils.findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = TestUtils.findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = TestUtils.findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
        JLabel errorLabel = findErrorLabel(dialog);

        assertNotNull(nameField);
        assertNotNull(emailField);
        assertNotNull(passwordField);
        assertNotNull(confirmPasswordField);
        assertNotNull(registerBtn);
        assertNotNull(errorLabel);

        nameField.setText("Test User");
        emailField.setText("test@a.com");
        passwordField.setText("abc");
        confirmPasswordField.setText("abc");

        SwingUtilities.invokeAndWait(registerBtn::doClick);
        assertEquals("Please check all fields and try again", errorLabel.getText());

        dialog.dispose();
        screen.dispose();
    }

    @Test
    void testRegistrationDialogValidationPasswordMismatch() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        showAndWait(screen);

        JButton createBtn = (JButton) TestUtils.getComponentByName(screen, "createBtn");
        assertNotNull(createBtn);

        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = waitForDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, 2000);
        assertNotNull(dialog);

        JTextField nameField = TestUtils.findField(dialog, JTextField.class, 0);
        JTextField emailField = TestUtils.findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = TestUtils.findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = TestUtils.findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = TestUtils.findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
        JLabel errorLabel = findErrorLabel(dialog);

        assertNotNull(nameField);
        assertNotNull(emailField);
        assertNotNull(passwordField);
        assertNotNull(confirmPasswordField);
        assertNotNull(registerBtn);
        assertNotNull(errorLabel);

        nameField.setText("Test User");
        emailField.setText("test@a.com");
        passwordField.setText("abcdef");
        confirmPasswordField.setText("ghijkl");

        SwingUtilities.invokeAndWait(registerBtn::doClick);
        assertEquals("Please check all fields and try again", errorLabel.getText());

        dialog.dispose();
        screen.dispose();
    }

    @Test
    void testRegistrationDialogValidationDuplicateEmail() throws Exception {
        LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
        showAndWait(screen);

        JButton createBtn = (JButton) TestUtils.getComponentByName(screen, "createBtn");
        assertNotNull(createBtn);

        SwingUtilities.invokeLater(createBtn::doClick);
        JDialog dialog = waitForDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, 2000);
        assertNotNull(dialog);

        JTextField nameField = TestUtils.findField(dialog, JTextField.class, 0);
        JTextField emailField = TestUtils.findField(dialog, JTextField.class, 1);
        JPasswordField passwordField = TestUtils.findField(dialog, JPasswordField.class, 0);
        JPasswordField confirmPasswordField = TestUtils.findField(dialog, JPasswordField.class, 1);
        JButton registerBtn = TestUtils.findButton(dialog, UIConfig.REGISTER_BUTTON_TEXT);
        JLabel errorLabel = findErrorLabel(dialog);

        assertNotNull(nameField);
        assertNotNull(emailField);
        assertNotNull(passwordField);
        assertNotNull(confirmPasswordField);
        assertNotNull(registerBtn);
        assertNotNull(errorLabel);

        nameField.setText("Test User");
        emailField.setText("test@a.com");
        passwordField.setText("abcdef");
        confirmPasswordField.setText("abcdef");

        when(physicianManager.getPhysicianByEmail("test@a.com")).thenReturn(mock(Physician.class));

        SwingUtilities.invokeAndWait(registerBtn::doClick);
        assertEquals("Please check all fields and try again", errorLabel.getText());

        dialog.dispose();
        screen.dispose();
    }

// @Test
// void testRegistrationDialogSuccessPhysicianAndReceptionist() throws Exception {
//     LoginScreen screen = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);
//     showAndWait(screen);

//     JButton createBtn = (JButton) TestUtils.getComponentByName(screen, "createBtn");
//     assertNotNull(createBtn);

//     // ---- Physician registration ----
//     SwingUtilities.invokeLater(createBtn::doClick);
//     JDialog dialog1 = waitForDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, 2000);
//     assertNotNull(dialog1);

//     JComboBox<?> userTypeCombo1 = TestUtils.findComboBox(dialog1);
//     JTextField nameField1 = TestUtils.findField(dialog1, JTextField.class, 0);
//     JTextField emailField1 = TestUtils.findField(dialog1, JTextField.class, 1);
//     JPasswordField passwordField1 = TestUtils.findField(dialog1, JPasswordField.class, 0);
//     JPasswordField confirmPasswordField1 = TestUtils.findField(dialog1, JPasswordField.class, 1);
//     JButton registerBtn1 = TestUtils.findButton(dialog1, UIConfig.REGISTER_BUTTON_TEXT);

//     assertNotNull(userTypeCombo1);
//     assertNotNull(nameField1);
//     assertNotNull(emailField1);
//     assertNotNull(passwordField1);
//     assertNotNull(confirmPasswordField1);
//     assertNotNull(registerBtn1);

//     userTypeCombo1.setSelectedItem("Physician");
//     nameField1.setText("Doc");
//     emailField1.setText("doc@a.com");
//     passwordField1.setText("abcdef");
//     confirmPasswordField1.setText("abcdef");

//     when(physicianManager.getPhysicianByEmail("doc@a.com")).thenReturn(null);
//     when(receptionistManager.getReceptionistByEmail("doc@a.com")).thenReturn(null);

//     Physician mockPhysician = mock(Physician.class);
//     try (
//         MockedConstruction<PhysicianController> mockPhysicianCtor = mockConstruction(PhysicianController.class,
//             (mock, context) -> when(mock.register(anyString(), anyString(), anyString(), anyString())).thenReturn(mockPhysician));
//         MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)
//     ) {
//         TestUtils.pressOkOnAnyDialogAsync();
//         SwingUtilities.invokeAndWait(registerBtn1::doClick);
//         SwingUtilities.invokeAndWait(() -> {}); // flush EDT
//         mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), eq(UIConfig.SUCCESS_ACCOUNT_CREATED), eq(UIConfig.SUCCESS_DIALOG_TITLE), eq(JOptionPane.INFORMATION_MESSAGE)));
//         verify(appController, timeout(1000)).showPhysicianApp(mockPhysician);
//     }
//     dialog1.dispose();

//     // ---- Receptionist registration ----
//     SwingUtilities.invokeLater(createBtn::doClick);
//     JDialog dialog2 = waitForDialogByTitle(UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, 2000);
//     assertNotNull(dialog2);

//     JComboBox<?> userTypeCombo2 = TestUtils.findComboBox(dialog2);
//     JTextField nameField2 = TestUtils.findField(dialog2, JTextField.class, 0);
//     JTextField emailField2 = TestUtils.findField(dialog2, JTextField.class, 1);
//     JPasswordField passwordField2 = TestUtils.findField(dialog2, JPasswordField.class, 0);
//     JPasswordField confirmPasswordField2 = TestUtils.findField(dialog2, JPasswordField.class, 1);
//     JButton registerBtn2 = TestUtils.findButton(dialog2, UIConfig.REGISTER_BUTTON_TEXT);

//     assertNotNull(userTypeCombo2);
//     assertNotNull(nameField2);
//     assertNotNull(emailField2);
//     assertNotNull(passwordField2);
//     assertNotNull(confirmPasswordField2);
//     assertNotNull(registerBtn2);

//     userTypeCombo2.setSelectedItem("Receptionist");
//     nameField2.setText("Rec");
//     emailField2.setText("rec@a.com");
//     passwordField2.setText("abcdef");
//     confirmPasswordField2.setText("abcdef");

//     when(physicianManager.getPhysicianByEmail("rec@a.com")).thenReturn(null);
//     when(receptionistManager.getReceptionistByEmail("rec@a.com")).thenReturn(null);

//     Receptionist mockReceptionist = mock(Receptionist.class);
//     try (
//         MockedConstruction<ReceptionistController> mockReceptionistCtor = mockConstruction(ReceptionistController.class,
//             (mock, context) -> when(mock.register(anyString(), anyString(), anyString(), anyString())).thenReturn(mockReceptionist));
//         MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)
//     ) {
//         //TestUtils.pressOkOnAnyDialogAsync();
//         SwingUtilities.invokeAndWait(registerBtn2::doClick);
//         SwingUtilities.invokeAndWait(() -> {}); // flush EDT
//         mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), eq(UIConfig.SUCCESS_ACCOUNT_CREATED), eq(UIConfig.SUCCESS_DIALOG_TITLE), eq(JOptionPane.INFORMATION_MESSAGE)));
//         verify(appController, timeout(1000)).showReceptionistApp(mockReceptionist);
//     }
//     dialog2.dispose();
//     screen.dispose();
// }
}