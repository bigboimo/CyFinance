package com.example.demo.MohamedTests;

import com.example.demo.CyFinanceApplication;
import com.example.demo.expenses.Expenses;
import com.example.demo.users.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CyFinanceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ExpensesControllerTests {

    @LocalServerPort
    private int port;


    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    // Test to verify that the API can fetch all expenses and returns HTTP status 200.
    @Test
    public void testFetchAllExpensesSuccessfully() {
        Response response = RestAssured.get("/expenses");
        assertEquals(200, response.getStatusCode());
        assertNotNull("Expenses list should not be null", response.getBody());
    }

    // Test to ensure that fetching expenses from an empty database returns an empty list but still status 200.
    @Test
    public void testFetchAllExpensesWhenEmpty() {
        Response response = RestAssured.get("/expenses");
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should contain an empty list", response.asString().contains("[]"));
    }


    // Tests that a valid expense ID can retrieve the correct expense details.
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


    // Test to ensure that retrieving an expense with a non-existing ID results in HTTP status 404.
    @Test
    public void testRetrieveFailureForNonExistingID() {
        // Assume this ID does not exist
        Response response = RestAssured.get("/expenses/9999");
        assertEquals(404, response.getStatusCode());
    }


    // Test to verify that attempting to retrieve an expense with an invalid ID format returns HTTP status 400.
    @Test
    public void testRetrieveWithInvalidIDFormat() {
        Response response = RestAssured.get("/expenses/abc");
        assertEquals(400, response.getStatusCode());
    }


    // Test that a new expense can be successfully created and that the response correctly reflects the created expense.
    @Test
    public void testCreateExpenseSuccessfully() {
        String json = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(json)
                .post("/expenses");
        assertEquals(200, response.getStatusCode());

        // Deserialize response into Expenses object and verify contents
        Expenses createdExpense = response.as(Expenses.class);
        assertNotNull("Created expense object should not be null", createdExpense);
        assertEquals(200, createdExpense.getFood(), 0.01);
        assertEquals(300, createdExpense.getRentandBills(), 0.01);
        assertEquals(100, createdExpense.getSchool(), 0.01);
        assertEquals(50, createdExpense.getOtherNeeds(), 0.01);
        assertEquals(25, createdExpense.getMisc(), 0.01);
    }


    // Test that creating an expense with some fields missing sets those missing fields to their default values (assumed zero).
    @Test
    public void testCreateExpenseWithMissingFields() {
        String json = "{\"food\": 200}"; // Missing 'rentandBills', 'school', 'otherNeeds', 'misc'
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, response.getStatusCode());

        // Deserialize response into Expenses object
        Expenses createdExpense = response.as(Expenses.class);

        // Verify that the missing fields are set to their default values, assuming 0 is the default
        assertNotNull("Created expense object should not be null", createdExpense);
        assertEquals("Food value should match the provided value", 200, createdExpense.getFood(), 0.01);
        assertEquals("Rent and Bills should be 0 as it's missing", 0, createdExpense.getRentandBills(), 0.01);
        assertEquals("School expenses should be 0 as it's missing", 0, createdExpense.getSchool(), 0.01);
        assertEquals("Other Needs should be 0 as it's missing", 0, createdExpense.getOtherNeeds(), 0.01);
        assertEquals("Misc expenses should be 0 as it's missing", 0, createdExpense.getMisc(), 0.01);
    }

    // Test to confirm that creating an expense with negative values correctly returns HTTP status 400.
    @Test
    public void testCreateExpenseWithNegativeValues() {
        String json = "{\"food\": -200, \"rentandBills\": -300}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(400, response.getStatusCode());
    }


    // Test that an existing expense can be updated successfully and that the response includes a success message.
    @Test
    public void testUpdateExistingExpenseSuccessfully() {
        // First, create an expense to ensure it exists
        String jsonCreate = "{\"food\": 100, \"rentandBills\": 150}";
        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(jsonCreate)
                .post("/expenses");
        int createdId = createResponse.jsonPath().getInt("id"); // Extract the created ID
        assertEquals(200, createResponse.getStatusCode());

        // Now, update the newly created expense
        String jsonUpdate = "{\"food\": 250, \"rentandBills\": 400}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(jsonUpdate)
                .put("/expenses/" + createdId); // Use the created ID for update
        assertEquals(200, response.getStatusCode());

        // Verify response content type
        assertEquals("text/plain;charset=UTF-8", response.getContentType());

        // Check if the response body contains the expected success message
        assertTrue("Response should confirm update", response.asString().contains("{\"message\":\"success\"}"));
    }


    // Test that updating a non-existing expense results in HTTP status 404.
    @Test
    public void testUpdateNonExistingExpense() {
        String json = "{\"food\": 300, \"rentandBills\": 450}";
        Response response = RestAssured.given().contentType("application/json").body(json).put("/expenses/9999");
        assertEquals(404, response.getStatusCode());
    }


    // Test that updating an expense with invalid data (negative values) results in HTTP status 400.
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


    // Test that an expense can be deleted successfully and the response confirms the deletion.
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


    // Test to ensure that attempting to delete a non-existing expense results in HTTP status 404.
    @Test
    public void testDeleteNonExistingExpense() {
        Response response = RestAssured.delete("/expenses/9999");
        assertEquals(404, response.getStatusCode());
    }


    // Test that attempting to delete an expense with an invalid ID format returns HTTP status 400.
    @Test
    public void testDeleteExpenseWithInvalidIdFormat() {
        Response response = RestAssured.delete("/expenses/abc"); // Non-numeric ID
        assertEquals(400, response.getStatusCode());
    }


    // Test that creating an expense while ignoring unmapped fields doesn't break the functionality and still returns HTTP status 200.
    @Test
    public void testCreateExpenseIgnoringUnmappedFields() {
        String json = "{\"food\": 100, \"rentandBills\": 200, \"extraField\": \"unexpected\"}";
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, response.getStatusCode());
        assertTrue("Response should not contain extra fields", !response.asString().contains("\"extraField\":"));
    }


    // Test that the API can handle very large values for expenses, either by succeeding with HTTP status 200 or by returning a meaningful error with status 400.
    @Test
    public void testCreateExpenseWithLargeValues() {
        String json = "{\"food\": 1E+10, \"rentandBills\": 1E+10, \"school\": 1E+10, \"otherNeeds\": 1E+10, \"misc\": 1E+10}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(json)
                .post("/expenses");

        boolean isSuccessful = response.getStatusCode() == 200;
        boolean isProperErrorHandling = response.getStatusCode() == 400 && response.asString().contains("value too large");

        assertTrue("API should handle large values by either succeeding or providing a meaningful error", isSuccessful || isProperErrorHandling);

        if (response.getStatusCode() == 200) {
            // Deserialize response into Expenses object and verify contents
            Expenses createdExpense = response.as(Expenses.class);
            assertNotNull("Created expense object should not be null", createdExpense);
            assertEquals(1E+10, createdExpense.getFood(), 0.01);
            assertEquals(1E+10, createdExpense.getRentandBills(), 0.01);
            assertEquals(1E+10, createdExpense.getSchool(), 0.01);
            assertEquals(1E+10, createdExpense.getOtherNeeds(), 0.01);
            assertEquals(1E+10, createdExpense.getMisc(), 0.01);
        }
    }


    // Test that updating an expense with the same values results in HTTP status 200 and confirms no actual data change.
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


    // Test the sequence of deleting an existing expense and then immediately creating a new one to check for any residue or state issues.
    @Test
    public void testDeleteThenCreateExpenseSequentially() {
        // First, ensure the expense exists
        String jsonCreate = "{\"food\": 200, \"rentandBills\": 300, \"school\": 100, \"otherNeeds\": 50, \"misc\": 25}";
        Response createResponse = RestAssured.given().contentType("application/json").body(jsonCreate).post("/expenses");
        int createdId = createResponse.jsonPath().getInt("id");

        // Delete the expense
        Response deleteResponse = RestAssured.delete("/expenses/" + createdId);
        assertEquals(200, deleteResponse.getStatusCode());

        // Create a new expense
        Response createNewResponse = RestAssured.given().contentType("application/json").body(jsonCreate).post("/expenses");
        assertEquals(200, createNewResponse.getStatusCode());

    }


    // Test to confirm that malformed JSON input results in HTTP status 400.
    @Test
    public void testCreateExpenseWithMalformedJson() {
        String json = "{food: 200, rentAndBills: 300}"; // Incorrect JSON format
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(400, response.getStatusCode());
    }


    // Test to verify that missing fields in the expense creation are automatically set to zero, ensuring default values are used where data is not provided.
    @Test
    public void testCreateExpenseWithMissingFieldsSetToZero() {
        String json = "{\"food\": 200}"; // Only 'food' is provided; other fields are missing
        Response response = RestAssured.given().contentType("application/json").body(json).post("/expenses");
        assertEquals(200, response.getStatusCode());

        Expenses responseExpenses = response.jsonPath().getObject("", Expenses.class);
        assertNotNull(responseExpenses);
        assertEquals(0, responseExpenses.getRentandBills(), 0.01);
        assertEquals(0, responseExpenses.getSchool(), 0.01);
        assertEquals(0, responseExpenses.getOtherNeeds(), 0.01);
        assertEquals(0, responseExpenses.getMisc(), 0.01);
        assertEquals(200, responseExpenses.getFood(), 0.01); // Verify provided value remains unchanged
    }


    @Test
    public void testCreateAndAssignExpenses() {
        // Create a user first to assign expenses to
        String userJson = "{\"email\": \"test@example.com\", \"name\": \"Test User\", \"password\": \"pass123\", \"role\": \"user\"}";
        Response userResponse = RestAssured.given()
                .contentType("application/json")
                .body(userJson)
                .post("/users");
        String userEmail = userResponse.jsonPath().getString("email");

        // Create an expense
        String expenseJson = "{\"food\": 150, \"rentandBills\": 250, \"school\": 100, \"otherNeeds\": 75, \"misc\": 50, \"user\": {\"email\": \"" + userEmail + "\"}}";
        Response expenseResponse = RestAssured.given()
                .contentType("application/json")
                .body(expenseJson)
                .post("/expenses");
        assertEquals("Verify HTTP status code for expense creation", 200, expenseResponse.getStatusCode());
        int expenseId = expenseResponse.jsonPath().getInt("id");

        // Retrieve the assigned expense to the user and validate the content of the assigned expenses to confirm the details match
        Response assignedExpenseResponse = RestAssured.get("/users/" + userEmail + "/expenses");
        assertTrue("Check if the expense is assigned to the user", assignedExpenseResponse.asString().contains(String.valueOf(expenseId)));
    }

    // Create a user and assign expenses for testing
    private String createUserAndAssignExpenses() {
        // Create user
        String userJson = "{\"email\": \"test@example.com\", \"name\": \"Test User\", \"password\": \"pass123\", \"role\": \"user\"}";
        Response userResponse = RestAssured.given()
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        // Check if user creation was successful
        if (userResponse.getStatusCode() != 201) {
            throw new IllegalStateException("Failed to create user: " + userResponse.getBody().asString());
        }

        // Extract user email from the response
        String userEmail = userResponse.jsonPath().getString("email");

        // Assign expenses to the created user
        String expenseJson = "{\"food\": 100, \"rentandBills\": 150, \"school\": 50, \"otherNeeds\": 30, \"misc\": 20, \"userEmail\": \"" + userEmail + "\"}";
        Response expenseResponse = RestAssured.given()
                .contentType("application/json")
                .body(expenseJson)
                .post("/expenses");

        // Check if expense assignment was successful
        if (expenseResponse.getStatusCode() != 200) {
            throw new IllegalStateException("Failed to assign expenses: " + expenseResponse.getBody().asString());
        }

        return userEmail;
    }
    @Test
    public void testUpdateAssignedExpenses() {
        String userEmail = createUserAndAssignExpenses();

        int expenseId = 1; // Get this ID from creation logic or set up if database is known
        String updatedExpenseJson = "{\"food\": 200, \"rentandBills\": 300, \"school\": 150, \"otherNeeds\": 100, \"misc\": 60}";
        Response updateResponse = RestAssured.given()
                .contentType("application/json")
                .body(updatedExpenseJson)
                .put("/expenses/" + expenseId);
        assertEquals("Check HTTP status code for updating expenses", 200, updateResponse.getStatusCode());
        assertTrue("Verify the response indicates success", updateResponse.asString().contains("success"));
    }

    @Test
    public void testDeleteAssignedExpenses() {
        String userEmail = createUserAndAssignExpenses();
        int expenseId = 1; // Get this ID from creation logic or set up if database is known
        Response deleteResponse = RestAssured.delete("/expenses/" + expenseId);
        assertEquals("Check HTTP status code for deleting expenses", 200, deleteResponse.getStatusCode());
        assertTrue("Verify the response indicates successful deletion", deleteResponse.asString().contains("success"));
    }






}
