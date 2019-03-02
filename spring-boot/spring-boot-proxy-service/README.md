```sh
mvn clean spring-boot:run
curl http://localhost:8080/api/test/proxy

curl http://localhost:8080/actuator/ | jq
curl http://localhost:8080/actuator/health | jq
curl http://localhost:8080/actuator/jolokia/read/org.apache.camel:context=*,type=routes,name=*

# openshift
mvn clean fabric8:deploy -Popenshift
HOSTNAME=$(oc get route spring-boot-proxy-service -o jsonpath={.spec.host})
curl http://$HOSTNAME/api/test/proxy
```
