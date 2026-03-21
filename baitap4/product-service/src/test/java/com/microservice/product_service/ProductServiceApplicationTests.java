package com.microservice.product_service;

import com.microservice.product_service.model.Product;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @Test
    void shouldCreateProduct() {
        String requestBody = """
                {
                    "name": "iPhone 17",
                    "description": "iPhone 15 is a smartphone Icon Apple",
                    "price": 30000
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/product")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("iPhone 17"))
                .body("description", equalTo("iPhone 15 is a smartphone Icon Apple"))
                .body("price", equalTo(30000));
    }

    @Test
    void shouldGetProduct() {
        // Tạo product trước
        Product product = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Test Product",
                            "description": "Desc",
                            "price": 1000
                        }
                        """)
                .post("/api/product")
                .then()
                .extract()
                .as(Product.class);

        RestAssured.given()
                .get("/api/product/" + product.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo("Test Product"))
                .body("description", equalTo("Desc"))
                .body("price", equalTo(1000));
    }
}
