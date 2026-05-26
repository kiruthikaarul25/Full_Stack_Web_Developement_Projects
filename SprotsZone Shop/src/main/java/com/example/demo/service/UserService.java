package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register user
    public String registerUser(User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            return "EMAIL_EXISTS";
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "SUCCESS";
    }

    // Login user
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Compare entered password with encoded DB password
            if (passwordEncoder.matches(password, user.getPassword())) {
                return "SUCCESS";
            }
        }

        return "INVALID";
    }

    // Find user by email
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }
}