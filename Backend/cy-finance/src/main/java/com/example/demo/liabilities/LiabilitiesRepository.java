package com.example.demo.liabilities;

import com.example.demo.users.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LiabilitiesRepository extends JpaRepository<Liabilities, Long> {
    public Set<Liabilities> findLiabilitiesByLabelAndUser(String label, User user);

    public Liabilities findLiabilitiesById(int id);

    @Transactional
    public void deleteLiabilitiesById(int id);

}
