package coin.notify.sender;

import java.util.ArrayList;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

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
    private List<Session> sessions = new ArrayList<Session>();
    private Session session = null;
    private AtomicLong rr = new AtomicLong(0);

    public MailSender() {
        init();
    }

    public MailSender(AuthType authType) {
        this.authType = authType;
        init();
    }

    private Session initSession(final String username) {
        Properties props = new Properties();
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

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        return session;
    }

    private void init() {
        for (int i = 1; i < 4; i++) {
            sessions.add(initSession("truecoins" + i + "@gmail.com"));
        }
    }

    @Override
    public boolean send(String mailbox, String content) {
        try {
            int id = (int)(rr.getAndIncrement() % sessions.size());
            Message message = new MimeMessage(sessions.get(id));
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
