package coin.notify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.eventbus.EventBus;

public class NotifyClient{
    private ExecutorService executor = null;
    private LinkedBlockingQueue<Runnable> workerPool = null;
    private boolean closed = true;
    private MailSender mailSender = null;
    private SmsSender smsSender = null;
    
    
    
    public NotifyClient(EventBus eb){
        
    }
}
