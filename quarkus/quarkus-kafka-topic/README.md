```sh
oc create -f - <<EOF
apiVersion: kafka.strimzi.io/v1alpha1
kind: KafkaTopic
metadata:
  name: prices
  labels:
    strimzi.io/cluster: my-cluster
spec:
  partitions: 3
  replicas: 3
  config:
    retention.ms: 7200000
    segment.bytes: 1073741824
EOF

oc exec my-cluster-kafka-0 -- bin/kafka-topics.sh --zookeeper localhost:2181 --topic prices --describe

# s2i build
NAME="quarkus-kafka-topic"
oc new-build --binary --name=$NAME -l app=$NAME
oc patch bc/$NAME -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile"}}}}'
oc start-build $NAME --from-dir=. --follow

oc new-app --image-stream=$NAME:latest
#oc delete all -l app=$NAME
```
