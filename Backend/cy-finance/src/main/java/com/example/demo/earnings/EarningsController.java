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
     *
     * @return a list of all earnings records
     */
    @GetMapping
    public ResponseEntity<?> getAllEarnings() {
        try {
            List<Earnings> earningsList = earningsRepository.findAll();
            return ResponseEntity.ok(earningsList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Retrieves an earnings record by its ID provided as a string.
     * This method attempts to convert the string ID to a long value.
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
                return ResponseEntity.badRequest().body(FAILURE);
            }
            Long numericId = bigIntId.longValue();
            return earningsRepository.findById(numericId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(FAILURE); // Handle malformed number
        }
    }

    /**
     * Create a new earnings record.
     * Validates the request body as an Earnings object.
     * Saves the object to the database and returns a ResponseEntity with the created status and object.
     *
     * @param earnings the earnings to create
     * @return ResponseEntity containing the created earnings and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> createEarnings(@RequestBody Earnings earnings) {
        try {
            if (earnings == null || earnings.getPrimaryMonthlyIncome() < 0 || earnings.getSecondaryMonthlyIncome() < 0) {
                return ResponseEntity.badRequest().body(FAILURE);
            }

            Earnings savedEarnings = earningsRepository.save(earnings);
            return ResponseEntity.ok(savedEarnings);  // Return 200 OK with the saved earnings
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Update an existing earnings record identified by ID.
     * Demonstrates the use of Optional for conditional logic in a functional style.
     * findById fetches the record, map updates it, and changes are saved to the database.
     *
     * @param id      the ID of the earnings to update
     * @param request the updated earnings data
     * @return ResponseEntity indicating success or the absence of the earnings record
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEarnings(@PathVariable Long id, @RequestBody Earnings request) {
        try {
            if (request == null || request.getPrimaryMonthlyIncome() < 0 || request.getSecondaryMonthlyIncome() < 0) {
                return ResponseEntity.badRequest().body(FAILURE);
            }

            return earningsRepository.findById(id)
                    .map(existingEarnings -> {
                        existingEarnings.setPrimaryMonthlyIncome(request.getPrimaryMonthlyIncome());
                        existingEarnings.setSecondaryMonthlyIncome(request.getSecondaryMonthlyIncome());
                        earningsRepository.save(existingEarnings);
                        return ResponseEntity.ok(SUCCESS);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }

    /**
     * Delete an earnings record by its ID.
     * Uses findById to conditionally find and delete the record, demonstrating functional style.
     *
     * @param id the ID of the earnings to delete
     * @return ResponseEntity indicating success or failure based on record presence
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEarnings(@PathVariable Long id) {
        try {
            earningsRepository.findById(id)
                    .ifPresent(earningsRepository::delete);
            return ResponseEntity.ok(SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
    }
}
