package com.kpruncoach.api.user.repository;

import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.COACH);
        testUser.setEmailVerified(false);
        testUser.setEmailVerificationToken("verification-token");
        testUser.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        testUser.setPasswordResetToken("reset-token");
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        testUser.setFailedLoginAttempts(0);
        testUser.setAccountLocked(false);
    }

    @Test
    void save_ShouldPersistUser() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getFirstName(), savedUser.getFirstName());
        assertEquals(testUser.getLastName(), savedUser.getLastName());
        assertEquals(testUser.getRole(), savedUser.getRole());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals(testUser.getEmail(), found.get().getEmail());
        assertEquals(testUser.getFirstName(), found.get().getFirstName());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void findByEmailVerificationToken_ShouldReturnUser_WhenTokenExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> found = userRepository.findByEmailVerificationToken("verification-token");

        // Then
        assertTrue(found.isPresent());
        assertEquals(testUser.getEmail(), found.get().getEmail());
        assertEquals("verification-token", found.get().getEmailVerificationToken());
    }

    @Test
    void findByEmailVerificationToken_ShouldReturnEmpty_WhenTokenDoesNotExist() {
        // When
        Optional<User> found = userRepository.findByEmailVerificationToken("nonexistent-token");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findByPasswordResetToken_ShouldReturnUser_WhenTokenExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> found = userRepository.findByPasswordResetToken("reset-token");

        // Then
        assertTrue(found.isPresent());
        assertEquals(testUser.getEmail(), found.get().getEmail());
        assertEquals("reset-token", found.get().getPasswordResetToken());
    }

    @Test
    void findByPasswordResetToken_ShouldReturnEmpty_WhenTokenDoesNotExist() {
        // When
        Optional<User> found = userRepository.findByPasswordResetToken("nonexistent-token");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void update_ShouldUpdateTimestamp() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        LocalDateTime originalUpdatedAt = savedUser.getUpdatedAt();

        // Wait a moment to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        savedUser.setFirstName("Jane");
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals("Jane", updatedUser.getFirstName());
        assertNotNull(updatedUser.getUpdatedAt());
        assertTrue(updatedUser.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void emailConstraint_ShouldEnforceUniqueness() {
        // Given
        entityManager.persistAndFlush(testUser);

        User duplicateUser = new User();
        duplicateUser.setFirstName("Jane");
        duplicateUser.setLastName("Smith");
        duplicateUser.setEmail("john.doe@example.com"); // Same email
        duplicateUser.setPassword("anotherPassword");
        duplicateUser.setRole(UserRole.COACH);

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }
}
