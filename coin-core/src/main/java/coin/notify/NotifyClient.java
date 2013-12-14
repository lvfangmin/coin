package coin.notify;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coin.conf.CoinConfiguration;

import com.google.common.eventbus.EventBus;

public class NotifyClient{
    private static final Logger logger = LoggerFactory.getLogger(NotifyClient.class);
    
    private static final int CORE_SIZE = 40;
    private static final int MAX_SIZE = 40;
    private static final int QUEUE_SIZE = 40;
    
    private ExecutorService executor = null;
    private LinkedBlockingQueue<Runnable> workerPool = null;
    private EventBus eventBus = null;
    private CoinConfiguration coinConfig = null;
    private boolean closed = true;
    
    private Sender mailSender = null;
    
    public NotifyClient(EventBus eventBus, CoinConfiguration coinConfig){
        this.eventBus = eventBus;
        this.coinConfig = coinConfig;
        init();
    }
    
    public boolean send(String user, String content)
    
    private void init(){
        initExecutor();
        initListener();
        initSender();
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
        LOG.info("MetaStorage shutdown successfully");
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
        private Notifycation notifycation;

        public QueryTask(Sender sender, Notification notification) {
            this.userId = userId;
            this.topic = topic;
            this.callback = callback;
        }

        @Override
        public void run() {
            long beforeSonoraHit = 0L;
            try {
                beforeSonoraHit = System.currentTimeMillis();
                List<Target> targets = storageStrategy.query(this.userId, topic);
                long afterSonoraHitSuccess = System.currentTimeMillis();
                metrics.setQueryRequestProcessLatency(afterSonoraHitSuccess - beforeSonoraHit);
                LOG.info("out Sync query latency:" + (afterSonoraHitSuccess - beforeSonoraHit));
                LOG.info("Metadata is successfully retrieved and constructed for key(" + userId + "), invoke listener.");
                this.callback.onComplete(targets);
            } catch (MetaStorageException ex) {
                long afterSonoraHitSuccess = System.currentTimeMillis();
                metrics.setQueryRequestProcessLatency(afterSonoraHitSuccess - beforeSonoraHit);
                LOG.error("Metadata failed to retrieve and construct for key(" + userId + "), invoke listener.");
                LOG.error("out Sync query latency:" + (afterSonoraHitSuccess - beforeSonoraHit));
                this.callback.onException((MetaStorageException) ex);
            }
        }
    }

}
