package it.fvaleri.integ;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KafkaClient implements Runnable {

    protected static final Logger LOG = LoggerFactory.getLogger(KafkaClient.class);

    private void sharedConfig(Properties props) {
        props.put("topics", System.getProperty("topics", "my-topic"));
        props.put("dms", Integer.parseInt(System.getProperty("dms", "0")));
        props.put("reg", System.getProperty("reg", "http://localhost:8081/api"));

        String truststore = System.getProperty("ts");
        String truststorePwd = System.getProperty("tsp");
        String keystore = System.getProperty("ks");
        String keystorePwd = System.getProperty("ksp");
        if (truststore != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            // disable hostname verification when using off-cluster NodePorts TLS access
            //env.put(SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM, "");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststore);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePwd);
            if (keystore != null) {
                props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystore);
                props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePwd);
            }
        }
    }

    protected Properties prodConfig() {
        Properties props = new Properties();
        sharedConfig(props);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "prod" + System.currentTimeMillis());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("url", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");

        // how many min.insync.replicas must acknowledge the receipt (all with idemp true)
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        // set internal cache to zero to send one message at a time
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384);

        // reconnect settings
        props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000);
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1_000);

        // retry settings (infinite retries)
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60_000);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 2_000);
        return props;
    }

    protected Properties consConfig() {
        Properties props = new Properties();
        sharedConfig(props);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group" + System.currentTimeMillis());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("url", "localhost:9092"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // max poll intervall and number of records
        if (props.get("dms") != "0") {
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, props.get("dms"));
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        }

        // heartbeat timeout to the consumer coordinator broker
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30_000);

        // auto commit (the offset is committed periodically)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1_000);

        // reconnect settings
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1_000);

        // retry settings
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60_000);
        props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 2_000);
        return props;
    }

    protected File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found");
        } else {
            return new File(resource.getFile());
        }
    }

}
