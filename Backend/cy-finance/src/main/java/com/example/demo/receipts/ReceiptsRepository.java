package com.example.demo.receipts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReceiptsRepository extends JpaRepository<Receipts, Long> {
    Receipts findById(int id);

    // Method to find a receipt by label and user's email
    @Query("SELECT r FROM Receipts r WHERE r.label = :label AND r.user.email = :email")
    Optional<Receipts> findByLabelAndUserEmail(@Param("label") String label, @Param("email") String email);

}
