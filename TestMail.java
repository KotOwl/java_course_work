import java.util.Properties;

public class TestMail {
    public static void main(String[] args) throws Exception {
        String to = "stas.black31@gmail.com";
        String user = "stas.black31@gmail.com";
        String pass = "xtxsnxbtgznglnav";
        
        System.out.println("Testing login for " + user);
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        
        Class<?> sessionClass = Class.forName("javax.mail.Session");
        Class<?> authClass = Class.forName("javax.mail.Authenticator");
        Object session = sessionClass.getMethod("getInstance", Properties.class, authClass).invoke(null, props, null);
        
        Object transport = sessionClass.getMethod("getTransport", String.class).invoke(session, "smtp");
        Class<?> transportClass = Class.forName("javax.mail.Transport");
        
        try {
            transportClass.getMethod("connect", String.class, int.class, String.class, String.class)
                .invoke(transport, "smtp.gmail.com", 587, user, pass);
            System.out.println("SUCCESSFULLY CONNECTED!");
            transportClass.getMethod("close").invoke(transport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
