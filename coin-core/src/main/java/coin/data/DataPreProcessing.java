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
    private int previousBTCPrice = 0;
    private int previousLTCPrice = 0;

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

    private void sendNotifications(int ruleId, String type, String price) {
        String rp = "rid:" + ruleId + ":price:" + price;
        Set<String> uidsids = sm.getJedis().smembers(rp);
        for (String uidsid: uidsids) {
            String[] us = uidsid.split(":");
            String uid = us[0];
            String sid = us[1];
            logger.info("Send notification to {}, latest price is {}", uid, price);
            triggerNotify(new Notification(sm.get(uid), DestinationType.MAIL,
                    "The latest price of " + type + " is meet your subscription price, current price " + price));

            sm.getJedis().srem(rp, uidsid);
            sm.getJedis().del("uid:" + uid + ":sid:" + sid);
            sm.getJedis().srem("uid" + uid + ":subscriptions", sid);

        }
    }

    @Subscribe
    public void handleRawData(CoinData data) {
        int price = (int)data.getLatestPrice();

        int ruleId = 0;
        String type = data.getType();

        if (type.equals("btc") && previousBTCPrice > 0) {
            if (price > previousBTCPrice) {
                ruleId = 1;
            } else {
                ruleId = 2;
            }
        }

        if (type.equals("ltc") && previousLTCPrice > 0) {
            if (price > previousLTCPrice) {
                ruleId = 3;
            } else {
                ruleId = 4;
            }
        }

        if (ruleId != 0) {
            Set<String> prices = sm.getJedis().smembers("rid:" + ruleId);
            for (String p : prices) {
                if (ruleId == 1 || ruleId == 3) {
                    if (price >= Integer.parseInt(p)) {
                        sendNotifications(ruleId, type, p);
                    }
                } else if (ruleId == 2 || ruleId == 4) {
                    if (price <= Integer.parseInt(p)) {
                        sendNotifications(ruleId, type, p);
                    }
                }
            }
        }

        if (type.equals("btc")) {
            previousBTCPrice = price;
        } else {
            previousLTCPrice = price;
        }
    }

    void triggerNotify(@Nonnull Notification message) {
        notifyEventBus.post(message);
    }
}
