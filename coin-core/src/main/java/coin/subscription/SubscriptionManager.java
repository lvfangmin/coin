package coin.subscription;

import java.util.List;

public interface SubscriptionManager {

    public List<User> query(String key);

    public void update(String key, User user);
}
