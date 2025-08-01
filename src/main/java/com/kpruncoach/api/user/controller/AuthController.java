package com.kpruncoach.api.user.controller;

import com.kpruncoach.api.user.dto.LoginRequest;
import com.kpruncoach.api.user.dto.LoginResponse;
import com.kpruncoach.api.user.dto.PasswordResetRequest;
import com.kpruncoach.api.user.dto.PasswordResetConfirmRequest;
import com.kpruncoach.api.user.dto.RegisterRequest;
import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/coach")
    public ResponseEntity<?> registerCoach(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerCoach(request);
        return ResponseEntity.ok(Map.of(
            "message", "Registration successful. Please check your email to verify your account.",
            "email", user.getEmail()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent if account exists"));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }
}
