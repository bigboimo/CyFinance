package com.example.demo.earnings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Retrieve a specific earnings record by ID using a functional approach.
     * findById returns an Optional, which is handled functionally with map and orElseGet.
     * @param id the ID of the earnings record
     * @return ResponseEntity containing the earnings or a Not Found status
     */
    @GetMapping("/{id}")
    public ResponseEntity<Earnings> getEarningsById(@PathVariable Long id) {
        return earningsRepository.findById(id)
                .map(ResponseEntity::ok)  // Directly map to ResponseEntity if present
                .orElseGet(() -> ResponseEntity.notFound().build());  // Handle absent case functionally
    }

    /**
     * Create a new earnings record.
     * Validates the request body as an Earnings object.
     * If the object is valid, it saves it and returns a success response.
     * @param earnings the earnings to create
     * @return ResponseEntity with a success message
     */
    @PostMapping
    public ResponseEntity<String> createEarnings(@RequestBody Earnings earnings) {
        if (earnings == null) {
            return ResponseEntity.badRequest().body(FAILURE);
        }
        earningsRepository.save(earnings);
        return ResponseEntity.ok(SUCCESS);
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
        return earningsRepository.findById(id)
                .map(existingEarnings -> {
                    existingEarnings.setPrimaryMonthlyIncome(request.getPrimaryMonthlyIncome());
                    existingEarnings.setSecondaryMonthlyIncome(request.getSecondaryMonthlyIncome());
                    earningsRepository.save(existingEarnings);
                    return ResponseEntity.ok(SUCCESS);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());  // If not found, return Not Found response
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
