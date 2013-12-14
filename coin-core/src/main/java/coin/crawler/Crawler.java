package coin.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.configuration.CompositeConfiguration;

import coin.conf.CoinConfiguration;
import coin.crawler.CoinTarget;

public class Crawler {
    private static Logger logger = LoggerFactory.getLogger(Crawler.class);
    private final CoinConfiguration conf;
    private final EventBus eventBus;
    private Timer timer;
    private List<CoinTarget> targets;

    protected static final String COIN_TARGET_NAME_PREFIX = "targets.target.name";
    protected static final String COIN_TARGET_NAME = "targets.target($i).name";
    protected static final String COIN_TARGET_TYPE = "targets.target($i).type";
    protected static final String COIN_TARGET_URL = "targets.target($i).url";
    protected static final String COIN_TARGET_PRICE_SELECTOR = "targets.target($i).latestPriceSelector";
    protected static final String COIN_TARGET_BUY_SELECTOR = "targets.target($i).buyInSelector";
    protected static final String COIN_TARGET_SELL_SELECTOR = "targets.target($i).sellOutSelector";
    protected static final String COIN_TARGET_PLACEHOLDER_LENGTH = "targts.target($i).placeholderLength";
    protected static final String COIN_TARGET_CRAWLER_INTERVAL = "targets.target($i).interval";
    
    public Crawler(CoinConfiguration conf, EventBus eventBus) {
        this.conf = conf;
        this.eventBus = eventBus;
        this.targets = new ArrayList<CoinTarget>();
        this.timer = new Timer();
        parseConf();
    }
 
    public void start() {
    	for (CoinTarget target : targets) {
    		CrawlerTimerTask  task = new CrawlerTimerTask(target, this.eventBus);
    		timer.scheduleAtFixedRate(task, 0, target.getInterval() * 1000);	
    	}
    }
    
    private void parseConf() {
    	CompositeConfiguration crawlerConf = this.conf.getCompositeConfiguration();
    	String[] targetPrefix = crawlerConf.getStringArray(COIN_TARGET_NAME_PREFIX);
    	
    	for (int i = 0; i < targetPrefix.length; ++i) {
    		String index = String.valueOf(i);
    		String name = crawlerConf.getString(COIN_TARGET_NAME.replace("$1", index));
    		String type = crawlerConf.getString(COIN_TARGET_TYPE.replace("$1", index));
            int interval = Integer.valueOf(crawlerConf.getString(COIN_TARGET_CRAWLER_INTERVAL.replace("$1", index)));
            String latestPriceSelector = crawlerConf.getString(COIN_TARGET_PRICE_SELECTOR.replace("$1", index));
            String buyInSelector = crawlerConf.getString(COIN_TARGET_BUY_SELECTOR.replace("$1", index));
            String sellOutSelector = crawlerConf.getString(COIN_TARGET_SELL_SELECTOR.replace("$1", index));
            String url  = crawlerConf.getString(COIN_TARGET_URL.replace("$1", index));
            int placeholderLength = Integer.valueOf(crawlerConf.getString(COIN_TARGET_PLACEHOLDER_LENGTH.replace("$1", index)));
            
    		CoinTarget target = new CoinTarget();
    		target.setBuyInSelector(buyInSelector);
    		target.setInterval(interval);
    		target.setLatestPriceSelector(latestPriceSelector);
    		target.setBuyInSelector(buyInSelector);
    		target.setSellOutSelector(sellOutSelector);
    		target.setType(type);
    		target.setName(name);
    		target.setUrl(url);
    		target.setPlaceholderLength(placeholderLength);
    		targets.add(target);
    	}
    }
    
    private void setHostVerifier() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
}
