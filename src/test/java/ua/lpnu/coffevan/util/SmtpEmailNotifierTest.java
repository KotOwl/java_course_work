package ua.lpnu.coffevan.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmtpEmailNotifier.
 *
 * <p>Because SMTP credentials are intentionally not configured in the
 * test environment, we can only verify the no-op / graceful-failure paths.
 * Real email delivery is integration-level and is not tested here.
 */
class SmtpEmailNotifierTest {

    /**
     * When SMTP password is empty (default test environment),
     * sendCriticalAlert must silently do nothing (no exception thrown).
     */
    @Test
    void sendCriticalAlert_whenNoPassword_doesNotThrow() {
        // Clear any accidentally set system property
        System.clearProperty("mail.smtp.password");
        assertDoesNotThrow(() ->
                SmtpEmailNotifier.sendCriticalAlert("Test subject", "Test body"));
    }

    /**
     * Verify that the method handles a null subject gracefully.
     */
    @Test
    void sendCriticalAlert_withNullSubject_doesNotThrow() {
        System.clearProperty("mail.smtp.password");
        assertDoesNotThrow(() ->
                SmtpEmailNotifier.sendCriticalAlert(null, "body"));
    }

    /**
     * Verify that the method handles a null body gracefully.
     */
    @Test
    void sendCriticalAlert_withNullBody_doesNotThrow() {
        System.clearProperty("mail.smtp.password");
        assertDoesNotThrow(() ->
                SmtpEmailNotifier.sendCriticalAlert("subject", null));
    }

    /**
     * When an invalid SMTP password is set, the method should catch
     * the underlying transport/reflection exception and not propagate it.
     */
    @Test
    void sendCriticalAlert_withInvalidCredentials_doesNotThrow() {
        System.setProperty("mail.smtp.password", "invalid_password");
        System.setProperty("mail.smtp.host",     "invalid.host.local");
        System.setProperty("mail.smtp.port",     "587");
        System.setProperty("mail.smtp.user",     "test@example.com");
        System.setProperty("mail.to",            "admin@example.com");
        try {
            assertDoesNotThrow(() ->
                    SmtpEmailNotifier.sendCriticalAlert("Critical", "Something failed"));
        } finally {
            // Restore clean state so other tests aren't affected
            System.clearProperty("mail.smtp.password");
            System.clearProperty("mail.smtp.host");
            System.clearProperty("mail.smtp.port");
            System.clearProperty("mail.smtp.user");
            System.clearProperty("mail.to");
        }
    }

    /**
     * SmtpEmailNotifier is a pure utility class — it must not be
     * instantiatable via reflection (private constructor).
     */
    @Test
    void smtpEmailNotifier_cannotBeInstantiated_hasPrivateConstructor()
            throws NoSuchMethodException {
        var constructor = SmtpEmailNotifier.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
                "Constructor should be private");
    }
}
