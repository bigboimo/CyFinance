package com.example.demo.earnings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface EarningsRepository extends JpaRepository<Earnings, Long> {
    Earnings findById(int id);

    @Transactional
    void deleteById(int id);
}
