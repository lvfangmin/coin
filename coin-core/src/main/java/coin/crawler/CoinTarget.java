package coin.crawler;

public class CoinTarget {

	private String name;
	private int interval;
	private String type;
	private String url;
	private String latestPriceSelector;
	private String buyInSelector;
	private String sellOutSelector;
	private int placeholderLength;
	
	public CoinTarget() {
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLatestPriceSelector() {
		return latestPriceSelector;
	}

	public void setLatestPriceSelector(String latestPriceSelector) {
		this.latestPriceSelector = latestPriceSelector;
	}

	public String getBuyInSelector() {
		return buyInSelector;
	}

	public void setBuyInSelector(String buyInSelector) {
		this.buyInSelector = buyInSelector;
	}

	public String getSellOutSelector() {
		return sellOutSelector;
	}

	public void setSellOutSelector(String sellOutSelector) {
		this.sellOutSelector = sellOutSelector;
	}

	public int getPlaceholderLength() {
		return placeholderLength;
	}

	public void setPlaceholderLength(int placeholderLength) {
		this.placeholderLength = placeholderLength;
	}

	public CoinTarget(String name, int interval) {
		this.name = name;
		this.interval = interval;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
