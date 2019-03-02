package it.fvaleri.integ;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Producer extends JMSClient {

    public static void main(String[] args) {
        new Producer().run();
        System.exit(0);
    }

    @Override
    public void run() {
        Connection conn = null;
        try {

            final Properties props = getConfiguration();
            conn = openConnection(props);

            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = createDestination(props, session);

            MessageProducer producer = session.createProducer(dest);
            if (props.get("ttl") != "0") {
                producer.setTimeToLive((int) props.get("ttl"));
            }
            TextMessage message = session.createTextMessage("test");
            for (int i = 0; i < (int) props.get("nom"); i++) {

                startStats();
                producer.send(message); // sync by default; async with TXs and non-pers
                LOG.info("Sent message {}", message.getJMSMessageID());
                printStats();

                Thread.sleep((int) props.get("dms"));
            }

        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        } finally {
            closeConnection(conn);
        }
    }

}
