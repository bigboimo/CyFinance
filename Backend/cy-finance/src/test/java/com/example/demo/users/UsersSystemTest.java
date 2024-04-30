package com.example.demo.users;


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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = CyFinanceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsersSystemTest {
    private final Logger logger = LoggerFactory.getLogger(UsersSystemTest.class);

    @LocalServerPort
    int port;

    /*
    - Setup creates 2 users
      - user: testing_user@email.com
      - admin: testing_admin@email.com
    - All normal tests use the admin cookie
    - UAC is checked by the last non-admin test
     */

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        Response response;
        JSONArray prexistingUsers;

        // Create non-admin user
        response = RestAssured.given().
                header("Content-Type", "application/json").
                body("{\"name\":\"Test Admin\", \"email\":\"testing_admin@email.com\", \"password\":\"password\", \"role\":\"admin\"}").
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

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=testing_admin@email.com")
                .when()
                .get("/users");

        logger.info("[SetUp] Users start: " + response.getBody().print());
        assertEquals(200, response.getStatusCode());
        try {
            prexistingUsers = new JSONArray(response.getBody().print());

            for (int i = 0; i < prexistingUsers.length(); i++) {
                response = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .header("Cookie", "user-id=" + prexistingUsers.getJSONObject(i).get("email"))
                        .when()
                        .delete("/users/" + prexistingUsers.getJSONObject(i).get("email"));

                assertEquals(200, response.getStatusCode());
                assertEquals("User deleted", new JSONObject(response.getBody().print()).get("message"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Create non-admin user
        response = RestAssured.given().
                header("Content-Type", "application/json").
                body("{\"name\":\"Test Admin\", \"email\":\"testing_admin@email.com\", \"password\":\"password\", \"role\":\"admin\"}").
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

        // Create non-admin user
        response = RestAssured.given().
                header("Content-Type", "application/json").
                body("{\"name\":\"Test User\", \"email\":\"testing_user@email.com\", \"password\":\"password\", \"role\":\"user\"}").
                when().
                post("/users");

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
    }

    @Test
    public void usersGetAllTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Get fresh users
        - Get users with everything attached
         */

        // ----------------------------------------------------------------
        // Get same user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users");

        assertEquals(200, response.getStatusCode());
        try {
            responseArray = new JSONArray(response.getBody().print());
            assert(responseArray.getJSONObject(0).get("email").equals(userSessionCookie) || responseArray.getJSONObject(0).get("email").equals(adminSessionCookie));
            assert(responseArray.getJSONObject(1).get("email").equals(userSessionCookie) || responseArray.getJSONObject(1).get("email").equals(adminSessionCookie));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



    }

    @Test
    public void usersGetByIdTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Get same user
        - Get different user
        - Get non-existent user
         */

        // ----------------------------------------------------------------
        // Get same user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(adminSessionCookie, responseObject.get("email"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Get different user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(userSessionCookie, responseObject.get("email"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Get non-existent user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/fake_user");

        assertEquals(400, response.getStatusCode());

    }

    @Test
    public void usersPostTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Create user with name, email, and password
        - Can login with new user (check password hashing)
        - Create user with email that already exists
        - No body
         */

        // ----------------------------------------------------------------
        // Create user with name, email, and password
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"email\":\"new_user@email.com\", \"name\":\"New User\", \"password\":\"Password123!\", \"role\":\"user\"}")
                .when()
                .post("/users");

        assertEquals(201, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("User created", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Can login with new user (check password hashing)
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/login?email=new_user@email.com&password=Password123!");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("success", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Create user with email that exists
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"email\":\"" + userSessionCookie + "\", \"name\":\"New Name\"}")
                .when()
                .post("/users");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("User already exists", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // No body
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/users");

        assertEquals(400, response.getStatusCode());

    }

    @Test
    public void usersLoginTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Valid username and password
        - Incorrect password
        - No password
        - non-existent username
         */

        // ----------------------------------------------------------------
        // Valid username and password
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/login?email=" + userSessionCookie + "&password=password");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("success", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Incorrect password
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/login?email=" + userSessionCookie + "&password=password1");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("failure", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // No password
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/login?email=" + userSessionCookie);

        assertEquals(400, response.getStatusCode());

        // ----------------------------------------------------------------
        // Non-existent user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/login?email=fake_user&password=password");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("failure", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void usersLogoutTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Cookie set
        - Cookie not set
         */

        // ----------------------------------------------------------------
        // Cookie set
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/logout");

        assertEquals(200, response.getStatusCode());
        assertEquals("", response.cookie("user-id"));

        // ----------------------------------------------------------------
        // Cookie not set
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .post("/logout");

        assertEquals(200, response.getStatusCode());
        assertEquals("", response.cookie("user-id"));

    }

    @Test
    public void usersPutTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        JSONObject originalUser;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Change user's name
        - Change user's password
        - Change user's role
        - Non-existent user
        - No body
         */

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(userSessionCookie, responseObject.get("email"));
            originalUser = responseObject;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Change user's name
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"email\":\"" + userSessionCookie + "\", \"name\":\"New Name\"}")
                .when()
                .put("/users");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("New Name", responseObject.get("name"));
            assertNotEquals("New Name", originalUser.get("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Change user's password
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"email\":\"" + userSessionCookie + "\", \"password\":\"Encrypted_Password\"}")
                .when()
                .put("/users");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .post("/login?email=" + userSessionCookie + "&password=Encrypted_Password");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("success", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Change user's role
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"email\":\"" + userSessionCookie + "\", \"role\":\"admin\"}")
                .when()
                .put("/users");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("admin", responseObject.get("role"));
            assertNotEquals("admin", originalUser.get("role"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Non-existent user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .body("{\"email\":\"fake_user\", \"name\":\"New Name\"}")
                .when()
                .put("/users");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("No user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // No body
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users");

        assertEquals(400, response.getStatusCode());

    }

    @Test
    public void usersNetWorthTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Set net-worth
        - Invalid user
         */

        // ----------------------------------------------------------------
        // Set net-worth
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(userSessionCookie, responseObject.get("email"));
            originalValue = responseObject.getInt("netWorth");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users/" + userSessionCookie + "/networth/1000");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("User modified", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(1000, responseObject.getInt("netWorth"));
            assertNotEquals(1000, originalValue);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(userSessionCookie, responseObject.get("email"));
            originalValue = responseObject.getInt("netWorth");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .put("/users/fake_user/networth/2000");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals("No user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.getBody().print());
            assertEquals(originalValue, responseObject.getInt("netWorth"));
            assertNotEquals(2000, originalValue);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void usersDeleteTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        int originalValue;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Valid user id
        - Invalid user id
         */

        // ----------------------------------------------------------------
        // Invalid user id
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/fake_user");

        assertEquals(400, response.getStatusCode());
        try {
            assertEquals("Invalid user provided", new JSONObject(response.getBody().print()).get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Valid user id
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            assertEquals(userSessionCookie, new JSONObject(response.getBody().print()).get("email"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie);

        assertEquals(200, response.getStatusCode());
        try {
            assertEquals("User deleted", new JSONObject(response.getBody().print()).get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
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
        - User can't add other users
        - User can't edit other user's net-worth
        - User can't delete other users
        - User can delete themself
         */

        // ----------------------------------------------------------------
        // User can't get all users
        // ----------------------------------------------------------------

        // Try to get all users (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .get("/users");

        // Check for forbidden code
        assertEquals(403, response.getStatusCode());

        // Check that no users were returned
        logger.info("[GET /users] Response: " + response.print());
        assertEquals("", response.body().print());

        // ----------------------------------------------------------------
        // User can get their own details
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
        // User can't get details of other users
        // ----------------------------------------------------------------

        // Get your admin user (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .get("/users/testing_admin@email.com");

        // Check for response code (uncomment when merged with proper code)
        assertEquals(403, response.getStatusCode());

        logger.info("[GET /users/{id}] Response: " + response.print());
        assertEquals("", response.body().print());

        // ----------------------------------------------------------------
        // User can edit their own information
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
        // User can't edit other user's information
        // ----------------------------------------------------------------

        // Edit admin user (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .body("{\"email\": \"testing_admin@email.com\", \"name\": \"New Name\"}")
                .when()
                .put("/users");

        // Check for response code
        assertEquals(403, response.getStatusCode());

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
        // User can't add other users
        // ----------------------------------------------------------------

        // Edit admin user (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .body("{\"email\": \"testing_admin@email.com\", \"name\": \"New Name\"}")
                .when()
                .post("/users");

        // Check for response code
        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User creation not allowed", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't edit other user's net-worth
        // ----------------------------------------------------------------

        // Edit admin user (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .put("/users/testing_admin@email.com/networth/2364");

        // Check for response code
        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't delete other users
        // ----------------------------------------------------------------

        // Delete admin (fails)
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + sessionCookie)
                .when()
                .delete("/users/testing_admin@email.com");

        assertEquals(403, response.getStatusCode());

        logger.info("[DELETE /users] Response: " + response.print());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can delete themself
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

}
