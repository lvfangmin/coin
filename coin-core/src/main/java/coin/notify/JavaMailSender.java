package coin.notify;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaMailSender implements MailSender {
    
    private static final Logger logger = LoggerFactory.getLogger(JavaMailSender.class);

    private static final String DEFAULT_USER_NAME = "truecoins@gmail.com";
    private static final String DEFAULT_PASSWORD = "passwordcoin";
    
    private static final String DEFAULT_SSL_SMTP_PORT = "587";
    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final AuthType DEFAULT_AUTH_TYPE = AuthType.TLS;
    
    private String username = null;
    private String password = null;
    private String smtpPort = null;
    private String smtpHost = null;
    private AuthType authType = null;
    
    
    public JavaMailSender(){
        this.username = DEFAULT_USER_NAME;
        this.password = DEFAULT_PASSWORD;
        this.smtpHost = DEFAULT_SMTP_HOST;
        this.smtpPort = DEFAULT_SSL_SMTP_PORT;
        this.authType = DEFAULT_AUTH_TYPE;
    }
    
    public JavaMailSender(AuthType authType){
        this.username = DEFAULT_USER_NAME;
        this.password = DEFAULT_PASSWORD;
        this.smtpHost = DEFAULT_SMTP_HOST;
        this.smtpPort = DEFAULT_SSL_SMTP_PORT;
        this.authType = authType;
    }
    
    
    @Override
    public boolean send(String mailbox, String content) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
 
        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
 
        try {
 
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("obama@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("124083308@qq.com"));
            message.setSubject("Testing Subject");
            message.setText("Dear Mail Crawler,"
                + "\n\n No spam to my email, please!");
 
            Transport.send(message);
 
            System.out.println("Done");
 
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        
        return true;
    }
    
    public static enum AuthType{
        TLS,SSL
    }
}
