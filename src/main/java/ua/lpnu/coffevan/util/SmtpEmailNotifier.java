package ua.lpnu.coffevan.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Utility for sending email notifications on critical errors.
 * Configure SMTP settings before use.
 *
 * <p>This class is intentionally decoupled from the Log4j2 SMTP appender to
 * avoid module-system conflicts. Call {@link #sendCriticalAlert(String, String)}
 * from any fatal-level catch block when email notification is needed.
 */
public class SmtpEmailNotifier {

    private static final Logger logger = LogManager.getLogger(SmtpEmailNotifier.class);

    static {
        loadDotEnv();
    }

    private static void loadDotEnv() {
        java.io.File envFile = new java.io.File(".env");
        if (envFile.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim();
                        String val = line.substring(eqIdx + 1).trim();
                        if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                            val = val.substring(1, val.length() - 1);
                        } else if (val.startsWith("'") && val.endsWith("'") && val.length() >= 2) {
                            val = val.substring(1, val.length() - 1);
                        }
                        switch (key) {
                            case "MAIL_SMTP_HOST" -> System.setProperty("mail.smtp.host", val);
                            case "MAIL_SMTP_PORT" -> System.setProperty("mail.smtp.port", val);
                            case "MAIL_SMTP_USER" -> System.setProperty("mail.smtp.user", val);
                            case "MAIL_SMTP_PASSWORD" -> System.setProperty("mail.smtp.password", val);
                            case "MAIL_TO" -> System.setProperty("mail.to", val);
                        }
                    }
                }
                logger.info(".env file loaded successfully");
            } catch (Exception e) {
                logger.warn("Could not load .env file", e);
            }
        }
    }

    private static String getSmtpHost() { return System.getProperty("mail.smtp.host", "smtp.gmail.com"); }
    private static String getSmtpPort() { return System.getProperty("mail.smtp.port", "587"); }
    private static String getSmtpUser() { return System.getProperty("mail.smtp.user", "coffeevan@example.com"); }
    private static String getSmtpPass() { return System.getProperty("mail.smtp.password", ""); }
    private static String getMailTo() { return System.getProperty("mail.to", "admin@example.com"); }

    private SmtpEmailNotifier() {
    }

    /**
     * Sends a critical alert email.
     * No-op if SMTP credentials are not configured.
     *
     * @param subject email subject
     * @param body    email body
     */
    public static void sendCriticalAlert(String subject, String body) {
        if (getSmtpPass().isEmpty()) {
            logger.warn("SMTP password not configured — skipping email alert for: {}", subject);
            return;
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", getSmtpHost());
            props.put("mail.smtp.port", getSmtpPort());

            Class<?> sessionClass = Class.forName("javax.mail.Session");
            Class<?> authClass = Class.forName("javax.mail.Authenticator");
            Object session = sessionClass.getMethod("getInstance", Properties.class, authClass)
                    .invoke(null, props, null);

            Class<?> messageClass = Class.forName("javax.mail.internet.MimeMessage");
            Object message = messageClass.getConstructor(sessionClass).newInstance(session);

            Class<?> interAddressClass = Class.forName("javax.mail.internet.InternetAddress");
            Object fromAddress = interAddressClass.getConstructor(String.class).newInstance(getSmtpUser());
            Object toAddress = interAddressClass.getConstructor(String.class).newInstance(getMailTo());

            messageClass.getMethod("setFrom", Class.forName("javax.mail.Address")).invoke(message, fromAddress);
            messageClass.getMethod("setRecipient",
                    Class.forName("javax.mail.Message$RecipientType"),
                    Class.forName("javax.mail.Address")).invoke(message,
                    Class.forName("javax.mail.Message$RecipientType")
                            .getField("TO").get(null),
                    toAddress);
            messageClass.getMethod("setSubject", String.class).invoke(message, "[CoffeeVan] " + subject);
            messageClass.getMethod("setText", String.class).invoke(message, body);

            Class<?> transportClass = Class.forName("javax.mail.Transport");
            transportClass.getMethod("send", Class.forName("javax.mail.Message")).invoke(null, message);

            logger.info("Critical alert email sent to {}", getMailTo());
        } catch (Exception e) {
            logger.error("Failed to send critical alert email", e);
        }
    }
}
