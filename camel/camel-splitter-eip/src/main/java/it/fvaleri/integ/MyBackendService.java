package it.fvaleri.integ;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.apache.cxf.headers.Header.Direction;
import org.apache.cxf.jaxb.JAXBDataBinding;

@SuppressWarnings("unchecked")
public class MyBackendService {

    public static void setHeadersAndLookupUri(Exchange exchange) throws JAXBException {
        String endpoint = exchange.getIn().getBody().toString();
        if (endpoint.contains("testEndpoint2")) {
            List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
            if (soapHeaders == null) {
                soapHeaders = new ArrayList<SoapHeader>();
            }

            ServiceAuthHeader auth = new ServiceAuthHeader();
            auth.setUsername("username");
            auth.setPassword("password");
            SoapHeader header = new SoapHeader(new QName("namespace", "name"), auth,
                    new JAXBDataBinding(ServiceAuthHeader.class));
            header.setDirection(Direction.DIRECTION_IN);
            soapHeaders.add(header);

            exchange.getIn().setHeader(Header.HEADER_LIST, soapHeaders);
            // other settings here...
        }
    }

}
