```sh
mvn clean install

# standalone
install -s mvn:it.fvaleri.integ/karaf-tick-tock-tick/1.0.0-SNAPSHOT
install -s mvn:it.fvaleri.integ/karaf-tick-tock-tock/1.0.0-SNAPSHOT

config:edit it.fvaleri.integ.tick
config:property-set delay 1000
config:update

# fabric
profile-create --parents default my-profile
profile-edit -b mvn:it.fvaleri.integ/karaf-tick-tock-tick/1.0.0-SNAPSHOT my-profile
profile-edit -b mvn:it.fvaleri.integ/karaf-tick-tock-tock/1.0.0-SNAPSHOT my-profile
profile-edit -p it.fvaleri.integ.tick/delay=1000 my-profile

profile-list
profile-display my-profile

container-add-profile child1 my-profile
container-connect child1
```
