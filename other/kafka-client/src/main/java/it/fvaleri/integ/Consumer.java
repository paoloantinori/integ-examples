package it.fvaleri.integ;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.SerializationException;

public class Consumer extends KafkaClient {

    public static void main(String[] args) {
        new Consumer().run();
        System.exit(0);
    }

    @Override
    public void run() {
        final Properties props = consConfig();
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

            consumer.subscribe(Collections.singletonList(props.getProperty("topics")));
            while (true) {
                // batch of messages potentially from multiple topic/partitions
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }

                // commit before processing: at-most-once (missing msgs possible)
                //consumer.commitSync();

                for (ConsumerRecord<String, String> record : records) {
                    LOG.info("Received message [topic: {}, partition: {}, offset: {}, key: {}, payload: {}]",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }

                // commit after processing: at-least-once (duplicate msgs possible, must be idempotent)
                //consumer.commitSync();

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
