package coin.notify;

import org.junit.Test;

public class JavaMailSenderTest {

    @Test
    public void test() {
        JavaMailSender ms = new JavaMailSender();
        ms.send(null, null);
    }

}
