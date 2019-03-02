package it.fvaleri.integ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerA1 {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerA1.class);

    @JmsListener(destination = "foo.example", id = "ConsumerA1", subscription = "foo.example", containerFactory = "jmsListenerContainerFactory")
    public void processMsg(String message) {
        LOG.info("Got {}", message);
    }

}
