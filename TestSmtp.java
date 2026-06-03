import ua.lpnu.coffevan.util.SmtpEmailNotifier;

public class TestSmtp {
    public static void main(String[] args) {
        System.out.println("Testing SmtpEmailNotifier.sendEmail...");
        boolean result = SmtpEmailNotifier.sendEmail("stas.black31@gmail.com", "Test Subject", "Test Body");
        System.out.println("Result: " + result);
        if (!result) {
            System.out.println("Error: " + SmtpEmailNotifier.getLastErrorMessage());
        }
    }
}
