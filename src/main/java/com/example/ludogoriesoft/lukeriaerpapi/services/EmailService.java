package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.CustomEmailException;
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

  private void sendEmail(List<String> emailList, String subject, String body) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom("berki07092@gmail.com");
      helper.setTo(emailList.toArray(new String[0]));
      helper.setSubject(subject);
      helper.setText(body, true);

      mailSender.send(message);
      logger.info("Successfully sent email to: {}", emailList);

    } catch (MessagingException e) {
      logger.error("Error while sending email to {} with subject {}", emailList, subject, e);
      throw new CustomEmailException("An error occurred while sending the email", e);
    } catch (MailException e) {
      logger.error("Mail server error while sending email to {} with subject {}", emailList, subject, e);
      throw new CustomEmailException("An issue occurred with the mail server", e);
    }
  }

  public void sendProductStockReportById(List<String> emailList, String subject, String body) {
    sendEmail(emailList, subject, body);
  }
  public void sendHtmlEmailWithProductReport(List<String> emailList, String subject, String body) {
    sendEmail(emailList, subject, body);
  }
  public void sendHtmlEmailForForgotPassword(String toEmail, String subject, String body) {
    sendEmail(List.of(toEmail), subject, body);
  }
}