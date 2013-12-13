package coin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.AsyncEventBus;

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
    private String configFile;
//    private Configuration cfg;
    private EventBus  eventBus;

    public Crawler() {
         this.eventBus = new EventBus();
    }

//    public Crawler(Configuration cfg) {
//        this.cfg = cfg;
//    }

//    public Crawler(Configuration cfg, EventBus eventBus) {
//        this.cfg = cfg;
//        this.eventBus = eventBus;
//    }

//    public void setConfiguration(Configuration cfg) {
//        this.cfg = cfg;
//    }
   
    public void setEventBus(EventBus eventBus) {
	this.eventBus = eventBus;
    } 

    public void run( ) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
  	    @Override
            public void run() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session)
			{
			return true;
			}
		});
		try {
		    Document doc = Jsoup.connect("https://www.okcoin.com/market.do?symbol=0").userAgent("Mozilla").get();
		    Elements elements = doc.select("#marketLast");
                    Data data = new Data();                    
		    for (Element element : elements) {
		        System.out.println("最新价格： " + element.text());
                        data.setLatestPrice(Double.valueOf(element.text()));
		    }
		    System.out.println("买入委托");
		    Elements buyElements = doc.select("div.real-left tbody tr");
                    List<Trade> buyList = new ArrayList<Trade>();
		    for (Element element : buyElements) {
			System.out.println(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
                        Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(1)), Double.valueOf(element.child(2).text().substring(1)));
                        buyList.add(trade); 
		    }
		    System.out.println("卖出委托");
		    Elements sellElements = doc.select("div.real-right tbody tr");
                    List<Trade> sellList = new ArrayList<Trade>();
		    for (Element element : sellElements) {
                        Trade trade = new Trade(Double.valueOf(element.child(1).text().substring(1)), Double.valueOf(element.child(2).text().substring(1)));
		    	System.out.println(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
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
		    System.out.println(e.getMessage());
		}		
            }
        }, 0, 1*2*1000);
    }
    
    public static void main(String[] args) {
        Crawler crawler  = new Crawler(); 
        crawler.run();
    }
}
