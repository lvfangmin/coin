package coin.notify;

import org.apache.commons.mail.EmailException;

public interface Sender {
    public boolean send(String mailbox,String content);

}
