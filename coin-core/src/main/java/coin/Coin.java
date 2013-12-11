package coin;

import java.io.File;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coin.conf.CoinConfiguration;

public class Coin {

    static Logger logger = LoggerFactory.getLogger(Coin.class);

    CoinConfiguration conf;

    static final int INVALID_CONF_FILE = 1;
    static final int MALFORMED_CONF_FILE = 2;

    public Coin(CoinConfiguration conf) {
        this.conf = conf;

        conf.validate();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: Init Notify layer
                // TODO: Init Persistence layer
                // TODO: Init Rule engine layer
                // TODO: Init Data pre processing layer
                // TODO: Init Crawler layer
                // TODO: Init jmx
            }
        }).start();
    }

    public static void main(String[] args) {
        logger.info("Attempting to start Coin");
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
