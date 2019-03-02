package it.fvaleri.integ;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.net.UnknownHostException;

@QuarkusTest
public class FruitResourceTest {

    @Test
    public void testAllFruits() throws UnknownHostException {
        given()
          .when().get("/fruits")
          .then()
             .statusCode(200)
             .body(is("[{\"id\":3,\"name\":\"Apple\"},"
                + "{\"id\":1,\"name\":\"Orange\"},"
                + "{\"id\":2,\"name\":\"Pear\"}]"));
    }

    @Test
    public void testSingleFruit() {
        given()
          .pathParam("id", "1")
          .when().get("/fruits/{id}")
          .then()
            .statusCode(200)
            .body(is("{\"id\":1,\"name\":\"Orange\"}"));
    }

}
