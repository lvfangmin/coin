package coin.subscription;

import java.util.Set;
import redis.clients.jedis.Jedis;

import coin.conf.CoinConfiguration;
import coin.redis.data.UserData;

public interface SubscriptionManager {

    public void init(CoinConfiguration conf);

    public Set<String> query(String key);

    public String get(String uid);

    public void update(String key, User user);

    public Jedis getJedis();
}
