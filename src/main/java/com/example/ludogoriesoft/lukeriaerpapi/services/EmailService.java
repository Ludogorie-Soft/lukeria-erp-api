package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.CustomEmailException;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailService {
    private static final Logger logger = LogManager.getLogger(EmailService.class);

    private final JavaMailSender mailSender;


    public void sendHtmlEmailWithProductReport(List<String> emailList, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("berki07092@gmail.com");
            helper.setTo(emailList.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            logger.info("Успешно изпратихме доклад за продуктите на: {}", emailList);

        } catch (MessagingException e) {
            logger.error("Грешка при изпращане на имейл до {} с тема {}", emailList, subject, e);
            throw new CustomEmailException("Възникна грешка при изпращането на имейла", e);
        } catch (MailException e) {
            logger.error("Грешка в мейл сървъра при изпращане на имейл до {} с тема {}", emailList, subject, e);
            throw new CustomEmailException("Възникна проблем с мейл сървъра", e);
        }
    }


    public void sendHtmlEmailForForgotPassword(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("berki07092@gmail.com");
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
