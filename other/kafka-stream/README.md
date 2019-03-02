```sh
oc rsh my-cluster-kafka-0
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic streams-plaintext-input --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic streams-plaintext-output --partitions 1 --replication-factor 1

bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

rm -rf /tmp/kafka-streams
mvn compile exec:java -Pwc
```
