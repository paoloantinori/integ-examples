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

public class Consumer extends JMSClient {

    public static void main(String[] args) {
        new Consumer().run();
        System.exit(0);
    }

    @Override
    public void run() {
        Connection conn = null;
        try {

            final Properties props = getConfiguration();
            conn = openConnection(props);
            //conn.setClientID("id0");

            // ack mode ignored when using transacted session
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = createDestination(props, session);

            MessageConsumer consumer = session.createConsumer(dest);
            //MessageConsumer consumer = session.createDurableSubscriber(topic, "sub0");
            //consumer.setMessageListener(new MyMessageListener()); // async

            for (int i = 0; i < (int) props.get("nom"); i++) {
                TextMessage message = (TextMessage) consumer.receive(60_000);
                startStats();

                if (message != null) {
                    LOG.info("Received message {}", message.getJMSMessageID());
                    printStats();
                }

                Thread.sleep((int) props.get("dms"));
            }

        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        } finally {
            closeConnection(conn);
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
