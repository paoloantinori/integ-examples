package it.fvaleri.integ;

import org.apache.camel.main.Main;

public class Application {

    public static void main(final String[] args) throws Exception {
        Main main = new Main(Configuration.class);
        main.configure().addRoutesBuilder(new Routes());
        main.run(args);
    }

}
