package com.kpruncoach.api.user.service;

import com.kpruncoach.api.user.dto.LoginRequest;
import com.kpruncoach.api.user.dto.LoginResponse;
import com.kpruncoach.api.user.dto.PasswordResetRequest;
import com.kpruncoach.api.user.dto.PasswordResetConfirmRequest;
import com.kpruncoach.api.user.dto.RegisterRequest;
import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.model.UserRole;
import com.kpruncoach.api.user.repository.UserRepository;
import com.kpruncoach.security.jwt.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 30;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtils jwtUtils,
                      EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    @Transactional
    public User registerCoach(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.COACH);
        user.setEmailVerificationToken(generateToken());
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        user = userRepository.save(user);

        emailService.sendVerificationEmail(
            user.getEmail(),
            user.getEmailVerificationToken()
        );

        return user;
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (LocalDateTime.now().isAfter(user.getEmailVerificationTokenExpiry())) {
            throw new RuntimeException("Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new RuntimeException("Account is locked due to too many failed login attempts");
        }

        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please check your email for verification link.");
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setLastLoginAttempt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtils.generateJwtToken(user.getEmail());
        return new LoginResponse(
            token,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole().name()
        );
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = generateToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (LocalDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        userRepository.save(user);
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        user.setLastLoginAttempt(LocalDateTime.now());

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
        }

        userRepository.save(user);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
