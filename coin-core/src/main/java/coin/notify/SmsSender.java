package coin.notify;

public interface SmsSender {
    public boolean send(String number,String content);
}
