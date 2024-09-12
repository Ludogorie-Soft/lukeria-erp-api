package com.example.ludogoriesoft.lukeriaerpapi.models;

import org.springframework.stereotype.Component;

@Component
public class EmailContentBuilder {

    public String buildResetPasswordEmail(String frontendUrl, String token) {
        String resetLink = frontendUrl + "/user/reset-password?token=" + token;

        return  "<html>" +
                "<body>" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "<h2 style='text-align: center; color: #333;'>Приложение на Лукерия ООД</h2>" +
                "<p style='font-size: 16px; color: #555;'>Здравейте,</p>" +
                "<p style='font-size: 16px; color: #555;'>Получихме заявка за възстановяване на паролата за вашия акаунт.</p>" +
                "<p style='font-size: 16px; color: #555;'>Моля, натиснете бутона по-долу, за да създадете нова парола:</p>" +
                "<div style='text-align: center; margin: 20px 0;'>" +
                "<a href='" + resetLink + "' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-size: 16px;'>Създай нова парола</a>" +
                "</div>" +
                "<p style='font-size: 14px; color: #888;'>Линка ще бъде активен 60 минути след получаването му.</p>" +
                "<p style='font-size: 14px; color: #888;'>Ако не сте заявили възстановяване на парола, игнорирайте този имейл.</p>" +
                "<p style='font-size: 14px; color: #888;'>С уважение,<br/>Екипът на Лукерия ООД</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
