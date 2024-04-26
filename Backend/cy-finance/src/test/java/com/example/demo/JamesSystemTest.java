package com.example.demo;

import com.example.demo.users.UserController;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JamesSystemTest {
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

    // Test non-admin access to CRUD user calls
    @Test
    public void nonAdminRequestToUsers() {
        Response response;
        JSONArray responseObjectArray;
        JSONObject responseObject;
        String sessionCookie = "testing_user@email.com";

        /*
        - User can't get all users
        - User can get their own details
        - User can't get details of other users
        - User can edit their own information
        - User can't edit other user's information
        - User can't delete other users
        - User can delete themself
         */

        // ----------------------------------------------------------------

        // Try to get all users (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .get("/users");

        // Check for forbidden code
        // TODO: Uncomment after merge
        // assertEquals(403, response.getStatusCode());

        // Check that no users were returned
        logger.info("[GET /users] Response: " + response.print());
        assertEquals("", response.body().print());

        // ----------------------------------------------------------------

        // Get your own user (success)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .get("/users/" + sessionCookie);

        // Check for response code
        assertEquals(200, response.getStatusCode());

        logger.info("[GET /users/{id}] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Test User", responseObject.get("name"));
            assertEquals("testing_user@email.com", responseObject.get("email"));
            assertEquals("user", responseObject.get("role"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------

        // Get your admin user (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .get("/users/testing_admin@email.com");

        // Check for response code (uncomment when merged with proper code)
        // TODO: Uncomment after merge
        // assertEquals(403, response.getStatusCode());

        logger.info("[GET /users/{id}] Response: " + response.print());
        assertEquals("", response.body().print());

        // ----------------------------------------------------------------

        // Edit your user (success)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .body("{\"email\": \"testing_user@email.com\", \"name\": \"New Name\"}")
                .when()
                .put("/users");

        // Check for response code
        assertEquals(200, response.getStatusCode());

        logger.info("[PUT /users] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Get your own user
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .get("/users/" + sessionCookie);

        // Check for response code
        assertEquals(200, response.getStatusCode());

        logger.info("[GET /users/{id}] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("New Name", responseObject.get("name"));
            assertEquals("testing_user@email.com", responseObject.get("email"));
            assertEquals("user", responseObject.get("role"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------

        // Edit admin user (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .body("{\"email\": \"testing_admin@email.com\", \"name\": \"New Name\"}")
                .when()
                .put("/users");

        // Check for response code
        // TODO: Uncomment when merged with code
        // assertEquals(403, response.getStatusCode());

        logger.info("[PUT /users] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Get admin user
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=testing_admin@email.com")
                .when()
                .get("/users/testing_admin@email.com");

        assertEquals(200, response.getStatusCode());

        logger.info("[GET /users/{id}] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Test Admin", responseObject.get("name"));
            assertEquals("testing_admin@email.com", responseObject.get("email"));
            assertEquals("admin", responseObject.get("role"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------

        // Delete admin (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .delete("/users/testing_admin@email.com");

        // TODO: uncomment after merge
        // assertEquals(403, response.getStatusCode());

        logger.info("[DELETE /users] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------

        // Delete user (success)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .delete("/users/testing_user@email.com");

        assertEquals(200, response.getStatusCode());

        logger.info("[DELETE /users] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User deleted", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Test assets total operations
    @Test
    public void assetsTotalTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can edit their asset total
        - User can't edit another user's asset total
        - Admin can edit their asset total
        - Admin can edit other user's asset total
        - Errors with no user provided
        - Errors with invalid user provided
        - Errors with no user cookie provided
        - Errors with no amount provided
         */

        // ----------------------------------------------------------------
        // User can edit their asset total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/assettotal/1234");

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
            assertEquals(1234, responseObject.getInt("assetsTotal"));
            assertNotEquals(1234, originalValue);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't edit another user's asset total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue =  responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/" + adminSessionCookie + "/assettotal/2345");

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
            assertEquals(originalValue, responseObject.getInt("assetsTotal"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can edit their asset total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users/" + adminSessionCookie + "/assettotal/3456");

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
            assertEquals(3456, responseObject.getInt("assetsTotal"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can edit other user's asset total
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/assettotal/4567");

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
            assertEquals(4567, responseObject.getInt("assetsTotal"));
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
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users//assettotal/5678");

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
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/fake_user/assettotal/5678");

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
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=")
                .when()
                .put("/users/" + userSessionCookie + "/assettotal/6789");

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
            assertEquals(originalValue, responseObject.getInt("assetsTotal"));
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
            originalValue = responseObject.getInt("assetsTotal");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/assettotal/");

        assertEquals(404, response.getStatusCode());
    }

    // Test assets POST
    @Test
    public void assetsPostTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can add an asset to themself (and it updates their net-worth automatically)
        - TODO: User can't add an asset to another user (and the other user's net-worth doesn't update)
        - Admin can add an asset to a user (and it updates their net-worth automatically)
        - Errors when given an invalid user
        - TODO: Errors with empty body
        - TODO: Errors with empty user Cookie
         */

        // ----------------------------------------------------------------
        // User can add an asset to themself (and it updates their net-worth automatically)
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
                .post("/users/" + userSessionCookie + "/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets assigned to user", responseObject.get("message"));
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
            assertEquals(originalValue + 10000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("assets");
            assertEquals(10000, responseArray.getJSONObject(0).getInt("amount"));
            assertEquals("car", responseArray.getJSONObject(0).get("label"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can add an asset to a user (and it updates their net-worth automatically)
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
                .post("/users/" + userSessionCookie + "/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets assigned to user", responseObject.get("message"));
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
            assertEquals(originalValue + 10000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("assets");
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
                .post("/users/fake_user/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to assign assets", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    // Test assets PUT
    @Test
    public void assetsPutTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        int originalNetWorth;
        int assetId;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can edit their own asset (and it updates their net-worth automatically)
        - TODO: User can't edit another user's asset (and the other user's net-worth doesn't update)
        - Admin can edit another user's asset (and it updates the other user's net-worth automatically)
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
                .post("/users/" + userSessionCookie + "/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can edit their own asset (and it updates their net-worth automatically)
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
            originalValue = responseObject.getJSONArray("assets").getJSONObject(0).getInt("amount");
            assetId = responseObject.getJSONArray("assets").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .body("{\"id\": " + assetId + ", \"label\": \"car\", \"amount\": 20000}")
                .when()
                .put("/users/" + userSessionCookie + "/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets updated", responseObject.get("message"));
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
            assertEquals(originalNetWorth - originalValue + 20000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("assets");
            assertEquals(assetId, responseArray.getJSONObject(0).getInt("id"));
            assertEquals(20000, responseArray.getJSONObject(0).getInt("amount"));
            assertEquals("car", responseArray.getJSONObject(0).get("label"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Admin can edit another user's asset (and it updates the other user's net-worth automatically)
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
            originalValue = responseObject.getJSONArray("assets").getJSONObject(0).getInt("amount");
            assetId = responseObject.getJSONArray("assets").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"id\": " + assetId + ", \"label\": \"car\", \"amount\": 30000}")
                .when()
                .put("/users/" + userSessionCookie + "/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets updated", responseObject.get("message"));
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
            assertEquals(originalNetWorth - originalValue + 30000, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("assets");
            assertEquals(assetId, responseArray.getJSONObject(0).getInt("id"));
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
                .put("/users/fake_user/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to edit assets", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Test assets DELETE
    @Test
    public void assetsDeleteTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        int originalNetWorth;
        int assetId;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - User can delete their own asset (And the value is automatically subtracted from their net-worth)
        - TODO: User can't delete another user's asset (And the value is automatically subtracted from the other user's net-worth)
        - TODO: Admin can delete their own asset (And the value is automatically subtracted from their net-worth)
        - TODO: Admin can delete another user's asset (And the value is automatically subtracted from the other user's net-worth)
        - Error when an invalid user is given
        - Error when an invalid asset is given
         */

        // ----------------------------------------------------------------
        // Initialize
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .body("{\"label\": \"car\", \"amount\": 10000}")
                .when()
                .post("/users/" + userSessionCookie + "/assets");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets assigned to user", responseObject.get("message"));
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
                .delete("/users/fake_user/assets/0");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to delete assets", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Error when an invalid asset is given
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie + "/assets/99");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to delete assets", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can delete their own asset (And the value is automatically subtracted from their net-worth)
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
            originalValue = responseObject.getJSONArray("assets").getJSONObject(0).getInt("amount");
            assetId = responseObject.getJSONArray("assets").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie + "/assets/" + assetId);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Assets deleted", responseObject.get("message"));
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
            assertEquals(originalNetWorth - originalValue, responseObject.getInt("netWorth"));
            responseArray = responseObject.getJSONArray("assets");
            assertEquals("[]", responseArray.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
