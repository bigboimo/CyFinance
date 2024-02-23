package com.example.demo.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findById(int id);

    User findByEmail(String email);

    @Transactional
    void deleteById(int id);
}
