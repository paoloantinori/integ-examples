```sh
mvn clean spring-boot:run

curl -u admin:admin http://localhost:13000
curl -u admin:admin http://localhost:13500/management/hawtio/jolokia/list | jq

# hawtio
http://localhost:13500/management/hawtio
```
