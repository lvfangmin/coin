package coin.subscription.redis;

import java.util.List;

import java.util.Set;

import redis.clients.jedis.Jedis;

import coin.conf.CoinConfiguration;
import coin.redis.Contant;
import coin.redis.RedisConf;
import coin.redis.RedisInstance;
import coin.subscription.SubscriptionManager;
import coin.subscription.User;
import coin.redis.data.UserData;

public class RedisSubscriptionManager implements SubscriptionManager {
    private CoinConfiguration conf;
    private RedisInstance redis;

    // Using our own redis client
    private Jedis jedis;

    @Override
    public void init(CoinConfiguration conf) {
        this.conf = conf;
        // TODO: Read host from conf
        //RedisConf redisConf = new RedisConf("localhost");
        //RedisInstance.init(redisConf);
        //redis = RedisInstance.getInstance();
        jedis = new Jedis("localhost");
    }

    public void start() {
    }

    @Override
    public Set<String> query(String key) {
        return jedis.smembers(key);
    }

    @Override
    public String get(String uid) {
        return jedis.get("uid:" + uid + ":username");
    }

    @Override
    public void update(String key, User user) {

    }

    public Jedis getJedis() {
        return jedis;
    }

}
