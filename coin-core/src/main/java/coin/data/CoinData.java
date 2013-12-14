package coin.data;

import java.util.List;

public class CoinData {
    private String name;
    private String type;
    private long timestamp;
    private double latestPrice;
    private List<Trade> buy;
    private List<Trade> sell;

    public CoinData(String name, String type) {
        this.name = name;
        this.type = type;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public void setLatestPrice(double latestPrice) {
       this.latestPrice = latestPrice;
    }

    public void setBuy(List<Trade> buy) {
       this.buy = buy;
    }

    public void setSell(List<Trade> sell) {
       this.sell = sell;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
       return this.type;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getLatestPrice() {
       return this.latestPrice;
    }

    public List<Trade> getBuy() {
       return this.buy;
    }

    public List<Trade> getSell() {
       return this.sell;
    }

}
