package com.example.demo.receipts;

import com.example.demo.CyFinanceApplication;
import com.example.demo.liabilities.LiabilitiesSystemTest;
import com.mysql.cj.util.TestUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.util.ResourceUtils.getFile;

@SpringBootTest(classes = CyFinanceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReceiptsSystemTest {

    private final Logger logger = LoggerFactory.getLogger(ReceiptsSystemTest.class);

    @LocalServerPort
    int port;

    @BeforeEach
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
    public void getAllReceiptsTest() throws FileNotFoundException {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Get all receipts
        - Invalid user
        - User can't access
         */

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image.png"), "multipart/*")
                .when()
                .post("/users/" + userSessionCookie + "/receipts/groceries");

        System.out.printf(response.body().print());

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Get all receipts
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie + "/receipts");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals(1, responseObject.getInt("numReceipts"));
            responseArray = responseObject.getJSONArray("receiptsData");
            assertEquals("groceries", responseArray.getJSONObject(0).getJSONObject("metadata").get("label"));
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
                .get("/users/fake_user/receipts");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Invalid user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't access
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie + "/receipts");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void getReceiptsByIdTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Get receipt by id
        - Get receipt for wrong user
        - Get non-existent receipt
        - Invalid user
        - User can't access
         */

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image.png"), "multipart/form-data")
                .when()
                .post("/users/" + userSessionCookie + "/receipts/groceries");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Get receipt by id
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + userSessionCookie + "/receipts/1");

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.body().print());

        // ----------------------------------------------------------------
        // Get receipt for wrong user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt not assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Get receipt for wrong user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt not assigned to user", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .get("/users/fake_user/receipts");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Invalid user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't access
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .get("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("User not allowed to perform this action", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void receiptsPostTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Add receipt
        - No image
        - Invalid user
        - User can't access
         */

        // ----------------------------------------------------------------
        // Add receipt
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image.png"))
                .when()
                .post("/users/" + userSessionCookie + "/receipts/groceries");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt successfully uploaded.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // No image (Can't test because Spring always checks it
        // ----------------------------------------------------------------

//        response = RestAssured.given()
//                .header("Cookie", "user-id=" + adminSessionCookie)
//                .when()
//                .post("/users/" + userSessionCookie + "/receipts/restaurant");
//
//        assertEquals(400, response.getStatusCode());
//        try {
//            responseObject = new JSONObject(response.body().print());
//            assertEquals("Invalid file type. Only JPEG and PNG are allowed.", responseObject.get("message"));
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }

        // ----------------------------------------------------------------
        // Invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image.png"))
                .when()
                .post("/users/fake_user/receipts/98129849");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Invalid user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't access
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + userSessionCookie)
                .multiPart("image", new File("../../test_image.png"))
                .when()
                .post("/users/" + adminSessionCookie + "/receipts/2235981");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Unauthorized access or invalid user.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void receiptsPutTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Edit receipt
        - No image
        - Invalid id provided
        - Invalid user
        - Receipt doesn't belong to user
        - User can't access
         */

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image.png"))
                .when()
                .post("/users/" + userSessionCookie + "/receipts/groceries");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Edit receipt
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image2.png"))
                .when()
                .put("/users/" + userSessionCookie + "/receipts/1");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt updated successfully.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // No image (Can't test because Spring already checks it
        // ----------------------------------------------------------------

//        response = RestAssured.given()
//                .header("Cookie", "user-id=" + adminSessionCookie)
//                .when()
//                .put("/users/" + userSessionCookie + "/receipts/1");
//
//        assertEquals(400, response.getStatusCode());
//        try {
//            responseObject = new JSONObject(response.body().print());
//            assertEquals("Invalid file type. Only JPEG and PNG are allowed.", responseObject.get("message"));
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }

        // ----------------------------------------------------------------
        // Invalid id
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image2.png"))
                .when()
                .put("/users/" + userSessionCookie + "/receipts/2");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt not found.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image2.png"))
                .when()
                .put("/users/fake_user/receipts/1");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Invalid user provided", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Receipt doesn't belong to user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image2.png"))
                .when()
                .put("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt does not belong to user.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't access
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + userSessionCookie)
                .multiPart("image", new File("../../test_image2.png"))
                .when()
                .put("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Unauthorized access or invalid user.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void receiptsDeleteTest() {
        Response response;
        JSONArray responseArray;
        JSONObject responseObject;
        String userSessionCookie = "testing_user@email.com";
        String adminSessionCookie = "testing_admin@email.com";

        /*
        - Invalid user
        - Receipt doesn't exist
        - Receipt doesn't belong to user
        - User can't access
        - Delete receipt
         */

        response = RestAssured.given()
                .header("Content-Type", "multipart/form-data")
                .header("Cookie", "user-id=" + adminSessionCookie)
                .multiPart("image", new File("../../test_image.png"))
                .when()
                .post("/users/" + userSessionCookie + "/receipts/groceries");

        assertEquals(200, response.getStatusCode());

        // ----------------------------------------------------------------
        // Invalid user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/fake_user/receipts/1");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Unauthorized access or invalid user.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Receipt doesn't exist
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie + "/receipts/2");

        assertEquals(400, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt not found.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Receipt Doesn't belong to user
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt does not belong to user.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // User can't access
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + userSessionCookie)
                .when()
                .delete("/users/" + adminSessionCookie + "/receipts/1");

        assertEquals(403, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Unauthorized access or invalid user.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ----------------------------------------------------------------
        // Delete receipt
        // ----------------------------------------------------------------

        response = RestAssured.given()
                .header("Cookie", "user-id=" + adminSessionCookie)
                .when()
                .delete("/users/" + userSessionCookie + "/receipts/1");

        assertEquals(200, response.getStatusCode());
        try {
            responseObject = new JSONObject(response.body().print());
            assertEquals("Receipt deleted successfully.", responseObject.get("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
}
