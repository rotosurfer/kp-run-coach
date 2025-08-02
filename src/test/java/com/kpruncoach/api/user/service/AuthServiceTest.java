package com.kpruncoach.api.user.service;

import com.kpruncoach.api.user.dto.LoginRequest;
import com.kpruncoach.api.user.dto.LoginResponse;
import com.kpruncoach.api.user.dto.PasswordResetConfirmRequest;
import com.kpruncoach.api.user.dto.PasswordResetRequest;
import com.kpruncoach.api.user.dto.RegisterRequest;
import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.model.UserRole;
import com.kpruncoach.api.user.repository.UserRepository;
import com.kpruncoach.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setRole(UserRole.COACH);
        user.setEmailVerified(true);
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
    }

    @Test
    void registerCoach_Success() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = authService.registerCoach(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(registerRequest.getEmail(), result.getEmail());
        assertEquals(UserRole.COACH, result.getRole());
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertEquals(registerRequest.getFirstName(), savedUser.getFirstName());
        assertEquals(registerRequest.getLastName(), savedUser.getLastName());
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotNull(savedUser.getEmailVerificationToken());
        assertNotNull(savedUser.getEmailVerificationTokenExpiry());
        
        verify(emailService).sendVerificationEmail(eq(registerRequest.getEmail()), anyString());
    }

    @Test
    void registerCoach_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerCoach(registerRequest));
        assertEquals("Email already registered", exception.getMessage());
        
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void login_Success() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtils.generateJwtToken(user.getEmail())).thenReturn("jwt-token");

        // When
        LoginResponse result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getRole().name(), result.getRole());
        
        verify(userRepository).save(user);
        assertEquals(0, user.getFailedLoginAttempts());
        assertNotNull(user.getLastLoginAttempt());
    }

    @Test
    void login_UserNotFound() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_AccountLocked() {
        // Given
        user.setAccountLocked(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("Account is locked due to too many failed login attempts", exception.getMessage());
    }

    @Test
    void login_EmailNotVerified() {
        // Given
        user.setEmailVerified(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("Email not verified. Please check your email for verification link.", exception.getMessage());
    }

    @Test
    void login_InvalidCredentials() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
        
        verify(userRepository).save(user);
        assertEquals(1, user.getFailedLoginAttempts());
    }

    @Test
    void verifyEmail_Success() {
        // Given
        String token = "verification-token";
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        user.setEmailVerified(false);
        
        when(userRepository.findByEmailVerificationToken(token)).thenReturn(Optional.of(user));

        // When
        authService.verifyEmail(token);

        // Then
        verify(userRepository).save(user);
        assertTrue(user.isEmailVerified());
        assertNull(user.getEmailVerificationToken());
        assertNull(user.getEmailVerificationTokenExpiry());
    }

    @Test
    void verifyEmail_InvalidToken() {
        // Given
        String token = "invalid-token";
        when(userRepository.findByEmailVerificationToken(token)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.verifyEmail(token));
        assertEquals("Invalid verification token", exception.getMessage());
    }

    @Test
    void requestPasswordReset_Success() {
        // Given
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("john.doe@example.com");
        
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        // When
        authService.requestPasswordReset(request);

        // Then
        verify(userRepository).save(user);
        assertNotNull(user.getPasswordResetToken());
        assertNotNull(user.getPasswordResetTokenExpiry());
        verify(emailService).sendPasswordResetEmail(eq(user.getEmail()), anyString());
    }
}
