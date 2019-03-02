## Camel bridge application

Prerequisites:
- Fuse 6.3.0 R14
- AMQ 7.4.2

Create and start destination broker (external Artemis)
```sh
cd $AMQ_HOME
bin/artemis create hosts/host0 --name host0 --user admin --password admin --require-login --port-offset 1
cd hosts/hosts0 && bin/artemis run
```

Create and start fuse with source broker (embedded ActiveMQ)
```sh
cd $FUSE_HOME
echo "admin=admin,admin" >> etc/users.properties
bin/fuse
```

Check source broker from Fuse console
```sh
JBossFuse:karaf@root> activemq:dstat
Name                                                Queue Size  Producer #  Consumer #   Enqueue #   Dequeue #   Forward #    Memory %
ActiveMQ.Advisory.MasterBroker                               0           0           0           1           0           0           0
```

Check destination broker from another shell
```sh
fvaleri-mac:host0 fvaleri$ bin/artemis queue stat --url tcp://localhost:61617 --user admin --password admin
|NAME                     |ADDRESS                  |CONSUMER_COUNT |MESSAGE_COUNT |MESSAGES_ADDED |DELIVERING_COUNT |MESSAGES_ACKED |
|DLQ                      |DLQ                      |0              |0             |0              |0                |0              |
|ExpiryQueue              |ExpiryQueue              |0              |0             |0              |0                |0              |
```

Build and run the Camel bridge application
```sh
cd $APP_HOME
mvn clean install

# from Fuse console
install -s mvn:it.fvaleri.integ/karaf-amq-bridge/1.0.0-SNAPSHOT
```

Check if the message has been routed successfully
```sh
# from Fuse console
JBossFuse:karaf@root> activemq:dstat
Name                                                Queue Size  Producer #  Consumer #   Enqueue #   Dequeue #   Forward #    Memory %
ActiveMQ.Advisory.Connection                                 0           0           0           1           0           0           0
ActiveMQ.Advisory.Consumer.Queue.input                       0           0           0           3           0           0           0
ActiveMQ.Advisory.MasterBroker                               0           0           0           1           0           0           0
ActiveMQ.Advisory.Queue                                      0           0           0           1           0           0           0
input                                                        0           0           1           1           1           0           0

# from AMQ shell
fvaleri-mac:host0 fvaleri$ bin/artemis queue stat --url tcp://localhost:61617 --user admin --password admin
|NAME                     |ADDRESS                  |CONSUMER_COUNT |MESSAGE_COUNT |MESSAGES_ADDED |DELIVERING_COUNT |MESSAGES_ACKED |
|DLQ                      |DLQ                      |0              |0             |0              |0                |0              |
|ExpiryQueue              |ExpiryQueue              |0              |0             |0              |0                |0              |
|output                   |output                   |0              |1             |1              |0                |0              |
```

Now set `msgProcessor.raiseError=true` in [camel-context.xml](src/main/resources/OSGI-INF/blueprint/camel-context.xml) and repeat the test
```sh
cd $APP_HOME
mvn clean install

# from Fuse console
uninstall karaf-amq-bridge
install -s mvn:it.fvaleri.integ/karaf-amq-bridge/1.0.0-SNAPSHOT

JBossFuse:karaf@root> activemq:dstat
Name                                                Queue Size  Producer #  Consumer #   Enqueue #   Dequeue #   Forward #    Memory %
ActiveMQ.Advisory.Connection                                 0           0           0           3           0           0           0
ActiveMQ.Advisory.Consumer.Queue.input                       0           0           0          17           0           0           0
ActiveMQ.Advisory.MasterBroker                               0           0           0           1           0           0           0
ActiveMQ.Advisory.Queue                                      0           0           0           1           0           0           0
input                                                        1           0           1           2           1           0           0

JBossFuse:karaf@root> log:tail
...
2020-02-05 08:56:10,865 | WARN  | sConsumer[input] | TransactionErrorHandler          | 232 - org.apache.camel.camel-core - 2.17.0.redhat-630424 | Transaction rollback (0x8d634c9) redelivered(true) for (MessageId: ID:fvaleri-mac-49751-1580888866235-8:1:2:1:1 on ExchangeId: ID-fvaleri-mac-49755-1580889143653-1-9) caught: java.lang.RuntimeException: Forced exception
...

# from AMQ shell
fvaleri-mac:host0 fvaleri$ bin/artemis queue stat --url tcp://localhost:61617 --user admin --password admin
|NAME                     |ADDRESS                  |CONSUMER_COUNT |MESSAGE_COUNT |MESSAGES_ADDED |DELIVERING_COUNT |MESSAGES_ACKED |
|DLQ                      |DLQ                      |0              |0             |0              |0                |0              |
|ExpiryQueue              |ExpiryQueue              |0              |0             |0              |0                |0              |
|output                   |output                   |0              |1             |1              |0                |0              |
```
