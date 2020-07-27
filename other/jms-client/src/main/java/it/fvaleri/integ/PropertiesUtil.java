package it.fvaleri.integ;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertiesUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);
    private static final String FILE = "/config.properties";
    private static Properties prop;

    static {
        try (InputStream stream = PropertiesUtil.class.getResourceAsStream(FILE)) {
            prop = new Properties();
            prop.load(stream);
            LOG.debug("Loaded properties: {}", prop);
        } catch (Exception e) {
            LOG.error("Error while loading properties", e);
        }
    }

    private PropertiesUtil() {
    }

    public static String getVersion() {
        return prop.getProperty("client.version");
    }

    public static String getConnFactory() {
        return prop.getProperty("client.cf");
    }

    public static String getConnUrl() {
        return prop.getProperty("client.url");
    }

    public static String getUser() {
        return prop.getProperty("client.user");
    }

    public static String getPass() {
        return prop.getProperty("client.pass");
    }

    public static String getQueue() {
        return prop.getProperty("client.queue");
    }

    public static String getTopic() {
        return prop.getProperty("client.topic");
    }

    public static Long getMessageTTL() {
        return Long.parseLong(prop.getProperty("client.messageTTL", "0"));
    }

    public static Long getDelayMs() {
        return Long.parseLong(prop.getProperty("client.delayMs", "0"));
    }

}
