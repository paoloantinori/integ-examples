```sh
cd redis-X
make
src/redis-server

mvn clean spring-boot:run
curl http://localhost:8080/api/hello/Fede
```
