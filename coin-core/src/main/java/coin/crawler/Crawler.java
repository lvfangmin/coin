package coin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coin.conf.CoinConfiguration;
import coin.data.CoinData;
import coin.data.Trade;

import com.google.common.eventbus.EventBus;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
//import org.apache.commons.configuration;
//import org.apache.commons.configuration.BaseConfiguration;
import java.io.IOException;

public class Crawler {
    private static Logger logger = LoggerFactory.getLogger(Crawler.class);
    private final CoinConfiguration conf;
    private final EventBus eventBus;
    private final Timer timer;

    public Crawler(CoinConfiguration conf, EventBus eventBus) {
        this.conf = conf;
        this.eventBus = eventBus;
        this.timer = new Timer();
    }

    private void setHostVerifier() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.okcoin.com/market.do?symbol=0").userAgent("Mozilla").get();
                    Elements elements = doc.select("#marketLast");
                    CoinData data = new CoinData("okcoin", "btc");
                    for (Element element : elements) {
                        data.setLatestPrice(Double.valueOf(element.text()));
                    }
                    Elements buyElements = doc.select("div.real-left tbody tr");
                    List<Trade> buyList = new ArrayList<Trade>();
                    for (Element element : buyElements) {
                        logger.debug(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
                        Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(1)),
                                                Double.valueOf(element.child(2).text().substring(1)));
                        buyList.add(trade);
                    }
                    Elements sellElements = doc.select("div.real-right tbody tr");
                    List<Trade> sellList = new ArrayList<Trade>();
                    for (Element element : sellElements) {
                        Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(1)),
                                                Double.valueOf(element.child(2).text().substring(1)));
                        logger.debug(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
                        sellList.add(trade);
                    }
                    data.setBuy(buyList);
                    data.setSell(sellList);
                    eventBus.post(data);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }, 0, 1*10*1000);
    }
}
