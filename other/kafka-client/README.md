```sh
# set src/main/resources/config.properties
mvn clean compile exec:java -Ppro
mvn clean compile exec:java -Pcon

# schema upload (set artifactId == topicName)
curl -v -X POST -H "Content-Type: application/json" \
    -H "X-Registry-ArtifactId: my-topic" -H "X-Registry-ArtifactType: AVRO" \
    -d @src/main/resources/greeting.avsc $REGISTRY_URL/artifacts | jq
```
