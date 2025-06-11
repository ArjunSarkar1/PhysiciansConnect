package physicianconnect.presentation;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.*;
import physicianconnect.AppController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import static org.mockito.Mockito.*;

public class LoginScreenAcceptanceTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
    private PhysicianManager physicianManager;
    private ReceptionistManager receptionistManager;
    private AppointmentManager appointmentManager;
    private AppController appController;

    @Override
    protected void onSetUp() {
        FailOnThreadViolationRepaintManager.install();
        physicianManager = mock(PhysicianManager.class);
        receptionistManager = mock(ReceptionistManager.class);
        appointmentManager = mock(AppointmentManager.class);
        appController = mock(AppController.class);

        LoginScreen frame = new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController);


        frame.emailField.setName("emailField");
        frame.passField.setName("passField");
        frame.loginBtn.setName("loginBtn");
        frame.createBtn.setName("createBtn");

        window = new FrameFixture(robot(), frame);
        window.show();
    }

    @Test
    @GUITest
    public void successfulPhysicianLogin_showsPhysicianApp() {
        Physician fakePhysician = mock(Physician.class);
        when(physicianManager.getPhysicianByEmail("doc@clinic.com")).thenReturn(fakePhysician);
        when(fakePhysician.getPassword()).thenReturn("strongpass");

        window.textBox("emailField").setText("doc@clinic.com");
        window.textBox("passField").setText("strongpass");
        window.button("loginBtn").click();

        verify(appController, timeout(1500)).showPhysicianApp(fakePhysician);
    }

    @Test
    @GUITest
    public void failedLogin_showsErrorLabel() {
        when(physicianManager.getPhysicianByEmail("fail@fail.com")).thenReturn(null);
        when(receptionistManager.getReceptionistByEmail("fail@fail.com")).thenReturn(null);

        window.textBox("emailField").setText("fail@fail.com");
        window.textBox("passField").setText("nopass");
        window.button("loginBtn").click();

        window.label("errorLabel").requireVisible();
        window.label("errorLabel").requireText("Invalid credentials.");
    }

    @Test
    @GUITest
    public void createAccountDialog_opensOnButtonClick() {
        window.button("createBtn").click();
        // Have to wait a little bit
        window.dialog().requireVisible();
        org.junit.jupiter.api.Assertions.assertEquals("Create Account", window.dialog().target().getTitle());
        window.dialog().close();
    }

    @Override
    protected void onTearDown() {
        window.cleanUp();
    }
}