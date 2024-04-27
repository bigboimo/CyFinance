package com.example.demo.earnings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/earnings")
public class EarningsController {

    @Autowired
    private EarningsRepository earningsRepository;

    private static final String SUCCESS = "{\"message\":\"success\"}";
    private static final String FAILURE = "{\"message\":\"failure\"}";

    /**
     * Retrieve all earnings records.
     * Utilizes Spring Data JPA's findAll() method to fetch all records from the database.
     * @return a list of all earnings records
     */
    @GetMapping
    public List<Earnings> getAllEarnings() {
        return earningsRepository.findAll();
    }

    /**
     * Retrieves an earnings record by its ID provided as a string. This method attempts to convert the string ID to a long value.
     * If the ID is not a valid long or if it exceeds the range of long values, it returns a bad request response.
     * If the ID is valid but no corresponding earnings record is found, it returns a not found response.
     * This ensures robust handling of user input and protects against malformed or inappropriately large values.
     *
     * @param id The string representation of the earnings record ID.
     * @return ResponseEntity containing the earnings record if found, or the appropriate error response if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEarningsById(@PathVariable String id) {
        try {
            BigInteger bigIntId = new BigInteger(id);
            if (bigIntId.bitLength() > 63) { // Checks if the number is too large for a Long
                return ResponseEntity.badRequest().build();
            }
            Long numericId = bigIntId.longValue();
            return earningsRepository.findById(numericId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build(); // Handle malformed number
        }
    }




    /**
     * Create a new earnings record.
     * Validates the request body as an Earnings object.
     * Saves the object to the database and returns a ResponseEntity with the created status and object.
     * @param earnings the earnings to create
     * @return ResponseEntity containing the created earnings and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> createEarnings(@RequestBody Earnings earnings) {
        if (earnings == null) {
            return ResponseEntity.badRequest().body("{\"error\":\"Request body cannot be null\"}");
        }
        // Validate negative income values
        if (earnings.getPrimaryMonthlyIncome() < 0 || earnings.getSecondaryMonthlyIncome() < 0) {
            return ResponseEntity.badRequest().body("{\"error\":\"Income values cannot be negative\"}");
        }

        Earnings savedEarnings = earningsRepository.save(earnings);
        return ResponseEntity.ok(savedEarnings);  // Return 200 OK with the saved earnings
    }


    /**
     * Update an existing earnings record identified by ID.
     * Demonstrates the use of Optional for conditional logic in a functional style.
     * findById fetches the record, map updates it, and changes are saved to the database.
     * @param id the ID of the earnings to update
     * @param request the updated earnings data
     * @return ResponseEntity indicating success or the absence of the earnings record
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEarnings(@PathVariable Long id, @RequestBody Earnings request) {
        // Check if the incoming data is numeric
        try {
            Float.parseFloat(String.valueOf(request.getPrimaryMonthlyIncome()));
            Float.parseFloat(String.valueOf(request.getSecondaryMonthlyIncome()));
        } catch (NumberFormatException e) {
            // If any of the values are not numeric, return Bad Request response
            return ResponseEntity.badRequest().body("Primary and secondary monthly incomes must be numeric.");
        }

        return earningsRepository.findById(id)
                .map(existingEarnings -> {
                    existingEarnings.setPrimaryMonthlyIncome(request.getPrimaryMonthlyIncome());
                    existingEarnings.setSecondaryMonthlyIncome(request.getSecondaryMonthlyIncome());
                    earningsRepository.save(existingEarnings);
                    return ResponseEntity.ok("Earnings updated successfully.");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    /**
     * Delete an earnings record by its ID.
     * Uses findById to conditionally find and delete the record, demonstrating functional style.
     * @param id the ID of the earnings to delete
     * @return ResponseEntity indicating success or failure based on record presence
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEarnings(@PathVariable Long id) {
        return earningsRepository.findById(id)
                .map(earnings -> {
                    earningsRepository.deleteById(id);
                    return ResponseEntity.ok(SUCCESS);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());  // Handle not found situation functionally
    }
}
