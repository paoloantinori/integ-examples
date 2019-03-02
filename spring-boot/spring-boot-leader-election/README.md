```sh
mvn clean fabric8:deploy -Popenshift
# even if we scale there will be always one service active (singleton)
oc scale dc spring-boot-leader-election --replicas=2
```
