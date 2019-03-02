package it.fvaleri.integ;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;

public class HelloRoute extends RouteBuilder {

    @Autowired
    RedisClient redisClient;

    @Override
    public void configure() throws Exception {
        from("cxfrs:bean:cxfrsRestApiEndpoint").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                final String name = exchange.getIn().getBody(String.class);
                // check if name exists in Redis Cache else push in cache
                if (null == redisClient.get(name)) {
                    String value = "Hello " + name;
                    log.info("Caching value: {}", value);
                    redisClient.setex(name, value, 30000L, TimeUnit.MILLISECONDS);
                }
                // set output in exchange body fetching data from Redis cache
                exchange.getOut().setBody(redisClient.get(name));
            }
        });
    }

}
