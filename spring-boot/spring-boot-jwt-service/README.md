```sh
# in this example ResourceServer and AuthorizationServer are the same
mvn clean spring-boot:run

# access public resources
curl -H "Content-Type: application/json" http://localhost:8080/api/doc

# use basic HTTP authentication to get the access_token (pwd grant type)
curl -u fvaleri:secret http://localhost:8080/auth

# access protected resource using the access_token
curl -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/greet/Fede
```
