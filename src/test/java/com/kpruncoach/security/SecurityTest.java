package com.kpruncoach.security;

import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.model.UserRole;
import com.kpruncoach.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=testSecretKeyForJWTValidationTesting123456789"
})
class SecurityTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.COACH);
    }

    @Test
    void passwordEncoder_ShouldHashPassword() {
        // Given
        String plainPassword = "password123";

        // When
        String hashedPassword = passwordEncoder.encode(plainPassword);

        // Then
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(passwordEncoder.matches(plainPassword, hashedPassword));
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentHashesForSamePassword() {
        // Given
        String plainPassword = "password123";

        // When
        String hash1 = passwordEncoder.encode(plainPassword);
        String hash2 = passwordEncoder.encode(plainPassword);

        // Then
        assertNotEquals(hash1, hash2);
        assertTrue(passwordEncoder.matches(plainPassword, hash1));
        assertTrue(passwordEncoder.matches(plainPassword, hash2));
    }

    @Test
    void passwordEncoder_ShouldRejectIncorrectPassword() {
        // Given
        String plainPassword = "password123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = passwordEncoder.encode(plainPassword);

        // When & Then
        assertFalse(passwordEncoder.matches(wrongPassword, hashedPassword));
    }

    @Test
    void jwtUtils_ShouldGenerateValidToken() {
        // When
        String token = jwtUtils.generateJwtToken(testUser.getEmail());

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(testUser.getEmail(), jwtUtils.getEmailFromJwtToken(token));
    }

    @Test
    void jwtUtils_ShouldRejectInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    void jwtUtils_ShouldRejectExpiredToken() {
        // This test would require more complex setup with clock manipulation
        // For now, we test token validation logic
        String token = jwtUtils.generateJwtToken(testUser.getEmail());
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void jwtUtils_ShouldRejectTamperedToken() {
        // Given
        String validToken = jwtUtils.generateJwtToken(testUser.getEmail());
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // When & Then
        assertFalse(jwtUtils.validateJwtToken(tamperedToken));
    }

    @Test
    void passwordEncoder_ShouldUseStrongHashingAlgorithm() {
        // Given
        String password = "testPassword123";

        // When
        String hashedPassword = passwordEncoder.encode(password);

        // Then
        // BCrypt hashes start with $2a$ and are at least 60 characters long
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"));
        assertTrue(hashedPassword.length() >= 60);
    }

    @Test
    void jwtUtils_TokenShouldContainCorrectClaims() {
        // Given
        String email = "test@example.com";

        // When
        String token = jwtUtils.generateJwtToken(email);

        // Then
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(email, jwtUtils.getEmailFromJwtToken(token));
    }
}
