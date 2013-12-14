package coin.subscription.redis;

import java.util.List;

import redis.clients.jedis.Jedis;

import coin.conf.CoinConfiguration;
import coin.subscription.SubscriptionManager;
import coin.subscription.User;

public class RedisSubscriptionManager implements SubscriptionManager {
    private CoinConfiguration conf;
    private Jedis jedis;

    public void init(CoinConfiguration conf) {
        this.conf = conf;
        // TODO: Read host from conf
        this.jedis = new Jedis("localhost");
    }

    public void start() {
    }

    @Override
    public List<User> query(String key) {
        return null;
    }

    @Override
    public void update(String key, User user) {

    }

}
