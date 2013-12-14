package coin.notify;

import org.apache.commons.mail.EmailException;

public interface MailSender {
    public boolean send(String mailbox,String content);

}
