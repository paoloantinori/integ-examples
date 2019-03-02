package it.fvaleri.integ.service;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import it.fvaleri.integ.domain.Greeting;

@Component
public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .apiContextPath("/api")
            .bindingMode(RestBindingMode.json)
            .component("servlet")
            .enableCORS(true)
            .apiContextPath("/doc")
            .apiProperty("api.title", "Greeting API")
            .apiProperty("api.version", "1.0");

        rest("/greet").description("Greet {name}")
            .securityDefinitions()
                .basicAuth("apiAuth", "Auth URL: http://localhost:8080/auth")
            .end()
            .get("/{name}")
                .outType(Greeting.class)
                .security("apiAuth")
                .route()
                    .routeId("greet-api")
                    .streamCaching()
                    .bean("greetingService", "getGreeting");
    }

}
