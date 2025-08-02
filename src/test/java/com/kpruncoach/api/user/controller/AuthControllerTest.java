package com.kpruncoach.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpruncoach.api.user.dto.LoginRequest;
import com.kpruncoach.api.user.dto.LoginResponse;
import com.kpruncoach.api.user.dto.PasswordResetConfirmRequest;
import com.kpruncoach.api.user.dto.PasswordResetRequest;
import com.kpruncoach.api.user.dto.RegisterRequest;
import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.model.UserRole;
import com.kpruncoach.api.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

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

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRole(UserRole.COACH);
        mockUser.setEmailVerified(true);
    }

    @Test
    void registerCoach_Success() throws Exception {
        // Given
        when(authService.registerCoach(any(RegisterRequest.class))).thenReturn(mockUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register/coach")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful. Please check your email to verify your account."))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(authService).registerCoach(any(RegisterRequest.class));
    }

    @Test
    void registerCoach_ValidationError() throws Exception {
        // Given - Invalid request with missing required fields
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/auth/register/coach")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerCoach(any(RegisterRequest.class));
    }

    @Test
    void registerCoach_ServiceException() throws Exception {
        // Given
        when(authService.registerCoach(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already registered"));

        // When & Then
        mockMvc.perform(post("/api/auth/register/coach")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService).registerCoach(any(RegisterRequest.class));
    }

    @Test
    void login_Success() throws Exception {
        // Given
        LoginResponse loginResponse = new LoginResponse("jwt-token", "john.doe@example.com", 
                "John", "Doe", "COACH");
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.role").value("COACH"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void requestPasswordReset_Success() throws Exception {
        // Given
        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setEmail("john.doe@example.com");
        
        doNothing().when(authService).requestPasswordReset(any(PasswordResetRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset email sent if account exists"));

        verify(authService).requestPasswordReset(any(PasswordResetRequest.class));
    }

    @Test
    void confirmPasswordReset_Success() throws Exception {
        // Given
        PasswordResetConfirmRequest confirmRequest = new PasswordResetConfirmRequest();
        confirmRequest.setToken("reset-token");
        confirmRequest.setNewPassword("newPassword123");
        
        doNothing().when(authService).confirmPasswordReset(any(PasswordResetConfirmRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/password-reset/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        verify(authService).confirmPasswordReset(any(PasswordResetConfirmRequest.class));
    }

    @Test
    void verifyEmail_Success() throws Exception {
        // Given
        String token = "verification-token";
        doNothing().when(authService).verifyEmail(token);

        // When & Then
        mockMvc.perform(get("/api/auth/verify-email")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"));

        verify(authService).verifyEmail(token);
    }

    @Test
    void verifyEmail_InvalidToken() throws Exception {
        // Given
        String token = "invalid-token";
        doThrow(new RuntimeException("Invalid verification token"))
                .when(authService).verifyEmail(token);

        // When & Then
        mockMvc.perform(get("/api/auth/verify-email")
                .param("token", token))
                .andExpect(status().isInternalServerError());

        verify(authService).verifyEmail(token);
    }
}
