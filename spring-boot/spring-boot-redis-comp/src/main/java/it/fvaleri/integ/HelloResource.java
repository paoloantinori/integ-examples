package it.fvaleri.integ;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class HelloResource {

    @GET
    @Path("/{name}")
    public String hello(@PathParam("name") String key) {
        return null;
    }

}
