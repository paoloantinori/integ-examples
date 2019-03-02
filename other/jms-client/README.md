```sh
mvn clean compile exec:java -Ppro
mvn clean compile exec:java -Pcon

# other options
mvn exec:java -Ppro -Dcf=qpid \
    -Durl="amqp://localhost:5672" \
    -Dqueue=TestQueue \
    -Duser=admin -Dpass=admin \
    -Dnom=10 -Ddms=1000
```
