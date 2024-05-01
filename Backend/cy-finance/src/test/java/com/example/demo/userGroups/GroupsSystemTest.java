package com.example.demo.userGroups;

import com.example.demo.CyFinanceApplication;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CyFinanceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GroupsSystemTest {
    private final Logger logger = LoggerFactory.getLogger(GroupsSystemTest.class);

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        Response response;

        // Create non-admin user
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Test User\", \"email\":\"testing_user@email.com\", \"password\":\"1love=Password\", \"role\":\"user\"}")
                .when()
                .post("/users");

        // Check status code
        assertEquals(201, response.getStatusCode());

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
        assertEquals(201, response.getStatusCode());

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

    @Test
    public void groupsGetTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Get empty groups
        - TODO: Get multiple groups
         */

        // ----------------------------------------------------------------
        // Get all groups
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/groups");

        assertEquals(200, response.getStatusCode());
        assertEquals("[]", response.body().print());
    }

    @Test
    public void groupsGetByIdTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Get group by id
        - Get non-existent group
         */

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"name\": \"family1\"}")
                .when()
                .post("/groups");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Get group by id
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/groups/1");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("family1", responseObject.get("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Get non-existent group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/groups/2");

        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void groupsPostTest() {

        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Add group
        - Add pre-existing group
         */

        // ----------------------------------------------------------------
        // Add group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"name\": \"family1\"}")
                .when()
                .post("/groups");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("success", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Add pre-existing group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"name\": \"family1\"}")
                .when()
                .post("/groups");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("A group with that name already exists", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void groupsPutTest() {

        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Edit group
        - Group doesn't exist
        - New label already exists
         */

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"name\": \"family1\"}")
                .when()
                .post("/groups");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Edit group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"id\":1, \"name\": \"family2\"}")
                .when()
                .put("/groups");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("success", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Group doesn't exist
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"id\":2, \"name\": \"family2\"}")
                .when()
                .put("/groups");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Invalid group provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Name already exists
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"id\":1, \"name\": \"family2\"}")
                .when()
                .put("/groups");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Name taken", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void groupsDeleteTest() {

        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Delete group
        - Group doesn't exist
         */

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"name\": \"family1\"}")
                .when()
                .post("/groups");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Delete group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/groups/1");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("success", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Group doesn't exist
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/groups/1");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Group doesn't exist", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void groupsAttachToUserTest() {

        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Attach group
        - User doesn't exist
        - Group doesn't exist
         */

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"name\": \"family1\"}")
                .when()
                .post("/groups");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Attach group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/users/" + userSessionCookie + "/groups/1");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Group assigned", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Non-existent user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/users/fake_user/groups/1");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to assign group", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Non-existent group
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/users/" + userSessionCookie + "/groups/2");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Failed to assign group", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
