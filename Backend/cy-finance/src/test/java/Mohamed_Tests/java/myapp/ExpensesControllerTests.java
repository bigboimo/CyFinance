package Mohamed_Tests.java.myapp;

import com.example.demo.CyFinanceApplication;
import com.example.demo.expenses.ExpensesRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CyFinanceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ExpensesControllerTests {

    @LocalServerPort
    private int port;

    @Mock
    private ExpensesRepository expensesRepository;


    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

    }

    @Test
    public void testFetchAllExpensesSuccessfully() {
        Response response = RestAssured.get("/expenses");
        assertEquals(200, response.getStatusCode());
        assertNotNull("Expenses list should not be null", response.getBody());
    }

    @Test
    public void testFetchAllExpensesWhenEmpty() {
        Response response = RestAssured.get("/expenses");
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should contain an empty list", response.asString().contains("[]"));
    }

    @Test
    public void testRetrieveExpenseByValidID() {
        // First, create an expense and capture its ID
        String jsonCreate = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(jsonCreate)
                .post("/expenses");
        assertEquals(200, createResponse.getStatusCode());

        // Extract the ID from the response
        int createdId = createResponse.jsonPath().getInt("id");

        // Now retrieve the newly created expense by ID
        Response response = RestAssured.get("/expenses/" + createdId);
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should contain expense details with the correct ID",
                response.asString().contains("\"id\":" + createdId));
    }

    @Test
    public void testRetrieveFailureForNonExistingID() {
        // Assume this ID does not exist
        Response response = RestAssured.get("/expenses/9999");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testRetrieveWithInvalidIDFormat() {
        Response response = RestAssured.get("/expenses/abc");
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testCreateExpenseSuccessfully() {
        String json = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should confirm creation", response.asString().contains("success"));
    }

    @Test
    public void testCreateExpenseWithMissingFields() {
        String json = "{\"food\": 200}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testCreateExpenseWithNegativeValues() {
        String json = "{\"food\": -200, \"rentandBills\": -300}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testUpdateExistingExpenseSuccessfully() {
        // First, create an expense to ensure it exists
        String jsonCreate = "{\"food\": 100, \"rentandBills\": 150}";
        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(jsonCreate)
                .post("/expenses");
        int createdId = createResponse.jsonPath().getInt("id"); // Extract the created ID

        // Now, update the newly created expense
        String jsonUpdate = "{\"food\": 250, \"rentandBills\": 400}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(jsonUpdate)
                .put("/expenses/" + createdId); // Use the created ID for update
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should confirm update", response.asString().contains("success"));
    }


    @Test
    public void testUpdateNonExistingExpense() {
        String json = "{\"food\": 300, \"rentandBills\": 450}";
        Response response = RestAssured.given().contentType("application/json").body(json).put("/expenses/9999");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testUpdateExpenseWithInvalidData() {
        // Create a valid expense first
        String jsonCreate = "{\"food\": 100, \"rentandBills\": 200}";
        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(jsonCreate)
                .post("/expenses");
        int createdId = createResponse.jsonPath().getInt("id"); // Assuming ID is returned

        // Now attempt to update with invalid data
        String jsonUpdate = "{\"food\": -100, \"rentandBills\": -200}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(jsonUpdate)
                .put("/expenses/" + createdId);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testDeleteExpenseSuccessfully() {
        // First, create an expense
        String jsonCreate = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(jsonCreate)
                .post("/expenses");
        int createdId = createResponse.jsonPath().getInt("id"); // Assuming ID is returned

        // Now attempt to delete the newly created expense
        Response response = RestAssured.delete("/expenses/" + createdId);
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should confirm deletion", response.asString().contains("success"));
    }

    @Test
    public void testDeleteNonExistingExpense() {
        Response response = RestAssured.delete("/expenses/9999");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testDeleteExpenseWithInvalidIdFormat() {
        Response response = RestAssured.delete("/expenses/abc"); // Non-numeric ID
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testCreateExpenseIgnoringUnmappedFields() {
        String json = "{\"food\": 100, \"rentandBills\": 200, \"extraField\": \"unexpected\"}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should not contain extra fields", !response.asString().contains("\"extraField\":"));
    }

    @Test
    public void testHandlingOfSQLDatabaseErrors() {
        // Mock the ExpensesRepository to throw a RuntimeException when findAll is called
        when(expensesRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Make the request to the endpoint
        Response response = RestAssured.get("/expenses");

        // Verify the response status code
        assertEquals(500, response.getStatusCode());
    }

    @Test
    public void testCreateExpenseWithLargeValues() {
        String json = "{\"food\": 1E+10, \"rentandBills\": 1E+10, \"school\": 1E+10, \"otherNeeds\": 1E+10, \"misc\": 1E+10}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        boolean isSuccessful = response.getStatusCode() == 200;
        boolean isProperErrorHandling = response.getStatusCode() == 400 && response.asString().contains("value too large");
        assertTrue("API should handle large values by either succeeding or providing a meaningful error", isSuccessful || isProperErrorHandling);
    }

    @Test
    public void testUpdateWithoutChanges() {
        String json = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response createResponse = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, createResponse.getStatusCode());

        // Updating with the same values
        Response updateResponse = RestAssured.given().contentType("application/json").body(json).put("/expenses/1");
        assertEquals(200, updateResponse.getStatusCode());
        assertTrue("Response should confirm no actual data change", updateResponse.asString().contains("success"));
    }

    @Test
    public void testDeleteThenCreateExpenseSequentially() {
        // Delete an existing expense
        Response deleteResponse = RestAssured.delete("/expenses/1"); // Assuming '1' is a valid ID
        assertEquals(200, deleteResponse.getStatusCode());

        // Immediately create a new expense
        String json = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response createResponse = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, createResponse.getStatusCode());
    }

    @Test
    public void testCreateExpenseWithMalformedJson() {
        String json = "{food: 200, rentAndBills: 300}"; // Incorrect JSON format
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(400, response.getStatusCode());
    }
}
