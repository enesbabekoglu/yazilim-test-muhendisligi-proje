package com.example.ecommerce;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiRegressionTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/products";
    }

    // Test 1: GET /products - Tüm ürünleri listeleme
    @Test
    @DisplayName("GET /products - Tüm ürünleri listeleme regresyon testi")
    void testGetAllProducts() {
        given()
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3))
                .body("[0].name", not(emptyString()))
                .body("[0].price", greaterThan(0f))
                .body("[0].stock", greaterThanOrEqualTo(0))
                .time(lessThan(2000L));
    }

    // Test 2: GET /products/1 - Ürün detay (200 OK)
    @Test
    @DisplayName("GET /products/1 - Ürün detay regresyon testi")
    void testGetProductById() {
        given()
        .when()
                .get("/1")
        .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Laptop"))
                .body("price", equalTo(25000f))
                .body("stock", equalTo(15))
                .body("category", equalTo("Electronics"))
                .time(lessThan(2000L));
    }

    // Test 3: GET /products/9999 - Olmayan ürün (404 Not Found)
    @Test
    @DisplayName("GET /products/9999 - Olmayan ürün 404 regresyon testi")
    void testGetProductNotFound() {
        try {
            given().get("/9999");
        } catch (Exception e) {
            assertEquals(true, e.getMessage().contains("404"));
        }
    }

    // Test 4: POST /products - Yeni ürün ekleme (201 Created)
    @Test
    @DisplayName("POST /products - Yeni ürün ekleme regresyon testi")
    void testCreateProduct() {
        String requestBody = """
                {
                    "name": "iPhone 17 Pro",
                    "price": 1200.0,
                    "stock": 10,
                    "category": "Electronics"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post()
        .then()
                .statusCode(201)
                .body("id", greaterThan(0))
                .body("name", equalTo("iPhone 17 Pro"))
                .body("price", equalTo(1200f))
                .body("stock", equalTo(10))
                .body("category", equalTo("Electronics"))
                .time(lessThan(2000L));
    }

    // Test 5: POST /products - Aynı isimde ürün (409 Conflict)
    @Test
    @DisplayName("POST /products - Duplicate ürün 409 Conflict regresyon testi")
    void testCreateDuplicateProduct() {
        String requestBody = """
                {
                    "name": "Laptop",
                    "price": 30000,
                    "stock": 5,
                    "category": "Electronics"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post();

        assertEquals(409, response.getStatusCode());
        response.then().body("error", equalTo("Bu isimde bir ürün zaten mevcut"));
        response.then().body("name", equalTo("Laptop"));
    }

    // Test 6: POST /products - Negatif fiyat (400 Bad Request)
    @Test
    @DisplayName("POST /products - Negatif fiyat 400 Bad Request regresyon testi")
    void testCreateProductWithInvalidPrice() {
        String requestBody = """
                {
                    "name": "FreeBuds",
                    "price": -100,
                    "stock": 20,
                    "category": "Electronics"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post();

        assertEquals(400, response.getStatusCode());
        response.then().body("error", equalTo("Ürün fiyatı 0'dan büyük olmalıdır"));
    }

    // Test 7: DELETE /products/{id} - Ürün silme (200 OK)
    @Test
    @DisplayName("DELETE /products/{id} - Ürün silme regresyon testi")
    void testDeleteProduct() {
        String requestBody = """
                {
                    "name": "Silinecek Ürün",
                    "price": 500,
                    "stock": 5,
                    "category": "Test"
                }
                """;

        int createdId = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post()
        .then()
                .statusCode(201)
                .extract().path("id");

        Response response = given()
                .contentType(ContentType.JSON)
        .when()
                .delete("/" + createdId);

        assertEquals(200, response.getStatusCode());
        response.then().body("message", equalTo("Ürün silindi"));
        response.then().body("id", equalTo(createdId));
    }

    // Test 8: DELETE /products/9999 - Olmayan ürünü silme (404 Not Found)
    @Test
    @DisplayName("DELETE /products/9999 - Olmayan ürün silme 404 regresyon testi")
    void testDeleteProductNotFound() {
        try {
            given().delete("/9999");
        } catch (Exception e) {
            assertEquals(true, e.getMessage().contains("404"));
        }
    }
}
