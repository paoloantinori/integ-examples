package it.fvaleri.integ;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.apicurio.registry.utils.serde.AbstractKafkaSerDe;
import io.apicurio.registry.utils.serde.AbstractKafkaSerializer;
import io.apicurio.registry.utils.serde.AvroKafkaDeserializer;
import io.apicurio.registry.utils.serde.AvroKafkaSerializer;
import io.apicurio.registry.utils.serde.strategy.FindBySchemaIdStrategy;
import io.apicurio.registry.utils.serde.strategy.SimpleTopicIdStrategy;

public final class KafkaUtil {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtil.class);
    private static Properties pprop, cprop;

    static {
        pprop = new Properties();
        cprop = new Properties();

        // SHARED
        Map<Object, Object> shared = new HashMap<>();
        if (PropertiesUtil.getTruststore() != null) {
            shared.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            // disable hostname verification when using NodePorts TLS access
            //shared.put(SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM, "");
            shared.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, PropertiesUtil.getTruststore());
            shared.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, PropertiesUtil.getTruststorePwd());
            if (PropertiesUtil.getKeystore() != null) {
                shared.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, PropertiesUtil.getKeystore());
                shared.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, PropertiesUtil.getKeystorePwd());
            }
        }

        pprop.putAll(shared);
        cprop.putAll(shared);

        // PRODUCER
        pprop.put(ProducerConfig.CLIENT_ID_CONFIG, "prod" + System.currentTimeMillis());
        pprop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesUtil.getBootstrapUrl());
        pprop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pprop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pprop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");

        // how many min.insync.replicas must acknowledge the receipt (all with idemp true)
        pprop.put(ProducerConfig.ACKS_CONFIG, "1");
        pprop.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        // set internal cache to zero to send one message at a time
        pprop.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384);

        // reconnect settings
        pprop.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000);
        pprop.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1_000);

        // retry settings (infinite retries)
        pprop.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60_000);
        pprop.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        pprop.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 2_000);

        // avro registry
        if (PropertiesUtil.getRegistryUrl() != null) {
            pprop.put(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, PropertiesUtil.getRegistryUrl());
            pprop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            pprop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());
            pprop.put(AbstractKafkaSerializer.REGISTRY_ARTIFACT_ID_STRATEGY_CONFIG_PARAM, SimpleTopicIdStrategy.class.getName());
            pprop.put(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM, FindBySchemaIdStrategy.class.getName());
        }

        // CONSUMER
        cprop.put(ConsumerConfig.GROUP_ID_CONFIG, "group" + System.currentTimeMillis());
        cprop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesUtil.getBootstrapUrl());
        cprop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cprop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cprop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // max poll intervall and number of records
        if (PropertiesUtil.getDelayMs() != 0) {
            cprop.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, PropertiesUtil.getDelayMs());
            cprop.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        }

        // heartbeat timeout to the consumer coordinator broker
        cprop.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30_000);

        // auto commit (the offset is committed periodically)
        cprop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        cprop.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1_000);

        // reconnect settings
        cprop.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000);
        cprop.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1_000);

        // retry settings
        cprop.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60_000);
        cprop.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 2_000);

        // avro registry
        if (PropertiesUtil.getRegistryUrl() != null) {
            cprop.put(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, PropertiesUtil.getRegistryUrl());
            cprop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            cprop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroKafkaDeserializer.class.getName());
        }
    }

    private KafkaUtil() {
    }

    public static Properties getProducerConfig() {
        LOG.debug("Getting producer config");
        return pprop;
    }

    public static Properties getConsumerConfig() {
        LOG.debug("Getting consumer config");
        return cprop;
    }

    public static File getResourceAsFile(String name) {
        LOG.debug("Getting resource {}", name);
        ClassLoader classLoader = KafkaUtil.class.getClassLoader();
        URL resource = classLoader.getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException(
                String.format("Resource %s not found", name));
        } else {
            return new File(resource.getFile());
        }
    }

}
