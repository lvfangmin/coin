import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class Example {
    public static void main(String [] args) {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
	        return true;
	    }
        });
        try {
            //Document doc = Jsoup.connect("http://www.baidu.com").userAgent("Mozilla").get();           
            //System.setProperty("javax.net.ssl.trustStore", "/Library/Java/Home/lib/security/cacerts");
            //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
            Document doc = Jsoup.connect("https://www.okcoin.com/market.do?symbol=0").userAgent("Mozilla").get();           
            //Document doc = Jsoup.connect("https://www.google.com").userAgent("Mozilla").get();           
            Elements elements = doc.select("#marketLast");
            for (Element element : elements) {
               System.out.println("最新价格： " + element.text());
              //System.out.print("url : " + element.attr("href"));
            }            
            System.out.println("买入委托");
            Elements buyElements = doc.select("div.real-left tbody tr");
            for (Element element : buyElements) {
                System.out.println(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
            }
            System.out.println("卖出委托");
            Elements sellElements = doc.select("div.real-right tbody tr");
            for (Element element : sellElements) {
                System.out.println(element.child(1).text().substring(1) + " " + element.child(2).text().substring(1));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Gson example
        Gson gson= new Gson();
        Trade trade = new Trade(100, 1); 
        List<String> buy = new ArrayList<String>();
        List<String> sell = new ArrayList<String>();
        buy.add(trade.toString());
        sell.add(trade.toString());
        Data data = new Data("btc");
        data.setBuy(buy); 
        data.setSell(sell);
        data.setLatestPrice(1000);
        CoinData coinData = new CoinData("okcoin");
        List<Data> dataList = new ArrayList<Data>();
        dataList.add(data);
        coinData.setDatas(dataList);        
        String str = gson.toJson(coinData);
        System.out.println(trade.toString());
        System.out.println(str);
    }
}
