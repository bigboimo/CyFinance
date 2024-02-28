package com.example.demo.netWorth;

import com.example.demo.earnings.Earnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface NetWorthRepository extends JpaRepository<NetWorth, Long> {
    NetWorth findById(int id);

    @Transactional
    void deleteById(int id);
}
