package it.fvaleri.integ;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) {
        Connection conn = null;
        try {

            conn = JMSUtil.openConnection();
            //conn.setClientID("id0");

            // ack mode ignored when using transacted session
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = JMSUtil.createDestination(session);

            MessageConsumer consumer = session.createConsumer(dest);
            //MessageConsumer consumer = session.createDurableSubscriber(topic, "sub0");
            //consumer.setMessageListener(new MyMessageListener()); // async

            while (true) {
                TextMessage message = (TextMessage) consumer.receive(60_000);
                if (message != null) {
                    LOG.info("Received message {}", message.getJMSMessageID());
                }
                Thread.sleep(PropertiesUtil.getDelayMs());
            }

        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        } finally {
            JMSUtil.closeConnection(conn);
        }
    }

    class MyMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            // message acked only when this method returns
            try {
                LOG.info("Received message {}", message.getJMSMessageID());
            } catch (JMSException e) {
                LOG.error("Message error: {}", e.getMessage());
            }
        }
    }

}
