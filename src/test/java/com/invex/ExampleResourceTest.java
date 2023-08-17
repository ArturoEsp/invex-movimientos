package com.invex;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class ExampleResourceTest {

	@Test
    public void testGeneratetoken() {
        given()
                .when().get("/efectivo/generatetoken")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

}