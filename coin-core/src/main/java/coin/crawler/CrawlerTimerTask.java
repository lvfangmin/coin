package coin.crawler;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import coin.data.CoinData;
import coin.data.Trade;

import java.util.List;
import java.util.ArrayList;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.AsyncEventBus;

import java.io.IOException;

public class CrawlerTimerTask extends TimerTask {

    private static Logger logger = LoggerFactory.getLogger(CrawlerTimerTask.class);
    private CoinTarget target;
    private EventBus eventBus;
    
	public CrawlerTimerTask(CoinTarget target, EventBus eventBus) {
		this.target = target;
		this.eventBus = eventBus;
	}

    public CoinData fetchData() {
        try {
            Document doc = Jsoup.connect(this.target.getUrl()).userAgent("Mozilla").get();
            Elements elements = doc.select(this.target.getLatestPriceSelector());
            CoinData data = new CoinData(this.target.getName(),this.target.getType());
            for (Element element : elements) {
                logger.info("Latest Price : " + element.text());
                data.setLatestPrice(Double.valueOf(element.text()));
            }
            
            Elements buyElements = doc.select(this.target.getBuyInSelector());
            List<Trade> buyList = new ArrayList<Trade>();
            for (Element element : buyElements) {
                Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(target.getPlaceholderLength())),
                                                Double.valueOf(element.child(2).text().substring(target.getPlaceholderLength())));
                buyList.add(trade);
            }
            Elements sellElements = doc.select(target.getSellOutSelector());
            List<Trade> sellList = new ArrayList<Trade>();
            for (Element element : sellElements) {
                Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(target.getPlaceholderLength())),
                                        Double.valueOf(element.child(2).text().substring(target.getPlaceholderLength())));
                logger.info(element.child(1).text().substring(target.getPlaceholderLength()) + " " + element.child(2).text().substring(target.getPlaceholderLength()));
                sellList.add(trade);
            }
            data.setBuy(buyList);
            data.setSell(sellList);
            return data;
        } catch (IOException e) {
            logger.info(e.getMessage());
            return null;
        }
	}

    @Override
    public void run() {
        CoinData data = fetchData();
        eventBus.post(data); 
    }
}
