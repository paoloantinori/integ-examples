package it.fvaleri.integ;

import java.text.DecimalFormat;
import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(MsgProcessor.class);
    private static DecimalFormat FORMATTER = new DecimalFormat("#0.000");
    private static Long MSG_COUNTER = 0L;
    private static Long START_NS = 0L;

    private int statsEveryNMessages = 1_000;
    private boolean runComplexProcessing = false;
    private long throwErrorAtMessage = -1;

    public void process(Exchange exchange) throws Exception {
        synchronized (MSG_COUNTER) {
            if (MSG_COUNTER == 0) {
                START_NS = System.nanoTime();
                LOG.trace("Taking startup time");
            }
        }

        Message message = exchange.getIn();
        synchronized (MSG_COUNTER) {
            MSG_COUNTER++;
            LOG.trace("Message {} received, RD:{}, {}",
                MSG_COUNTER, message.getHeader("JMSRedelivered"), message.getHeader("JMSMessageID"));
        }

        // print statistics every N messages
        synchronized (MSG_COUNTER) {
            if (MSG_COUNTER == statsEveryNMessages) {
                double durationNs = (System.nanoTime()-START_NS);
                double durationMs = durationNs/1_000_000f;
                double durationSec = (System.nanoTime()-START_NS)/1_000_000_000f;
                long throughput = durationSec>0 ? (int)(MSG_COUNTER/durationSec) : MSG_COUNTER;
                LOG.info("Stats: processed {} msgs in {} ms ({} msgs/s)",
                    statsEveryNMessages, FORMATTER.format(durationMs), throughput);
                MSG_COUNTER = 0L;
            }
        }

        // raise exception at messages X
        if (throwErrorAtMessage > 0 && MSG_COUNTER % throwErrorAtMessage == 0) {
            throw new RuntimeException("Forced exception");
        }

        // message processing simulation
        if (runComplexProcessing) {
            int randomSleep = new Random().nextInt(600-300) + 300;
            LOG.trace("Processing message {} for {} ms", MSG_COUNTER, randomSleep);
            Thread.sleep(randomSleep);
        } else {
            LOG.trace("Processing message {}", MSG_COUNTER);
        }

        LOG.trace("Message {} done", MSG_COUNTER);
    }

    public long getStatsEveryNMessages() {
        return statsEveryNMessages;
    }

    public void setStatsEveryNMessages(int statsEveryNMessages) {
        this.statsEveryNMessages = statsEveryNMessages;
    }

    public boolean isRunComplexProcessing() {
        return runComplexProcessing;
    }

    public void setRunComplexProcessing(boolean runComplexProcessing) {
        this.runComplexProcessing = runComplexProcessing;
    }

    public long getThrowErrorAtMessage() {
        return throwErrorAtMessage;
    }

    public void setThrowErrorAtMessage(long throwErrorAtMessage) {
        this.throwErrorAtMessage = throwErrorAtMessage;
    }

}
