```sh
# dev mode run with fast hot-reload
mvn compile quarkus:dev -Ddebug=false
mvn clean test

curl http://localhost:8080/fruits | jq
curl -X POST -H "Content-Type: application/json" -d '{"name":"Banana"}' http://localhost:8080/fruits
curl -X DELETE http://localhost:8080/fruits/4

# application package (all dependecies in target/lib)
mvn clean package
java -jar target/quarkus-rest-service-1.0.0-SNAPSHOT-runner.jar

# GraalVM setup
brew cask install graalvm-ce-java8
export GRAALVM_HOME="/Library/Java/JavaVirtualMachines/graalvm-ce-java8-19.3.1/Contents/Home"
$GRAALVM_HOME/bin/gu install native-image
$GRAALVM_HOME/bin/native-image --version

# native compilation (file shows executable platform)
mvn clean package -Pnative
file quarkus-rest-service-1.0.0-SNAPSHOT-runner
./target/quarkus-rest-service-1.0.0-SNAPSHOT-runner

# openshift
NAME="quarkus-rest-service"
oc new-build --binary --name=$NAME -l app=$NAME
oc patch bc/$NAME -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile"}}}}'
oc start-build $NAME --from-dir=. --follow

oc new-app --image-stream=$NAME:latest
oc expose service $NAME
oc scale dc $NAME --replicas=100
#oc delete all -l app=$NAME

export URL="http://$(oc get route | grep $NAME | awk '{print $2}')"
curl $URL/fruits
```
