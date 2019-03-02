```sh
mvn clean package
cp target/connect-smt-*.jar $PLUGINS

# add to the connectors SMT chain
"transforms": "JsonWriter",
"transforms.JsonWriter.type": "it.fvaleri.cdc.JsonWriter"
```
