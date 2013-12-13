package coin;

import java.io.File;

import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import coin.conf.CoinConfiguration;
import coin.data.DataPreProcessing;

public class Coin {

    static Logger logger = LoggerFactory.getLogger(Coin.class);

    public static final int INVALID_CONF_FILE = 1;
    public static final int MALFORMED_CONF_FILE = 2;

    CoinConfiguration conf;
    EventBus dataEventBus;
    DataPreProcessing dpp;
    MBeanServer mBeanServer;

    final ThreadGroup tg;

    public Coin(CoinConfiguration conf) {
        this.conf = conf;
        // Define the custom thread group to handler the uncaught exception
        this.tg = new ThreadGroup("coin") {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception in thread {}", t.getName(), e);
                Runtime.getRuntime().exit(1);
            }
        };

        conf.validate();
    }

    public void start() {
        // using thread in case the init takes long time to finish
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataEventBus = new EventBus("Data Event Bus");
                // TODO: Init Notify layer
                // TODO: Init Persistence layer
                // TODO: Init Rule engine layer
                // TODO: Init Data pre processing layer
                dpp = new DataPreProcessing(conf).register(dataEventBus);
                // TODO: Init Crawler layer
                // TODO: Init jmx
                try {
                    mBeanServer = ManagementFactory.getPlatformMBeanServer();
                } catch (Exception e) {
                    mBeanServer = MBeanServerFactory.createMBeanServer();
                }

                // Graceful shutdown
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        Coin.this.stop();
                    }
                });
                logger.info("Successfully started Coin server.");
            }
        }).start();
    }

    public void stop() {
        logger.info("Attempting to shutdown Coin server...");
        // TODO: Stop jmx
        // TODO: Stop Crawler layer
        // TODO: Stop Data pre processing layer
        // TODO: Stop Rule engine layer
        // TODO: Stop Persistence layer
        // TODO: Stop Notify layer
        logger.info("Successfully shutdown Coin server.");
    }

    public static void main(String[] args) {
        logger.info("Attempting to start Coin server...");
        CoinConfiguration conf = new CoinConfiguration();

        if (args.length > 0) {
            String confFile = args[0];
            try {
                conf.loadConf(new File(confFile).toURI().toURL());
            } catch (MalformedURLException e) {
                logger.error("Could not open configuration file: {}", confFile);
                System.exit(INVALID_CONF_FILE);
            }
            logger.info("Using configuration file {}", confFile);
        }

        new Coin(conf).start();
    }
}
