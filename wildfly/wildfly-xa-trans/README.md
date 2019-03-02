```sh
mvn clean install
mvn install -Pwildfly
#mvn clean -Pwildfly

$AMQ_HOME/bin/artemis queue stat
$AMQ_HOME/bin/artemis producer --destination TestQueue --message-count 1 --url tcp://localhost:61616
```
