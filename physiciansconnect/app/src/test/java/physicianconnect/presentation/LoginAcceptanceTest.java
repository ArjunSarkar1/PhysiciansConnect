package physicianconnect.presentation;

import static org.mockito.Mockito.*;
import static org.assertj.swing.finder.JOptionPaneFinder.findOptionPane;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Physician;

@Tag("ui")
public class LoginAcceptanceTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private PhysicianManager mockPhysicianManager;
    private AppointmentManager mockAppointmentManager;

    @Override
    protected void onSetUp() {
        mockPhysicianManager = mock(PhysicianManager.class);
        mockAppointmentManager = mock(AppointmentManager.class);

        LoginScreen login = GuiActionRunner
                .execute(() -> new LoginScreen(mockPhysicianManager, mockAppointmentManager));

        window = new FrameFixture(robot(), login);
        window.show();
    }

    @Test
    public void login_withValidCredentials_shouldLaunchDashboard() {
        // Arrange
        when(mockPhysicianManager.login("test@email.com", "test123"))
                .thenReturn(new Physician("123", "Dr. Test", "test@email.com", "test123"));

        // Act
        window.textBox("emailField").setText("test@email.com");
        window.textBox("passwordField").setText("test123");
        window.button("loginBtn").click();

        // Assert: verify login was attempted
        verify(mockPhysicianManager).login("test@email.com", "test123");

        // Optional: check that the login window is no longer active
        assertFalse(window.target().isDisplayable(), "Login window should be disposed");
    }

    @Test
    public void login_withInvalidCredentials_shouldShowErrorMessage() {
        // Arrange
        when(mockPhysicianManager.login("wrong@email.com", "badpass"))
                .thenReturn(null);

        // Act
        window.textBox("emailField").setText("wrong@email.com");
        window.textBox("passwordField").setText("badpass");
        window.button("loginBtn").click();

        // Assert
        findOptionPane().using(robot())
                .requireErrorMessage()
                .requireMessage("Invalid credentials.");
    }

}
