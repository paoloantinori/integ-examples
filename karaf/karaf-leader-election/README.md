```sh
# leader election (clustered singleton service)
cd quartz-profile
mvn clean install

$FUSE_HOME/bin/start
$FUSE_HOME/bin/client
create --clean --wait-for-provisioning --bind-address localhost --resolver manualip \
    --global-resolver manualip --manual-ip 127.0.0.1 --zookeeper-password admin
container-remove-profile host0 jboss-fuse-full

profile-import file:///quartz-profile/target/quartz-profile-1.0-0-SNAPSHOT-solution.zip
container-create-child --profile quartz root child1
container-create-child --profile quartz root child2
```
