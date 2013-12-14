package coin.conf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class CoinConfiguration {

    private final CompositeConfiguration conf;

    public CoinConfiguration() {
        conf = new CompositeConfiguration();
    }

    // TODO: throw exception if there is any malformed configuration
    public void validate() {

    }

    public void loadConf(String coinProperties, String crawlerXml) throws ConfigurationException, MalformedURLException {
        conf.addConfiguration(new PropertiesConfiguration(new File(coinProperties).toURI().toURL()));
        conf.addConfiguration(new XMLConfiguration(new File(crawlerXml).toURI().toURL()));
    }

    public CompositeConfiguration getCompositeConfiguration() {
        return conf;
    }
}
