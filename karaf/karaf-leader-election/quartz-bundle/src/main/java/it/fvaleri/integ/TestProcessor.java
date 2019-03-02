package it.fvaleri.integ;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestProcessor implements Processor  {

    private static final Logger LOG = LoggerFactory.getLogger(TestProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LOG.info("*** Time is: {} ", dateFormat.format(new Date()));
    }

}
