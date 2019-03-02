# stage1: build with maven builder image with native capabilities
FROM quay.io/quarkus/centos-quarkus-maven:19.3.1-java8 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
USER root
RUN chown -R quarkus /usr/src/app
USER quarkus
RUN mvn -f /usr/src/app/pom.xml -Pnative clean package

# stage2: create the docker final image
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.1
WORKDIR /work/
COPY --from=build /usr/src/app/target/*-runner /work/application
RUN chmod -R 775 /work
EXPOSE 8080
CMD ["./application/quarkus-camel-route-1.0.0-SNAPSHOT-runner", "-Dquarkus.http.host=0.0.0.0"]
