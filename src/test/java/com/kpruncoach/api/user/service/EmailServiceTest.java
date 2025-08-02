package com.kpruncoach.api.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "appUrl", "https://localhost:3000");
    }

    @Test
    void sendVerificationEmail_ShouldSendCorrectEmail() {
        // Given
        String recipientEmail = "test@example.com";
        String token = "verification-token-123";

        // When
        emailService.sendVerificationEmail(recipientEmail, token);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(recipientEmail, sentMessage.getTo()[0]);
        assertEquals("Verify your email address", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("verify your email address"));
        assertTrue(sentMessage.getText().contains("https://localhost:3000/verify-email?token=" + token));
    }

    @Test
    void sendPasswordResetEmail_ShouldSendCorrectEmail() {
        // Given
        String recipientEmail = "test@example.com";
        String token = "reset-token-456";

        // When
        emailService.sendPasswordResetEmail(recipientEmail, token);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(recipientEmail, sentMessage.getTo()[0]);
        assertEquals("Reset your password", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("reset your password"));
        assertTrue(sentMessage.getText().contains("https://localhost:3000/reset-password?token=" + token));
    }

    @Test
    void sendVerificationEmail_ShouldHandleEmailSendingException() {
        // Given
        String recipientEmail = "test@example.com";
        String token = "verification-token-123";
        doThrow(new RuntimeException("SMTP server unavailable")).when(emailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            emailService.sendVerificationEmail(recipientEmail, token);
        });

        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ShouldHandleEmailSendingException() {
        // Given
        String recipientEmail = "test@example.com";
        String token = "reset-token-456";
        doThrow(new RuntimeException("SMTP server unavailable")).when(emailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetEmail(recipientEmail, token);
        });

        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_ShouldNotSendEmailWithNullRecipient() {
        // Given
        String token = "verification-token-123";

        // When & Then
        assertThrows(Exception.class, () -> {
            emailService.sendVerificationEmail(null, token);
        });
    }

    @Test
    void sendVerificationEmail_ShouldNotSendEmailWithNullToken() {
        // Given
        String recipientEmail = "test@example.com";

        // When & Then
        assertThrows(Exception.class, () -> {
            emailService.sendVerificationEmail(recipientEmail, null);
        });
    }

    @Test
    void emailContent_ShouldContainProperInstructions() {
        // Given
        String recipientEmail = "test@example.com";
        String verificationToken = "verification-token-123";
        String resetToken = "reset-token-456";

        // When
        emailService.sendVerificationEmail(recipientEmail, verificationToken);
        emailService.sendPasswordResetEmail(recipientEmail, resetToken);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(2)).send(messageCaptor.capture());

        SimpleMailMessage verificationMessage = messageCaptor.getAllValues().get(0);
        SimpleMailMessage resetMessage = messageCaptor.getAllValues().get(1);

        // Verification email should contain proper instructions
        assertTrue(verificationMessage.getText().contains("click the link below"));
        assertTrue(verificationMessage.getText().contains("verify your email"));

        // Reset email should contain proper instructions
        assertTrue(resetMessage.getText().contains("click the link below"));
        assertTrue(resetMessage.getText().contains("reset your password"));
    }
}
