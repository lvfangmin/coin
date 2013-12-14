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

public class MailSender implements Sender {

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private static final String DEFAULT_USER_NAME = "truecoins@gmail.com";
    private static final String DEFAULT_PASSWORD = "passwordcoin";

    private static final AuthType DEFAULT_AUTH_TYPE = AuthType.TLS;

    private String username = DEFAULT_USER_NAME;
    private String password = DEFAULT_PASSWORD;
    private AuthType authType = DEFAULT_AUTH_TYPE;
    private Session session = null;

    private Properties props = new Properties();

    public MailSender() {
        init();
    }

    public MailSender(AuthType authType) {
        this.authType = authType;
        init();
    }

    private void init() {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");

        if (this.authType == AuthType.SSL) {
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.port", "465");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");
        }

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    @Override
    public boolean send(String mailbox, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailbox));
            message.setSubject("TrusCoins notify");
            message.setText("Dear, \n\n " + content);
            Transport.send(message);
            logger.info("Successfully sent mail to {}.", mailbox);

        } catch (MessagingException e) {
            logger.error("Failed to send mail to " + mailbox);
            throw new RuntimeException(e);
        }

        return true;
    }

    public static enum AuthType {
        TLS, SSL
    }
}
