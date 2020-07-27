package it.fvaleri.integ;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JMSUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JMSUtil.class);

    private JMSUtil() {
    }

    public static Connection openConnection() throws JMSException {
        ConnectionFactory cf = null;
        final String url = PropertiesUtil.getConnUrl();
        switch (PropertiesUtil.getConnFactory()) {
            case "activemq":
                cf = new org.apache.activemq.ActiveMQConnectionFactory(url);
                break;
            case "artemis":
                cf = new org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory(url);
                break;
            case "qpid":
                cf = new org.apache.qpid.jms.JmsConnectionFactory(url);
                break;
            default:
                throw new RuntimeException("Unknown ConnectionFactory");
        }
        Connection conn = cf.createConnection(PropertiesUtil.getUser(), PropertiesUtil.getPass());
        LOG.info("Client connected");
        conn.setExceptionListener(new MyExceptionListener());
        conn.start();
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (JMSException e) {
                LOG.error("Error on connection close: {}", e.getMessage());
            }
        }
    }

    public static Destination createDestination(Session session) throws JMSException {
        return PropertiesUtil.getTopic() != null
                ? session.createTopic(PropertiesUtil.getTopic())
                : session.createQueue(PropertiesUtil.getQueue());
    }

    private static class MyExceptionListener implements ExceptionListener {
        @Override
        public void onException(JMSException e) {
            LOG.error("Unexpected error", e.getMessage());
        }
    }

}
