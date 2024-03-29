package com.mycompany.repository;

import com.mycompany.model.CEO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CEORepository extends JpaRepository<CEO, Long> {
    // No need to define any method here as JpaRepository provides basic CRUD methods
}
