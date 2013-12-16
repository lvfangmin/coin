package coin.data;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import coin.conf.CoinConfiguration;
import coin.notify.Notification;
import coin.notify.Notification.DestinationType;
import coin.rule.PriceRule;
import coin.subscription.SubscriptionManager;
import coin.redis.data.UserData;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPreProcessing {
    private static final Logger logger = LoggerFactory.getLogger(DataPreProcessing.class);

    private final CoinConfiguration conf;
    private final EventBus notifyEventBus;
    private final SubscriptionManager sm;
    private final Set<PriceRule> rules;

    public DataPreProcessing(@Nonnull CoinConfiguration conf, @Nonnull EventBus notifyEventBus,
                             @Nonnull SubscriptionManager sm) {
        Preconditions.checkNotNull(conf, "Configuration file should not be null");
        Preconditions.checkNotNull(notifyEventBus, "Notify event bus should not be null");
        Preconditions.checkNotNull(sm, "Subscription manager should not be null");
        this.conf = conf;
        this.notifyEventBus = notifyEventBus;
        this.sm = sm;
        this.rules = new HashSet<PriceRule>();
    }

    public DataPreProcessing registerTo(EventBus dataEventBus) {
        dataEventBus.register(this);
        return this;
    }

    public void addRule(PriceRule rule) {
        rules.add(rule); 
    }

    @Subscribe
    public void handleRawData(CoinData data) {
        double price = data.getLatestPrice();

        for (PriceRule rule : rules) {
            Set<String> uids = sm.query(rule.getRuleId());
            if (uids == null) {
                continue;
            }
            logger.info("uids for rule id {}: {}", rule.getRuleId(), uids);
            for (String uid : uids) {
                UserData user = sm.get(uid);
                logger.info("User data for uid {}: {}", uid, user);
                if (rule.meet(user, price, data.getType())) {
                    logger.info("Send notification to {}, latest price is {}", uid, price);
                    triggerNotify(new Notification(user.email, DestinationType.MAIL,
                            "The latest price of " + data.getType() + " is meet your subscription price, current price " + price));   
                }
            }
        }
    }

    void triggerNotify(@Nonnull Notification message) {
        notifyEventBus.post(message);
    }
}
