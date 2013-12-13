import java.util.List;

public class CoinData {
    private String name;
    private long timestamp;
    private List<Data> data;

    public CoinData(String name) {
        this.name = name;
        this.timestamp = System.currentTimeMillis() / 1000;
    } 

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(List<Data> dataList) {
        this.data = dataList;
    }

    public String getName() {
        return this.name;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public List<Data> getData() {
        return this.data;
    }
}
