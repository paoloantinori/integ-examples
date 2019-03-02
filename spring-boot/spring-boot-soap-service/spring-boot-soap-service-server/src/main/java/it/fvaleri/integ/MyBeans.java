package it.fvaleri.integ;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.LoggingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.SimplePortType;

@Configuration
public class MyBeans {

    @Autowired
    private Bus bus;

    @Bean
    public CxfEndpoint simpleEndpoint() {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setServiceName(new QName("http://example.com", "simple"));
        endpoint.setServiceClass(SimplePortType.class);

        // using features we can avoid setting logging interceptor manually on CxfEndpoint
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        bus.setFeatures(new ArrayList<>(Arrays.asList(loggingFeature)));
        endpoint.setBus(bus);
        return endpoint;
    }

}
