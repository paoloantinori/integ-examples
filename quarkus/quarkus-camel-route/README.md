```sh
# dev mode run with fast hot-reload
mvn compile quarkus:dev -Ddebug=false

# native
mvn clean package -Pnative

# openshift
NAME="quarkus-camel-route"
oc new-build --binary --name=$NAME -l app=$NAME
oc patch bc/$NAME -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile"}}}}'
oc start-build $NAME --from-dir=. --follow

oc new-app --image-stream=$NAME:latest
oc scale dc $NAME --replicas=100
#oc delete all -l app=$NAME
```
