package coin.notify;

import org.junit.Test;

public class JavaMailSenderTest {

    @Test
    public void test() {
        MailSender ms = new MailSender();
        ms.send("407362048@qq.com", "Test OK!");
    }

}
