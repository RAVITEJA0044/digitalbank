package com.digitalbank.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

   @Value("${jwt.secret}")
private String secretKey;

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60; // 1 hour

    // =========================
    // SIGNING KEY
    // =========================

   private SecretKey getSigningKey() {

    return Keys.hmacShaKeyFor(
            secretKey.getBytes(StandardCharsets.UTF_8)
    );
}

    // =========================
    // GENERATE TOKEN
    // =========================

    public String generateToken(
            String username,
            String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    // =========================
    // EXTRACT ALL CLAIMS
    // =========================

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================
    // EXTRACT USERNAME
    // =========================

    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // =========================
    // EXTRACT ROLE
    // =========================

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    // =========================
    // CHECK TOKEN EXPIRATION
    // =========================

    public boolean isTokenExpired(String token) {

        Date expiration =
                extractAllClaims(token)
                        .getExpiration();

        return expiration.before(new Date());
    }
}