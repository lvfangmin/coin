package coin.notify;

import org.junit.Test;

public class JavaMailSenderTest {

    @Test
    public void test() {
        MailSender ms = new MailSender();
        ms.send(null, null);
    }

}
