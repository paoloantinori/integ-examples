```sh
mvn clean compile

mvn clean spring-boot:run -f ./spring-boot-soap-service-server/pom.xml -Drun.arguments="--server.port=8011"
mvn clean spring-boot:run -f ./spring-boot-soap-service-server/pom.xml -Drun.arguments="--server.port=8012"
mvn clean spring-boot:run -f ./spring-boot-soap-service-server/pom.xml -Drun.arguments="--server.port=8013"

mvn clean spring-boot:run -f ./spring-boot-soap-service-client/pom.xml
```
