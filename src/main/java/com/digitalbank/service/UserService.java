package com.digitalbank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digitalbank.entity.Customer;
import com.digitalbank.entity.User;
import com.digitalbank.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // REGISTER USER
    // =========================
    public User registerUser(User user) {

        if (user.getUsername() == null
                || user.getUsername().isBlank()) {

            throw new RuntimeException(
                    "Username is required");
        }

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required");
        }

        if (userRepository.existsByUsername(
                user.getUsername())) {

            throw new RuntimeException(
                    "Username already exists: "
                    + user.getUsername());
        }

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        // Default role
        if (user.getRole() == null
                || user.getRole().isBlank()) {

            user.setRole("CUSTOMER");
        }

        return userRepository.save(user);
    }

    // =========================
    // FIND USER
    // =========================
    public User findByUsername(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(()
                        -> new RuntimeException(
                        "User not found"));
    }

    public Customer getCustomerByUsername(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(()
                        -> new RuntimeException("User not found"));

        if (user.getCustomer() == null) {
            throw new RuntimeException(
                    "User is not linked to a customer");
        }

        return user.getCustomer();
    }

    // =========================
// RESET PASSWORD
// =========================
    public void resetPassword(String username, String newPassword) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(()
                        -> new RuntimeException("User not found"));

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }

  
}
