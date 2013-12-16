package coin;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coin.redis.RedisConf;
import coin.redis.RedisInstance;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/coin/";
    private final HttpServer server;
    private final ResourceConfig rc;
    private final RedisConf redisConf;
    private volatile boolean stopped = false;

    public Application() {
        // create a resource config that scans for JAX-RS resources and
        // providers
        rc = new ResourceConfig().packages("coin.handler");
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        redisConf = new RedisConf("localhost");
    }

    public void start() throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                RedisInstance.init(redisConf);
                RedisInstance.getInstance();
                try {
                    server.start();
                } catch (IOException e) {
                    logger.error("Error while start http server, {}", e);
                }
                // Graceful shutdown
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        Application.this.stop();
                    }
                });
                while (!stopped) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("InterruptedException when sleep");
                    }
                }

            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public void stop() {
        logger.info("Attempt to shutdown coin service...");
        stopped = true;
        server.stop();
        logger.info("Successfully shutdown coin service.");
    }

    /**
     * Main method.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        logger.info("Attemp to start coin service...");
        Application app = new Application();
        app.start();

        logger.info("Coin Service started, WADL available at {}application.wadl", BASE_URI);
    }
}
