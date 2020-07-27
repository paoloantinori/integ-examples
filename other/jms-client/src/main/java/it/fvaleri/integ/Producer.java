package it.fvaleri.integ;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        Connection conn = null;
        try {

            conn = JMSUtil.openConnection();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = JMSUtil.createDestination(session);

            MessageProducer producer = session.createProducer(dest);
            producer.setTimeToLive(PropertiesUtil.getMessageTTL());
            TextMessage message = session.createTextMessage("test");

            while (true) {
                producer.send(message); // sync by default; async with TXs and non-pers
                LOG.info("Sent message {}", message.getJMSMessageID());
                Thread.sleep(PropertiesUtil.getDelayMs());
            }

        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        } finally {
            JMSUtil.closeConnection(conn);
        }
    }

}
