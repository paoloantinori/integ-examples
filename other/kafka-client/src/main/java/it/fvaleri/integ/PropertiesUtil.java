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

    public static String getBootstrapUrl() {
        return prop.getProperty("client.bootstrapUrl", "localhost:9092");
    }

    public static String getTopics() {
        return prop.getProperty("client.topics", "my-topic");
    }

    public static Long getDelayMs() {
        return Long.parseLong(prop.getProperty("client.delayMs", "0"));
    }

    public static String getRegistryUrl() {
        return prop.getProperty("client.registryUrl");
    }

    public static String getTruststore() {
        return prop.getProperty("client.truststore");
    }

    public static String getTruststorePwd() {
        return prop.getProperty("client.truststorePwd");
    }

    public static String getKeystore() {
        return prop.getProperty("client.keystore");
    }

    public static String getKeystorePwd() {
        return prop.getProperty("client.keystorePwd");
    }

}
