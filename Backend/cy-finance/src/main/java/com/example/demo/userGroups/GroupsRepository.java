package com.example.demo.userGroups;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupsRepository extends JpaRepository<Groups, Long> {
    Groups findById(int id);

    Groups findByName(String name);

    @Transactional
    void deleteById(int id);
}
