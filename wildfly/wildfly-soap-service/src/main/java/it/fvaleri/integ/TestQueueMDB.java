package it.fvaleri.integ;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.ejb3.annotation.ResourceAdapter;

// default: activemq-ra
@ResourceAdapter(value = "activemq-ra")
@MessageDriven(name = "TestQueueMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "TestQueue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    // max sessions (default 15, must be < max-pool-size): maxSessions (EAP6), maxSession (EAP7)
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "15")
})
public class TestQueueMDB implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestQueueMDB.class);

    @Override
    public void onMessage(Message rcvMessage) {
        try {
            if (rcvMessage instanceof TextMessage) {
                TextMessage msg = (TextMessage) rcvMessage;
                LOG.info("Got message: {}", msg.getText());
            } else {
                LOG.warn("Got message of wrong type: {}", rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}
