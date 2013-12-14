package coin.notify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coin.conf.CoinConfiguration;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NotificationListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
    
    private static final int CORE_SIZE = 40;
    private static final int MAX_SIZE = 40;
    private static final int QUEUE_SIZE = 40;
    
    private ExecutorService executor = null;
    private LinkedBlockingQueue<Runnable> workerPool = null;
    private EventBus eventBus = null;
    private CoinConfiguration coinConfig = null;
    private boolean closed = true;
    
    private Sender mailSender = null;
    
    public NotificationListener(CoinConfiguration coinConfig, EventBus eventBus){
        this.eventBus = eventBus;
        this.coinConfig = coinConfig;
        init();
    }
    
    private void init(){
        initExecutor();
        initListener();
        initSender();
    }
    
    
    private void initSender() {
        mailSender = new MailSender();
    }

    private void initListener() {
        this.eventBus.register(this);
    }

    public boolean shutdown() {
        if (this.workerPool != null) {
            this.executor.shutdown();
            try {
                this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("NotifyClient shutdown interrupted");
                e.printStackTrace();
                return false;
            }
        }
        closed = true;
        logger.info("NotifyClient shutdown successfully");
        return true;
    }
    
    
    private void initExecutor() {
        // Init thread pool
        //int coreSize = coinConfig.getCoreSize();
        //int maxSize = coinConfig.getMaxSize();
        //int queueSize = coinConfig.getQueueSize();
        int coreSize = this.CORE_SIZE;
        int maxSize = this.MAX_SIZE;
        int queueSize = this.QUEUE_SIZE;

        this.workerPool = new LinkedBlockingQueue<Runnable>(queueSize);
        this.executor = new ThreadPoolExecutor(coreSize, maxSize, 30, TimeUnit.SECONDS, this.workerPool,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        logger.trace("Created thread");
                        return thread;
                    }
                }, new ThreadPoolExecutor.CallerRunsPolicy());

        closed = false;
    }
    
    private class NotifyTask implements Runnable {

        private Sender sender;
        private Notification notification;

        public NotifyTask(Sender sender, Notification notification) {
            this.sender = sender;
            this.notification = notification;
        }

        @Override
        public void run() {
            try {
                sender.send(notification.getDestination(), notification.getContent());
            } catch (Exception ex) {
                logger.error("Failed to send notification to {}", notification.getDestination());
            }
        }
    }

    @Subscribe
    public void send(Notification notification) {
        if(!this.closed&&notification.getDestinationType().equals(Notification.DestinationType.MAIL)){
            this.executor.execute(new NotifyTask(this.mailSender,notification));
        }
        else if(this.closed){
            logger.error("NotifyClient has been closed!");
        }
        else{
            logger.error("Unsupported channel!");
        }
    }
}
