```sh
# initial setup
oc login -u kubeadmin -p 8rynV-SeYLc-h8Ij7-YPYcz https://api.crc.testing:6443
oc new-project camel-k
oc adm policy add-role-to-user admin developer
kamel install --cluster-setup
oc login -u developer -p developer https://api.crc.testing:6443

# integration run
kamel run src/Routes.java --property-file src/application.properties --dev
kamel run src/Routes.java --property-file src/application.properties --trait quarkus.enabled=true --dev
oc get it
```
