package it.fvaleri.integ;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;

import javax.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

import org.junit.Test;

public class ConsumerRateTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext ctx = super.createCamelContext();
        ctx.disableJMX();
        return ctx;
    }

    @Override
    protected int getShutdownTimeout() {
        return 10;
    }

    @Test
    public void test() throws Exception {
        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                jmsComponentSetup(context);

                // using the Aggregate EAP we achieve at most X messages/s semantic
                // with a JMS consumer, it's easier to use the Artemis consumerMaxRate parameter
                from("jms:queue:TestQueue")
                    // simulate processing
                    .delay(100)
                    // aggregate every 5 messages or 1 second interval, whichever comes first
                    .aggregate(new GroupedExchangeAggregationStrategy())
                        .constant(true).completionSize(5).completionInterval(1000L)
                    .log("Messages: ${body}")
                    .to("mock:result");
            }
        };
    }

    private void jmsComponentSetup(CamelContext ctx) {
        // set client buffer to 0 for pulling messages instead of being pushed
        final String url = "tcp://localhost:61616?consumerWindowSize=0";
        final ConnectionFactory cf = new ActiveMQConnectionFactory(url);
        final JmsComponent comp = JmsComponent.jmsComponentAutoAcknowledge(cf);
        comp.setUsername("admin");
        comp.setPassword("admin");
        // use local transactions to not lose messages
        comp.setLazyCreateTransactionManager(false);
        comp.setTransacted(true);
        ctx.addComponent("jms", comp);
    }

}
