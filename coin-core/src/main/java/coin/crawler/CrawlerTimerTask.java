package coin.crawler;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import coin.data.CoinData;
import coin.data.Trade;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.eventbus.EventBus;

import java.io.IOException;

public class CrawlerTimerTask extends TimerTask {

    private static Logger logger = LoggerFactory.getLogger(CrawlerTimerTask.class);
    private final CoinTarget target;
    private final EventBus eventBus;
    private final Timer timer;
    private final Random random = new Random();
    private String[] userAgents = new String[]{"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
                                               "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25",
                                               "Chrome", "Firefox", "Opera", "Safari", "Gecko"};
    private final AtomicLong al = new AtomicLong(0);

    public CrawlerTimerTask(CoinTarget target, EventBus eventBus, Timer timer) {
        this.target = target;
        this.eventBus = eventBus;
        this.timer = timer;
    }

    public CoinData fetchData() {
        try {
            Document doc = Jsoup.connect(this.target.getUrl())
                .userAgent(userAgents[(int)(al.getAndIncrement() % userAgents.length)]).get();
            Elements elements = doc.select(this.target.getLatestPriceSelector());
            CoinData data = new CoinData(this.target.getName(),this.target.getType());
            for (Element element : elements) {
                logger.info("Latest Price for {} is {} in platform {}", target.getType(),
                        element.text(), target.getName());
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
                sellList.add(trade);
            }
            data.setBuy(buyList);
            data.setSell(sellList);
            return data;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
        timer.schedule(new CrawlerTimerTask(target, eventBus, timer),
                Math.max(random.nextInt(target.getInterval()) * 1000, 5000));
        CoinData data = fetchData();
        if (data != null) {
            eventBus.post(data);
        }
    }
}
