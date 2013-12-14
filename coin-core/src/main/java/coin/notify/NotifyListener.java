package coin.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class NotifyListener {
    private static final Logger logger = LoggerFactory.getLogger(NotifyListener.class);
    
    private NotifyClient notifyClient;
    public NotifyListener(NotifyClient notifyClient){
        this.notifyClient = notifyClient;
    }
    
    @Subscribe
    public void receiveNotifyEvent(Notification notification){
        logger.info("Receive a notifycation" );
        notifyClient.send(notification);
    }
}
