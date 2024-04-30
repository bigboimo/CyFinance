package com.example.demo.expenses;


import com.example.demo.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    @Autowired
    private ExpensesRepository expensesRepository;

    private static final String SUCCESS = "{\"message\":\"success\"}";
    private static final String FAILURE = "{\"message\":\"failure\"}";

    /**
     * Retrieve all expenses records.
     * Utilizes Spring Data JPA's findAll() method to fetch all records from the database.
     *
     * @return a list of all expenses records
     */
    @GetMapping
    public ResponseEntity<?> getAllExpenses() {
        try {
            List<Expenses> expensesList = expensesRepository.findAll();
            return ResponseEntity.ok(expensesList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Retrieves an expenses record by its ID provided as a string.
     * This method attempts to convert the string ID to a long value.
     * If the ID is not a valid long or if it exceeds the range of long values, it returns a bad request response.
     * If the ID is valid but no corresponding expenses record is found, it returns a not found response.
     *
     * @param id The string representation of the expenses record ID.
     * @return ResponseEntity containing the expenses record if found, or the appropriate error response if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpensesById(@PathVariable String id) {
        try {
            BigInteger bigIntId = new BigInteger(id);
            if (bigIntId.bitLength() > 63) {
                return ResponseEntity.badRequest().body(FAILURE);
            }
            Long numericId = bigIntId.longValue();
            return expensesRepository.findById(numericId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Create a new expenses record.
     * Validates the request body as an Expenses object.
     * Saves the object to the database and returns a ResponseEntity with the created status and object.
     *
     * @param expenses the expenses to create
     * @return ResponseEntity containing the created expenses and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> createExpenses(@RequestBody Expenses expenses) {
        try {
            if (expenses == null) {
                return ResponseEntity.badRequest().body(FAILURE);
            }
            Expenses savedExpenses = expensesRepository.save(expenses);
            return ResponseEntity.ok(savedExpenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Update an existing expenses record identified by ID.
     * Demonstrates the use of Optional for conditional logic in a functional style.
     * findById fetches the record, map updates it, and changes are saved to the database.
     *
     * @param id      the ID of the expenses to update
     * @param request the updated expenses data
     * @return ResponseEntity indicating success or the absence of the expenses record
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateExpenses(@PathVariable Long id, @RequestBody Expenses request) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body(FAILURE);
            }
            return expensesRepository.findById(id)
                    .map(existingExpenses -> {
                        existingExpenses.setFood(request.getFood());
                        existingExpenses.setMisc(request.getMisc());
                        existingExpenses.setSchool(request.getSchool());
                        existingExpenses.setRentandBills(request.getRentandBills());
                        existingExpenses.setOtherNeeds(request.getOtherNeeds());
                        expensesRepository.save(existingExpenses);
                        return ResponseEntity.ok(SUCCESS);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Delete an expenses record by its ID.
     * Uses findById to conditionally find and delete the record, demonstrating functional style.
     *
     * @param id the ID of the expenses to delete
     * @return ResponseEntity indicating success or failure based on record presence
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpenses(@PathVariable Long id) {
        try {
            expensesRepository.findById(id)
                    .ifPresent(expensesRepository::delete);
            return ResponseEntity.ok(SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }


    @DeleteMapping(path = "/expenses/{id}")
    String deleteExpenses(@PathVariable int id) {
        Response<String> response = new Response<>();
        expensesRepository.deleteById(id);
        response.put("message", "Expenses deleted");
        return response.toString();
    }
}
