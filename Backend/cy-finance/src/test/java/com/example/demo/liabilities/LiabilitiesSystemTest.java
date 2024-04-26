package com.example.demo.liabilities;

import com.example.demo.JamesSystemTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LiabilitiesSystemTest {
    private final Logger logger = LoggerFactory.getLogger(JamesSystemTest.class);

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        Response response;

        // Create non-admin user
        response = RestAssured.given().
                header("Content-Type", "application/json").
                body("{\"name\":\"Test User\", \"email\":\"testing_user@email.com\", \"password\":\"1love=Password\", \"role\":\"user\"}").
                when().
                post("/users");

        // Check status code
        assertEquals(200, response.getStatusCode());

        // Check for success return message and cookie set
        logger.info("[POST /users] Response body " + response.body().print());
        logger.info("[POST /users] Response cookie " + response.cookie("user-id"));
        try {
            assertEquals("User created", new JSONObject(response.body().print()).get("message"));
            assertEquals("testing_user@email.com", response.cookie("user-id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create non-admin user
        response = RestAssured.given().
                header("Content-Type", "application/json").
                body("{\"name\":\"Test Admin\", \"email\":\"testing_admin@email.com\", \"password\":\"2love=Password\", \"role\":\"admin\"}").
                when().
                post("/users");

        // Check status code
        assertEquals(200, response.getStatusCode());

        // Check for success return message and cookie set
        logger.info("[POST /users] Response body " + response.body().print());
        logger.info("[POST /users] Response cookie " + response.cookie("user-id"));
        try {
            assertEquals("User created", new JSONObject(response.body().print()).get("message"));
            assertEquals("testing_admin@email.com", response.cookie("user-id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Test liabilities total operations
    @Test
    public void liabilitiesTotalTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can edit their liabilities total
        - User can't edit another user's liabilities total
        - Admin can edit their liabilities total
        - Admin can edit other user's liabilities total
        - Errors with no user provided
        - Errors with invalid user provided
        - Errors with no user cookie provided
        - Errors with no amount provided
         */

        // ----------------------------------------------------------------
        // User can edit their liabilities total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/liabilitiestotal/1234");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(1234, responseObject.getInt("liabilitiesTotal"));
            assertNotEquals(1234, originalValue);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't edit another user's liabilities total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue =  responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/" + adminSessionCookie + "/liabilitiestotal/2345");

        // TODO: Uncomment after merge
        // assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            // Ensure the original value wasn't changed and the original value isn't the new value
            assertNotEquals(2345, originalValue);
            assertEquals(originalValue, responseObject.getInt("liabilitiesTotal"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can edit their liabilities total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users/" + adminSessionCookie + "/liabilitiestotal/3456");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertNotEquals(3456, originalValue);
            assertEquals(3456, responseObject.getInt("liabilitiesTotal"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can edit other user's liabilities total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/liabilitiestotal/4567");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertNotEquals(4567, originalValue);
            assertEquals(4567, responseObject.getInt("liabilitiesTotal"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Errors with no user provided
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users//liabilitiestotal/5678");

        assertEquals(400, response.getStatusCode());

        // ----------------------------------------------------------------
        // Errors with invalid user provided
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/fake_user/liabilitiestotal/5678");

        // TODO: Uncomment after merge
        // assertEquals(400, response.getStatusCode());

        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("No user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Errors with no user cookie provided
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=")
                .when()
                .put("/users/" + userSessionCookie + "/liabilitiestotal/6789");

        // TODO: Uncomment after merge
        // assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertNotEquals(6789, originalValue);
            assertEquals(originalValue, responseObject.getInt("liabilitiesTotal"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Errors with no amount provided
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("liabilitiesTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/liabilitiestotal/");

        assertEquals(404, response.getStatusCode());
    }

    // Test liabilities POST
    @Test
    public void liabilitiesPostTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can add an liabilities to themself (and it updates their net-worth automatically)
        - TODO: User can't add an liabilities to another user (and the other user's net-worth doesn't update)
        - Admin can add an liabilities to a user (and it updates their net-worth automatically)
        - Errors when given an invalid user
        - TODO: Errors with empty body
        - TODO: Errors with empty user Cookie
         */

        // ----------------------------------------------------------------
        // User can add an liabilities to themself (and it updates their net-worth automatically)
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("netWorth");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .body("{\"label\": \"car\", \"amount\": 10000}")
                .when()
                .post("/users/" + userSessionCookie + "/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(originalValue - 10000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("liabilities");
            assertEquals(10000, responseArray.getJSONObject(0).getInt("amount"));
            assertEquals("car", responseArray.getJSONObject(0).get("label"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can add an liabilities to a user (and it updates their net-worth automatically)
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("netWorth");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"label\": \"car2\", \"amount\": 10000}")
                .when()
                .post("/users/" + userSessionCookie + "/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(originalValue - 10000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("liabilities");
            assertEquals(10000, responseArray.getJSONObject(1).getInt("amount"));
            assertEquals("car2", responseArray.getJSONObject(1).get("label"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Errors when given an invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("netWorth");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"label\": \"car2\", \"amount\": 10000}")
                .when()
                .post("/users/fake_user/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to assign liabilities", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    // Test liabilities PUT
    @Test
    public void liabilitiesPutTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        int originalNetWorth;
        int liabilitiesId;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can edit their own liabilities (and it updates their net-worth automatically)
        - TODO: User can't edit another user's liabilities (and the other user's net-worth doesn't update)
        - Admin can edit another user's liabilities (and it updates the other user's net-worth automatically)
        - Errors when given an invalid user
        - TODO: Errors with empty body
        - TODO: Errors with invalid body
        - TODO: User can edit only label
        - TODO: User can edit only value
         */

        // ----------------------------------------------------------------
        // Initialize
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .body("{\"label\": \"car\", \"amount\": 10000}")
                .when()
                .post("/users/" + userSessionCookie + "/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can edit their own liabilities (and it updates their net-worth automatically)
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalNetWorth = responseObject.getInt("netWorth");
            originalValue = responseObject.getJSONArray("liabilities").getJSONObject(0).getInt("amount");
            liabilitiesId = responseObject.getJSONArray("liabilities").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .body("{\"id\": " + liabilitiesId + ", \"label\": \"car\", \"amount\": 20000}")
                .when()
                .put("/users/" + userSessionCookie + "/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities updated", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(originalNetWorth + originalValue - 20000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("liabilities");
            assertEquals(liabilitiesId, responseArray.getJSONObject(0).getInt("id"));
            assertEquals(20000, responseArray.getJSONObject(0).getInt("amount"));
            assertEquals("car", responseArray.getJSONObject(0).get("label"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can edit another user's liabilities (and it updates the other user's net-worth automatically)
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalNetWorth = responseObject.getInt("netWorth");
            originalValue = responseObject.getJSONArray("liabilities").getJSONObject(0).getInt("amount");
            liabilitiesId = responseObject.getJSONArray("liabilities").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"id\": " + liabilitiesId + ", \"label\": \"car\", \"amount\": 30000}")
                .when()
                .put("/users/" + userSessionCookie + "/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities updated", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(originalNetWorth + originalValue - 30000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("liabilities");
            assertEquals(liabilitiesId, responseArray.getJSONObject(0).getInt("id"));
            assertEquals(30000, responseArray.getJSONObject(0).getInt("amount"));
            assertEquals("car", responseArray.getJSONObject(0).get("label"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Errors when given an invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"id\": 0, \"label\": \"car\", \"amount\": 10000}")
                .when()
                .put("/users/fake_user/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to edit liabilities", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Test liabilities DELETE
    @Test
    public void liabilitiesDeleteTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        int originalNetWorth;
        int liabilitiesId;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can delete their own liabilities (And the value is automatically subtracted from their net-worth)
        - TODO: User can't delete another user's liabilities (And the value is automatically subtracted from the other user's net-worth)
        - TODO: Admin can delete their own liabilities (And the value is automatically subtracted from their net-worth)
        - TODO: Admin can delete another user's liabilities (And the value is automatically subtracted from the other user's net-worth)
        - Error when an invalid user is given
        - Error when an invalid liabilities is given
         */

        // ----------------------------------------------------------------
        // Initialize
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .body("{\"label\": \"car\", \"amount\": 10000}")
                .when()
                .post("/users/" + userSessionCookie + "/liabilities");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Error when an invalid user is given
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/fake_user/liabilities/0");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to delete liabilities", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Error when an invalid liabilities is given
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie + "/liabilities/99");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to delete liabilities", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can delete their own liabilities (And the value is automatically subtracted from their net-worth)
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalNetWorth = responseObject.getInt("netWorth");
            originalValue = responseObject.getJSONArray("liabilities").getJSONObject(0).getInt("amount");
            liabilitiesId = responseObject.getJSONArray("liabilities").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie + "/liabilities/" + liabilitiesId);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Liabilities deleted", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(originalNetWorth + originalValue, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("liabilities");
            assertEquals("[]", responseArray.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}