package it.fvaleri.integ;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        final Properties prop = KafkaUtil.getProducerConfig();
        try (KafkaProducer<Object, Object> producer = new KafkaProducer<>(prop)) {

            int i = 0;
            Schema schema = PropertiesUtil.getRegistryUrl() != null
                    ? new Schema.Parser().parse(KafkaUtil.getResourceAsFile("/greeting.avsc"))
                    : null;
            while (true) {
                Object value = null;
                if (schema != null) {
                    // create a generic avro record using the schema
                    GenericRecord gr = new GenericData.Record(schema);
                    gr.put("Message", "test" + i++);
                    gr.put("Time", new Date().getTime());
                    value = gr;
                } else {
                    value = "test" + i++;
                }

                // send the message with no key
                ProducerRecord<Object, Object> record = new ProducerRecord<>(PropertiesUtil.getTopics(), value);
                RecordMetadata metadata = producer.send(record).get(); // blocking
                //producer.send(record, new MyCallback()); // non-blocking

                LOG.info("Message sent [topic: {}, partition: {}, offset: {}, key: {}, payload: {}]", record.topic(),
                        record.partition(), metadata.offset(), record.key(), record.value());

                Thread.sleep(PropertiesUtil.getDelayMs());
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
