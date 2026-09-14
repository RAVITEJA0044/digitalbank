package com.digitalbank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbank.entity.User;
import com.digitalbank.service.JwtService;
import com.digitalbank.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user) {

        User savedUser =
                userService.registerUser(user);

        return ResponseEntity.ok(savedUser);
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password) {

        User user =
                userService.findByUsername(username);

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid username or password");
        }

        String token =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole()
                );

        Map<String, Object> response =
                new HashMap<>();

        response.put("message", "Login successful");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}

