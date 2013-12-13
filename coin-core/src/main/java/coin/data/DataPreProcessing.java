package coin.data;

import com.google.common.eventbus.EventBus;

import coin.conf.CoinConfiguration;

public class DataPreProcessing {
    private final CoinConfiguration conf;

    public DataPreProcessing(CoinConfiguration conf) {
        this.conf = conf;
    }

    public DataPreProcessing register(EventBus dataEventBus) {
        dataEventBus.register(this);
        return this;
    }
}
