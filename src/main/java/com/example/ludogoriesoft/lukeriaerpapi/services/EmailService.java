package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.CustomEmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LogManager.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String mail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String mail) {
        this.mailSender = mailSender;
        this.mail = mail;
    }


    public void sendHtmlEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Messaging error occurred while sending email to {} with subject {}", toEmail, subject, e);
            throw new CustomEmailException("Messaging error occurred", e);
        } catch (MailException e) {
            logger.error("Mail server error occurred while sending email to {} with subject {}", toEmail, subject, e);
            throw new CustomEmailException("Mail server error occurred", e);
        }
    }
}
