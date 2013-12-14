package coin.subscription;

import java.util.Set;

import coin.conf.CoinConfiguration;
import coin.redis.data.UserData;

public interface SubscriptionManager {

    public void init(CoinConfiguration conf);

    public Set<String> query(String key);

    public UserData get(String uid);

    public void update(String key, User user);
}
