```sh
cd spring-boot-rest-service
mvn clean compile
mvn spring-boot:run -f ./spring-boot-rest-service-server/pom.xml
mvn spring-boot:run -f ./spring-boot-rest-service-client/pom.xml

curl -H "Content-Type: application/json" http://localhost:8080/health | jq
curl -H "Content-Type: application/json" http://localhost:8080/api/doc | jq
curl -H "Content-Type: application/json" http://localhost:8080/api/greet/Fede | jq

# openshift
mvn clean fabric8:deploy -Popenshift
HOSTNAME=$(oc get route spring-boot-rest-service-server -o jsonpath={.spec.host})
curl -H "Content-Type: application/json" http://$HOSTNAME/api/greet/Fede | jq
```
