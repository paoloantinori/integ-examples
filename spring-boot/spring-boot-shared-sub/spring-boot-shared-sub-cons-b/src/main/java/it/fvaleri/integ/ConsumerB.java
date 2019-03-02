package it.fvaleri.integ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerB {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerB.class);

    @JmsListener(destination = "foo.example", id = "ConsumerB", subscription = "foo.example", containerFactory = "jmsListenerContainerFactory")
    public void processMsg(String message) {
        LOG.info("Got {}", message);
    }

}
