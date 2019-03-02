package it.fvaleri.integ;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JMSClient implements Runnable {

    protected static final Logger LOG = LoggerFactory.getLogger(JMSClient.class);
    private static DecimalFormat FORMATTER = new DecimalFormat("#0.000");
    private static AtomicLong MSG_COUNTER = new AtomicLong(0);
    private static long STATS_EVERY_N_MSGS = 1000L;
    private static long START_NS = 0L;

    protected Properties getConfiguration() {
        Properties props = new Properties();
        props.put("cf", System.getProperty("cf", "activemq"));
        props.put("url", System.getProperty("url", "tcp://localhost:61616"));
        props.put("user", System.getProperty("user", "admin"));
        props.put("pass", System.getProperty("pass", "admin"));
        props.put("queue", System.getProperty("queue", "TestQueue"));
        props.put("topic", System.getProperty("topic", ""));
        props.put("ttl", Integer.parseInt(System.getProperty("ttl", "0")));
        props.put("nom", Integer.parseInt(System.getProperty("nom", "1")));
        props.put("dms", Integer.parseInt(System.getProperty("dms", "0")));
        return props;
    }

    protected Connection openConnection(Properties props) throws JMSException {
        ConnectionFactory cf = null;
        String url = props.getProperty("url");
        switch (props.getProperty("cf")) {
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
        Connection conn = cf.createConnection(props.getProperty("user"), props.getProperty("pass"));
        LOG.info("Client connected");
        conn.setExceptionListener(new MyExceptionListener());
        conn.start();
        return conn;
    }

    protected void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (JMSException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    protected Destination createDestination(final Properties props, Session session) throws JMSException {
        return !props.getProperty("topic").isEmpty()
                ? session.createTopic(props.getProperty("topic"))
                : session.createQueue(props.getProperty("queue"));
    }

    protected void startStats() {
        if (MSG_COUNTER.get() == 0) {
            START_NS = System.nanoTime();
            LOG.debug("Taking stats start time");
        }
    }

    protected void printStats() {
        long count = MSG_COUNTER.incrementAndGet();
        if (count == STATS_EVERY_N_MSGS) {
            double durationNs = (System.nanoTime()-START_NS);
            double durationMs = durationNs/1_000_000f;
            double durationSec = (System.nanoTime()-START_NS)/1_000_000_000f;
            long throughput = durationSec>0 ? (int)(count/durationSec) : count;
            LOG.debug("Stats: processed {} msgs in {} ms ({} msgs/s)",
                STATS_EVERY_N_MSGS, FORMATTER.format(durationMs), throughput);
            MSG_COUNTER.set(0);
        }
    }

    private class MyExceptionListener implements ExceptionListener {
        @Override
        public void onException(JMSException e) {
            LOG.error("Unexpected error", e.getMessage());
        }
    }

}
