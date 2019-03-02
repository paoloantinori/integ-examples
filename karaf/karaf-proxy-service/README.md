```sh
mvn clean install

features:install war camel-servlet camel-http4
install -s mvn:it.fvaleri.integ/karaf-proxy-service/1.0.0-SNAPSHOT
http:list

curl -H "Content-Type: application/json" http://localhost:8181/proxy/test
curl -X POST -H "Content-Type: application/json" \
    -d '{"title":"test post","body":"test post","userId":1}' http://localhost:8181/proxy/test
```
