```sh
mvn clean compile exec:java -Ppro
mvn clean compile exec:java -Pcon

# other options
mvn clean compile exec:java -Pcona \
    -Durl=$BOOTSTRAP_URL \
    -Dtopics=my-topic -Dsms=100 \
    -Dreg=$REGISTRY_URL \
    -Dts=/tmp/client-ts.jks -Dtsp=secret

# registry (set artifactId == topicName)
curl -v -X POST -H "Content-Type: application/json" \
    -H "X-Registry-ArtifactId: my-topic" -H "X-Registry-ArtifactType: AVRO" \
    -d @src/main/resources/greeting.avsc $REGISTRY_URL/artifacts | jq
```
