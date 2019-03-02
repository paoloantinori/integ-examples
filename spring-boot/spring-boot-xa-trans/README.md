```sh
mvn clean spring-boot:run

# openshift (install AMQ and PostgresSQL from the catalog)
oc set env dc/postgresql POSTGRESQL_MAX_PREPARED_TRANSACTIONS=100
oc create -f pvc.yml

mvn clean fabric8:deploy -Popenshift
#mvn fabric8:undeploy -Popenshift

oc scale statefulset spring-boot-xa-trans --replicas 3

HOSTNAME=$(oc get route spring-boot-xa-trans -o jsonpath={.spec.host})
curl http://$HOSTNAME/api/

curl -X POST http://$HOSTNAME/api/?entry=hello
curl http://$HOSTNAME/api/

curl -X POST http://$HOSTNAME/api/?entry=fail
curl http://$HOSTNAME/api/
```
