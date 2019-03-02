package it.fvaleri.integ;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestReply {

    private static final Logger LOG = LoggerFactory.getLogger(RequestReply.class);

    private static final String CONNECTION_URL = "tcp://localhost:61616?jms.watchTopicAdvisories=false";
    private static final String QUEUE_NAME = "TestQueue";

    private static final Queue<Throwable> errors = new ConcurrentLinkedQueue<>();
    private static final CountDownLatch serverReady = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Client());
        executor.execute(new Server());
        executor.shutdown();
        LOG.info("Errors: {}", errors);
    }

    static class Client implements Runnable {
        @Override
        public void run() {
            Connection conn = null;
            try {

                ConnectionFactory cf = new org.apache.activemq.ActiveMQConnectionFactory(CONNECTION_URL);
                conn = cf.createConnection("admin", "admin");
                conn.setExceptionListener(new ExceptionListener() {
                    @Override
                    public void onException(JMSException e) {
                        LOG.warn("(client) Failure: {}", e.getMessage());
                    }
                });
                LOG.debug("(client) Connected");
                conn.start();

                Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination requestQueue = session.createQueue(QUEUE_NAME);
                Destination tempQueue = session.createTemporaryQueue();

                MessageProducer producer = session.createProducer(requestQueue);
                MessageConsumer consumer = session.createConsumer(tempQueue);

                TextMessage request = session.createTextMessage("ping");
                request.setJMSReplyTo(tempQueue);
                request.setJMSCorrelationID("id" + System.nanoTime());

                serverReady.await(20, TimeUnit.SECONDS);
                LOG.debug("(client) Sending request: {}", request.getText());
                producer.send(request);

                LOG.debug("(client) Waiting for respone");
                TextMessage response = (TextMessage) consumer.receive();
                LOG.debug("(client) Response received: {}", response.getText());

                conn.close();

            } catch (Exception e) {
                errors.add(e);
            } finally {
                try {
                    conn.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    static class Server implements Runnable {
        @Override
        public void run() {
            Connection conn = null;
            try {

                ConnectionFactory cf = new org.apache.activemq.ActiveMQConnectionFactory(CONNECTION_URL);
                conn = cf.createConnection("admin", "admin");
                conn.setExceptionListener(new ExceptionListener() {
                    @Override
                    public void onException(JMSException e) {
                        LOG.warn("(server) Failure: {}", e.getMessage());
                    }
                });
                LOG.debug("(server) Connected");
                conn.start();

                Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination requestQueue = session.createQueue(QUEUE_NAME);

                MessageConsumer consumer = session.createConsumer(requestQueue);
                MessageProducer producer = session.createProducer(null);

                LOG.debug("(server) Waiting for request");
                serverReady.countDown();
                TextMessage request = (TextMessage) consumer.receive();
                LOG.debug("(server) Request received: {}", request.getText());

                TextMessage response = session.createTextMessage("pong");
                response.setJMSCorrelationID(request.getJMSMessageID());
                LOG.debug("(server) Sending response: {}", response.getText());
                producer.send(request.getJMSReplyTo(), response);

                conn.close();

            } catch (Exception e) {
                errors.add(e);
            } finally {
                try {
                    conn.close();
                } catch (JMSException e) {
                }
            }
        }
    }

}
