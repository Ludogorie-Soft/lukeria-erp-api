package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.CustomEmailException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertThrows;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendHtmlEmail_ForForgotPassword_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        emailService.sendHtmlEmailForForgotPassword(toEmail, subject, body);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
    @Test
    void sendHtmlEmail_ForForgotPassword_Failure() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailException("Email sending failed") {}).when(mailSender).send(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        assertThrows(CustomEmailException.class, () -> {
            emailService.sendHtmlEmailForForgotPassword(toEmail, subject, body);
        });

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendHtmlEmailWithProductReport_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        emailService.sendHtmlEmailWithProductReport(List.of("test@mail.com"), subject, body);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
    @Test
    void sendHtmlEmailWithProductReport_Failure() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailException("Email sending failed") {}).when(mailSender).send(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        assertThrows(CustomEmailException.class, () -> {
            emailService.sendHtmlEmailWithProductReport(List.of("test@mail.com"), subject, body);
        });

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
    @Test
    void generateProductStockReportById_Failure() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailException("Email sending failed") {}).when(mailSender).send(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        assertThrows(CustomEmailException.class, () -> {
            emailService.sendProductStockReportById(List.of("test@mail.com"), subject, body);
        });

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
    @Test
    void generateProductStockReportById_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "<h1>This is a test email</h1>";

        emailService.sendProductStockReportById(List.of("test@mail.com"), subject, body);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
}