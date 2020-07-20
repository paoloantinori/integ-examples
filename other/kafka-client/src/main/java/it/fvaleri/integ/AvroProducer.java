package it.fvaleri.integ;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import io.apicurio.registry.utils.serde.AbstractKafkaSerDe;
import io.apicurio.registry.utils.serde.AbstractKafkaSerializer;
import io.apicurio.registry.utils.serde.AvroKafkaSerializer;
import io.apicurio.registry.utils.serde.strategy.FindBySchemaIdStrategy;
import io.apicurio.registry.utils.serde.strategy.SimpleTopicIdStrategy;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;

public class AvroProducer extends KafkaClient {

    public static void main(String[] args) {
        new AvroProducer().run();
        System.exit(0);
    }

    @Override
    public void run() {
        Properties props = prodConfig();
        props.put(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, props.get("reg"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());
        props.put(AbstractKafkaSerializer.REGISTRY_ARTIFACT_ID_STRATEGY_CONFIG_PARAM, SimpleTopicIdStrategy.class.getName());
        props.put(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM, FindBySchemaIdStrategy.class.getName());

        try (KafkaProducer<Object, Object> producer = new KafkaProducer<>(props)) {

            int i = 0;
            Schema schema = new Schema.Parser().parse(getFileFromResources("greeting.avsc"));
            while (true) {
                // create a generic record using the schema
                GenericRecord value = new GenericData.Record(schema);
                value.put("Message", "test" + i++);
                value.put("Time", new Date().getTime());

                // send the message with no key
                ProducerRecord<Object, Object> record = new ProducerRecord<>(props.getProperty("topics"), value);
                Future<RecordMetadata> future = producer.send(record);
                RecordMetadata metadata = future.get(); // blocking
                LOG.info("Message sent [topic: {}, partition: {}, offset: {}, key: {}, payload: {}]",
                    record.topic(), record.partition(), metadata.offset(), record.key(), record.value());

                Thread.sleep((int) props.get("dms"));
            }

        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }

}
