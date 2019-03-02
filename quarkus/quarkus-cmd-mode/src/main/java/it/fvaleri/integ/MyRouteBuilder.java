package it.fvaleri.integ;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MyRouteBuilder extends RouteBuilder {

    @ConfigProperty(name = "greeted.subject", defaultValue = "stranger")
    String subject;

    @Override
    public void configure() {
        from("timer:foo?delay=-1&repeatCount=1")
            .setBody().constant("Hello " + subject)
            .to("log:foo");
    }

}
