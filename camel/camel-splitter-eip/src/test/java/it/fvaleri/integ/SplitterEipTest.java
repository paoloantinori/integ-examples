package it.fvaleri.integ;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitterEipTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext ctx = super.createCamelContext();
        ctx.disableJMX();
        return ctx;
    }

    @Override
    protected int getShutdownTimeout() {
        return 5;
    }

    @Test
    public void test() throws Exception {
        List<String> endpoints = Arrays.asList(
            "cxf:bean:testEndpoint1", "cxf:bean:testEndpoint2",
            "cxf:bean:testEndpoint3", "cxf:bean:testEndpoint4"
        );
        MockEndpoint mock = getMockEndpoint("mock:split");
        mock.expectedBodiesReceivedInAnyOrder(endpoints);
        template.sendBody("direct:start", endpoints);
        assertMockEndpointsSatisfied();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                    .split(body()).parallelProcessing()
                        .to("direct:serviceRequest")
                    .end();

                from("direct:serviceRequest")
                    .bean(MyBackendService.class, "setHeadersAndLookupUri")
                    .log("Calling service ${body} with headers ${headers}")
                    // actual call to the backend web service
                    .to("mock:split");
            }
        };
    }

}
