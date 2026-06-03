import ua.lpnu.coffevan.util.SmtpEmailNotifier;

public class TestSendMail {
    public static void main(String[] args) {
        System.out.println("TEST SCRIPT: Calling sendEmail...");
        boolean res = SmtpEmailNotifier.sendEmail("stas.black31@gmail.com", "Test Subject", "Test Body");
        System.out.println("Result: " + res);
        System.out.println("Last error: " + SmtpEmailNotifier.getLastErrorMessage());
    }
}
