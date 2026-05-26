package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email already exists-ஆ check பண்ண
    Optional<User> findByEmail(String email);

    // findByEmailAndPassword நீக்கப்பட்டது — BCrypt matches() use பண்றோம்
}