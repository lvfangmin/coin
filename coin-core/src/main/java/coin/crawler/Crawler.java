package coin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

<<<<<<< Updated upstream
=======
import java.nio.charset.Charset;

>>>>>>> Stashed changes
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coin.conf.CoinConfiguration;
import coin.data.CoinData;
import coin.data.Data;
import coin.data.Trade;

import com.google.common.eventbus.EventBus;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
<<<<<<< Updated upstream
=======




>>>>>>> Stashed changes
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
                    Data data = new Data();
                    for (Element element : elements) {
                        logger.info("��������������� " + element.text());
                        data.setLatestPrice(Double.valueOf(element.text()));
                    }
                    logger.info("������������");
                    Elements buyElements = doc.select("div.real-left tbody tr");
                    List<Trade> buyList = new ArrayList<Trade>();
                    for (Element element : buyElements) {
                        logger.info(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
                        Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(1)),
                                                Double.valueOf(element.child(2).text().substring(1)));
                        buyList.add(trade);
                    }
                    logger.info("������������");
                    Elements sellElements = doc.select("div.real-right tbody tr");
                    List<Trade> sellList = new ArrayList<Trade>();
                    for (Element element : sellElements) {
                        Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(1)),
                                                Double.valueOf(element.child(2).text().substring(1)));
                        logger.info(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
                        sellList.add(trade);
                    }
                    data.setBuy(buyList);
                    data.setSell(sellList);
                    data.setType("btc");
                    List<Data> dataList = new ArrayList<Data>();
                    dataList.add(data);
                    CoinData coinData = new CoinData("okcoin");
                    coinData.setData(dataList);
                    eventBus.post(coinData);
                } catch (IOException e) {
                    logger.info(e.getMessage());
                }
            }
        }, 0, 1*2*1000);
    }
}
