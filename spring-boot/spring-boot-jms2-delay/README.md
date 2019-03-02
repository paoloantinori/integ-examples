```sh
mvn clean spring-boot:run

# openshift (install AMQ from the catalog)
mvn clean fabric8:deploy -Popenshift
#mvn fabric8:undeploy -Popenshift
oc logs $POD_NAME
```
