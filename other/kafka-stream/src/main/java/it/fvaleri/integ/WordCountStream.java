package it.fvaleri.integ;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public final class WordCountStream {

    private static final Logger LOG = LoggerFactory.getLogger(WordCountStream.class);

    public static void main(String[] args) {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> source = builder.stream("streams-plaintext-input");
        LOG.info("Stream created");

        // KTable is the stateful view of a KStream (stream-table duality)
        final KTable<String, Long> counts = source
            .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" ")))
            .groupBy((key, value) -> value)
            .count();

        // need to override value serde to Long type
        counts.toStream().to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        final KafkaStreams streams = new KafkaStreams(builder.build(), envProperties());
        final CountDownLatch latch = new CountDownLatch(1);

        // catch Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Properties envProperties() {
        Properties env = new Properties();
        env.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-stream");
        env.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.64.97:32100");
        env.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        env.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        env.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        env.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // enable trans by setting exactly_once (default at_least_once)
        //env.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once");
        // determines the TX size and therefore processing latency (default 30_000, exactly_once 100)
        //env.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "100");

        // reconnect settings
        env.put(StreamsConfig.RECONNECT_BACKOFF_MS_CONFIG, 1_000);
        env.put(StreamsConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000);

        // retry settings (infinite retries)
        env.put(StreamsConfig.REQUEST_TIMEOUT_MS_CONFIG, 60_000);
        env.put(StreamsConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        env.put(StreamsConfig.RETRY_BACKOFF_MS_CONFIG, 2_000);

        // increasing resiliency of internal streams topics (default: 1)
        env.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 2);
        env.put(ProducerConfig.ACKS_CONFIG, "all");

        return env;
    }

}
