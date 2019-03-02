package it.fvaleri.integ;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

public class Producer extends KafkaClient {

    public static void main(String[] args) {
        new Producer().run();
        System.exit(0);
    }

    @Override
    public void run() {
        final Properties props = prodConfig();
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            int i = 0;
            while (true) {
                String value = "test" + i++;
                ProducerRecord<String, String> record = new ProducerRecord<>(props.getProperty("topics"), value);

                //producer.send(record, new MyCallback()); //async
                Future<RecordMetadata> future = producer.send(record);
                RecordMetadata metadata = future.get(); // blocking

                LOG.info("Message sent [topic: {}, partition: {}, offset: {}, key: {}, payload: {}]",
                    record.topic(), record.partition(), metadata.offset(), record.key(), record.value());

                Thread.sleep((int) props.get("sms"));
            }

        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }

    class MyCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata m, Exception e) {
            if (e == null) {
                LOG.info("Message sent [topic: {}, partition: {}, offset: {}]", m.topic(), m.partition(), m.offset());
            } else {
                LOG.info("Failed to send message: {}", e.getMessage());
            }
        }
    }

}
