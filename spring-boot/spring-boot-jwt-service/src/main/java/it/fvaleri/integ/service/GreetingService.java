package it.fvaleri.integ.service;

import org.apache.camel.Header;
import org.springframework.stereotype.Service;

import it.fvaleri.integ.domain.Greeting;

@Service("greetingService")
public class GreetingService {

    public Greeting getGreeting(@Header("name") String name) {
        return new Greeting("Hello " + name);
    }

}
