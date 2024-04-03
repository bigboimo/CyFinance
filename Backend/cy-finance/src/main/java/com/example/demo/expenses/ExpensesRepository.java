package com.example.demo.expenses;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>{
    Expenses findById(int id);

    @Transactional
    void deleteById(int id);
}
