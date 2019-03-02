```sh
# cluster setup
oc login -u kubeadmin -p 8rynV-SeYLc-h8Ij7-YPYcz https://api.crc.testing:6443
oc apply -f my-cluster.yml
oc apply -f my-topic.yml
oc get pods
oc get kafkatopics

# get bootstrap hostname for external access (port 9094)
oc get routes | grep bootstrap

# create client trustore by importing the cluster CA certificate
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- > /tmp/ca.crt
keytool -import -alias root -file /tmp/ca.crt -keystore /tmp/truststore.jks -storepass secret -noprompt

mvn clean spring-boot:run #-Djavax.net.debug=ssl
```
