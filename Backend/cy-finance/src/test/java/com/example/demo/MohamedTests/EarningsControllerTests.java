package com.example.demo.MohamedTests;

import com.example.demo.CyFinanceApplication;
import com.example.demo.earnings.Earnings;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertTrue;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.Assert.*;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class contains integration tests for the EarningsController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CyFinanceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EarningsControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(EarningsControllerTests.class);

    @LocalServerPort
    private int port;

    private String userId;
    private String earningsId;

    @Before
    public void setUp() {
        // Initialize RestAssured with the randomly assigned port for testing
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @After
    public void tearDown() {
        if (userId != null) {
            RestAssured.given()
                    .when()
                    .delete("/users/" + userId);
        }
        if (earningsId != null) {
            RestAssured.given()
                    .when()
                    .delete("/earnings/" + earningsId);
        }
    }

    /**
     * Test case to verify creating an earnings record with valid data.
     * It checks for successful creation and ensures the ID is not null.
     */
    @Test
    public void testCreateEarningsWithValidData() {
        Response response = createEarnings(5000, 3000);
        assertEquals("Expected HTTP status 200 on successful creation", 200, response.getStatusCode());
        earningsId = response.body().jsonPath().getString("id");
        assertNotNull("Earnings ID should not be null after creation", earningsId);
        logger.info("Earnings created with ID: {}", earningsId);
    }

    /**
     * Test case to verify creating an earnings record with both incomes set to zero.
     * It checks if zero income is treated as valid.
     */
    @Test
    public void testCreateEarningsWithZeroIncome() {
        Response response = createEarnings(0, 0);
        assertEquals("Expected HTTP status 200 if zero income is treated as valid", 200, response.getStatusCode());
    }

    /**
     * Test case to verify the retrieval of earnings after creation to validate data integrity.
     */
    @Test
    public void testRetrieveEarnings() {
        testCreateEarningsWithValidData();
        Response response = RestAssured.given()
                .when()
                .get("/earnings/" + earningsId);

        assertEquals("Expected HTTP status 200 on successful retrieval", 200, response.getStatusCode());
        logger.info("Retrieved Earnings: {}", response.body().asString());
    }

    /**
     * Test case to verify updating existing earnings.
     */
    @Test
    public void testUpdateEarnings() {
        testCreateEarningsWithValidData();
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"primaryMonthlyIncome\": 5500, \"secondaryMonthlyIncome\": 3500}")
                .when()
                .put("/earnings/" + earningsId);

        assertEquals("Expected HTTP status 200 on successful update", 200, response.getStatusCode());
        logger.info("Updated Earnings: {}", response.body().asString());
    }

    /**
     * Test case to verify updating earnings with non-numeric values.
     * It expects a bad request with an appropriate error message.
     */
    @Test
    public void testUpdateEarningsWithNonNumericValues() {
        testCreateEarningsWithValidData();

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"primaryMonthlyIncome\": \"not a number\", \"secondaryMonthlyIncome\": \"none\"}")
                .when()
                .put("/earnings/" + earningsId);

        assertEquals("Expected HTTP status 400 for bad request with non-numeric values", 400, response.getStatusCode());

        // Check if the error message is null
        String actualMessage = response.getBody().asString();
        assertNotNull("Error message should not be null", actualMessage);

        // Check if the error message indicates a bad request
        assertTrue("Error message should indicate a bad request",
                actualMessage.contains("Bad Request"));
    }

    /**
     * Test case to verify deleting earnings and ensuring they are no longer accessible.
     */
    @Test
    public void testDeleteEarnings() {
        testCreateEarningsWithValidData();
        Response response = RestAssured.given()
                .when()
                .delete("/earnings/" + earningsId);

        assertEquals("Expected HTTP status 200 on successful deletion", 200, response.getStatusCode());
        logger.info("Earnings deleted.");

        response = RestAssured.given()
                .when()
                .get("/earnings/" + earningsId);
        assertEquals("Expected HTTP status 404 when trying to retrieve deleted earnings", 404, response.getStatusCode());
        logger.info("Earnings retrieval after delete returned: {}", response.getStatusCode());
    }

    /**
     * Helper method to create earnings using specified income values.
     * @param primaryIncome The primary monthly income value.
     * @param secondaryIncome The secondary monthly income value.
     * @return The response object containing the result of the request.
     */
    private Response createEarnings(int primaryIncome, int secondaryIncome) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"primaryMonthlyIncome\": %d, \"secondaryMonthlyIncome\": %d}", primaryIncome, secondaryIncome))
                .when()
                .post("/earnings");
    }

    /**
     * Test case for creating earnings with both incomes set to negative values.
     * It expects a bad request due to negative income values.
     */
    @Test
    public void testCreateEarningsWithNegativeIncome() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"primaryMonthlyIncome\": -5000, \"secondaryMonthlyIncome\": -3000}")
                .when()
                .post("/earnings");
        assertEquals("Expected HTTP status 400 for bad request due to negative income values", 400, response.getStatusCode());
    }

    /**
     * Test case for creating earnings with invalid data types (e.g., string instead of numbers).
     * It expects a bad request due to invalid data types.
     */
    @Test
    public void testCreateEarningsWithInvalidDataTypes() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"primaryMonthlyIncome\": \"five thousand\", \"secondaryMonthlyIncome\": \"three thousand\"}")
                .when()
                .post("/earnings");
        assertEquals("Expected HTTP status 400 for bad request due to invalid data types", 400, response.getStatusCode());
    }

    /**
     * Test case for creating earnings with extra unrecognized fields in the JSON.
     * It expects a successful creation even with extra fields.
     */
    @Test
    public void testCreateEarningsWithExtraFields() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"primaryMonthlyIncome\": 5000, \"secondaryMonthlyIncome\": 3000, \"extraField\": \"extraValue\"}")
                .when()
                .post("/earnings");
        assertEquals("Expected HTTP status 200 OK even with extra fields", 200, response.getStatusCode());
    }

    @Test
    public void attachEarningsToUser() {
        // Create user JSON
        String userJson = "{\"email\": \"test@example.com\", \"name\": \"Test User\", \"password\": \"pass123\", \"role\": \"user\"}";

        // Perform the POST request to create user
        Response userResponse = RestAssured.given()
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        // Check if user creation was successful
        if (userResponse.getStatusCode() != 201) {
            throw new IllegalStateException("Failed to create user: " + userResponse.getBody().asString());
        }

        // Extract the user ID from the response
        String userId = userResponse.jsonPath().getString("id");

        // Create earnings JSON
        String earningsJson = "{\"primaryMonthlyIncome\": 1000, \"secondaryMonthlyIncome\": 500}";

        // Perform the POST request to create earnings
        Response earningsResponse = RestAssured.given()
                .contentType("application/json")
                .body(earningsJson)
                .post("/earnings");

        // Check if earnings creation was successful
        if (earningsResponse.getStatusCode() != 200) {
            throw new IllegalStateException("Failed to create earnings: " + earningsResponse.getBody().asString());
        }

        // Extract the earnings ID from the response
        String earningsId = earningsResponse.jsonPath().getString("id");

        // Check if earnings ID is null
        if (earningsId == null) {
            throw new IllegalStateException("Earnings ID is null after creation");
        }

        // Perform the POST request to attach earnings to the user
        String attachUrl = "/users/" + userId + "/earnings/" + earningsId;
        Response attachResponse = RestAssured.given()
                .contentType("application/json")
                .post(attachUrl);

        // Check if earnings attachment was successful
        if (attachResponse.getStatusCode() != 201) {
            throw new IllegalStateException("Failed to attach earnings: " + attachResponse.getBody().asString());
        }
    }


}
