package com.example.demo.service;

import com.example.demo.model.Evidence;
import com.example.demo.model.User;
import com.example.demo.Repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    // Evidence save பண்ணும்
    public Evidence save(Evidence evidence) {
        return evidenceRepository.save(evidence);
    }

    // User-ஓட எல்லா evidence
    public List<Evidence> findByUser(User user) {
        return evidenceRepository.findByUser(user);
    }

    // Status-ல filter
    public List<Evidence> findByUserAndStatus(User user, String status) {
        return evidenceRepository.findByUserAndStatus(user, status);
    }

    // Status update
    public Evidence updateStatus(Long id, String status) {
        Optional<Evidence> evidence = evidenceRepository.findById(id);
        if (evidence.isPresent()) {
            evidence.get().setStatus(status);
            return evidenceRepository.save(evidence.get());
        }
        throw new RuntimeException("Evidence not found!");
    }

    // Delete
    public void delete(Long id) {
        evidenceRepository.deleteById(id);
    }
}