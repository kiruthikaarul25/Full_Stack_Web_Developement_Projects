package com.example.demo.Repository;

import com.example.demo.model.Evidence;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    
    List<Evidence> findByUser(User user);
    List<Evidence> findByUserAndStatus(User user, String status);
    List<Evidence> findByUserAndPlatform(User user, String platform);
    boolean existsByFoundUrl(String foundUrl);
}