import java.util.Random;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

/**
 * kamel run Routes.java --property-file application.properties --dev
 */
public class Routes extends RouteBuilder {

    private Random random = new Random();

    @PropertyInject("priority-marker")
    private String priorityMarker;

    @Override
    public void configure() throws Exception {

        from("timer:foo?period=2s")
            .id("generator")
            .bean(this, "randomItem({{items}})")
            .choice().when().simple("${body.startsWith('{{priority-marker}}')}")
                .transform().body(String.class, item -> item.substring(priorityMarker.length()))
                .to("direct:priorityQueue")
            .otherwise()
                .to("direct:standardQueue");

        from("direct:standardQueue")
            .id("standard")
            .log("Standard item: ${body}");

        from("direct:priorityQueue")
            .id("priority")
            .log("!!Priority item: ${body}");

    }

    public String randomItem(String items) {
        if (items == null || items.equals("")) {
            return "[no items configured]";
        }
        String[] list = items.split("\\s");
        return list[random.nextInt(list.length)];
    }

}
