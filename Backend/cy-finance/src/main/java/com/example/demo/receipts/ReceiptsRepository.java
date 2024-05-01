package com.example.demo.receipts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptsRepository extends JpaRepository<Receipts, Long> {
    Receipts findById(int id);
}
