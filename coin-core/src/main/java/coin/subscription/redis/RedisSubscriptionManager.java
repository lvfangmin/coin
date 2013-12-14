package coin.subscription.redis;

import java.util.List;

import java.util.Set;

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

    @Override
    public void init(CoinConfiguration conf) {
        this.conf = conf;
        // TODO: Read host from conf
        RedisConf redisConf = new RedisConf("localhost");
        RedisInstance.init(redisConf);
        redis = RedisInstance.getInstance();
    }

    public void start() {
    }

    @Override
    public Set<String> query(String key) {
        return redis.getUids(key);
    }

    @Override
    public UserData get(String uid) {
        return redis.getUser(uid);
    }

    @Override
    public void update(String key, User user) {

    }

}
