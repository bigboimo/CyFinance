package com.example.demo.assets;

import com.example.demo.users.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AssetsRepository extends JpaRepository<Assets, Long> {
    public Set<Assets> findAssetsByLabelAndUser(String label, User user);

    public Assets findAssetsById(int id);

    @Transactional
    public void deleteAssetsById(int id);

}
