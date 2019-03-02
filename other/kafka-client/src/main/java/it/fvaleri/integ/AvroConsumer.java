package it.fvaleri.integ;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.apicurio.registry.utils.serde.AbstractKafkaSerDe;
import io.apicurio.registry.utils.serde.AvroKafkaDeserializer;

public class AvroConsumer extends KafkaClient {

    public static void main(String[] args) {
        new AvroConsumer().run();
        System.exit(0);
    }

    @Override
    public void run() {
        Properties props = consConfig();
        props.put(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, props.get("reg"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroKafkaDeserializer.class.getName());

        try (KafkaConsumer<Long, GenericRecord> consumer = new KafkaConsumer<>(props)) {

            consumer.subscribe(Collections.singletonList(props.getProperty("topics")));
            while (true) {
                // batch of messages potentially from multiple topic/partitions
                ConsumerRecords<Long, GenericRecord> records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }

                for (ConsumerRecord<Long, GenericRecord> record : records) {
                    LOG.info("Received message [topic: {}, partition: {}, offset: {}, key: {}, payload: {}]",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }

                Thread.sleep((int) props.get("sms"));
            }

        } catch (Exception e) {
            if (e instanceof SerializationException) {
                LOG.error("Wrong deserializer");
            }
            LOG.error("Unexpected error", e);
        }
    }

}
