package com.mycompany.controller;

import com.mycompany.model.CEO;
import com.mycompany.repository.CEORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ceos")
public class CEOController {

    @Autowired
    private CEORepository ceoRepository;

    // Create a new CEO
    @PostMapping
    public CEO createCEO(@RequestBody CEO ceo) {
        return ceoRepository.save(ceo);
    }

    // Get all CEOs
    @GetMapping
    public List<CEO> getAllCEOs() {
        return ceoRepository.findAll();
    }

    // Get a single CEO by id
    @GetMapping("/{id}")
    public ResponseEntity<CEO> getCEOById(@PathVariable(value = "id") Long ceoId) {
        Optional<CEO> ceo = ceoRepository.findById(ceoId);
        return ceo.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a CEO
    @PutMapping("/{id}")
    public ResponseEntity<CEO> updateCEO(@PathVariable(value = "id") Long ceoId,
                                         @RequestBody CEO ceoDetails) {
        Optional<CEO> optionalCeo = ceoRepository.findById(ceoId);
        if (!optionalCeo.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        CEO ceo = optionalCeo.get();
        ceo.setName(ceoDetails.getName());
        final CEO updatedCEO = ceoRepository.save(ceo);
        return ResponseEntity.ok(updatedCEO);
    }

    // Delete a CEO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCEO(@PathVariable(value = "id") Long ceoId) {
        Optional<CEO> ceo = ceoRepository.findById(ceoId);
        if (!ceo.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ceoRepository.delete(ceo.get());
        return ResponseEntity.ok().build();
    }
}
