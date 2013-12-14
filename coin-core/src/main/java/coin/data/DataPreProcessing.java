package coin.data;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import coin.conf.CoinConfiguration;

import coin.notify.Notification;
import coin.notify.Notification.DestinationType;

import javax.annotation.Nonnull;

public class DataPreProcessing {
    private final CoinConfiguration conf;
    private final EventBus notifyEventBus;

    public DataPreProcessing(@Nonnull CoinConfiguration conf, @Nonnull EventBus notifyEventBus) {
        Preconditions.checkNotNull(conf, "Configuration file should not be null");
        Preconditions.checkNotNull(notifyEventBus, "Notify event bus should not be null");
        this.conf = conf;
        this.notifyEventBus = notifyEventBus;
    }

    public DataPreProcessing registerTo(EventBus dataEventBus) {
        dataEventBus.register(this);
        return this;
    }

    @Subscribe
    public void handleRawData(CoinData data) {
        Data d = data.getData().get(0);
        if (d.getLatestPrice() > 1000) {
            triggerNotify(new Notification("516408755@qq.com", DestinationType.MAIL,
                "The latest price of " + d.getType() + " is larger than 1000 now."));
        }
    }

    void triggerNotify(@Nonnull Notification message) {
        notifyEventBus.post(message);
    }
}
