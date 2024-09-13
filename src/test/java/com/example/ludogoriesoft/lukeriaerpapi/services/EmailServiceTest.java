package com.example.ludogoriesoft.lukeriaerpapi.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;  // Мокваме JavaMailSender

    @InjectMocks
    private EmailService emailService;  // Инжектираме мокнатия обект в EmailService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Инициализиране на Mockito
    }

    @Test
    void sendHtmlEmail_Success() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        emailService.sendHtmlEmail(toEmail, subject, body);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
    @Test
    void sendHtmlEmail_Failure() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("Email sending failed")).when(mailSender).send(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        emailService.sendHtmlEmail(toEmail, subject, body);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);

        verify(mailSender).send(mimeMessage);
    }
}