package com.example.demo.expenses;

import com.example.demo.netWorth.NetWorth;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>{
    Expenses findById(int id);

    @Transactional
    void deleteById(int id);
}
