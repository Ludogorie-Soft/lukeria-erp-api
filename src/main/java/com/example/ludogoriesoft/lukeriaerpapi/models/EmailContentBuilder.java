package com.example.ludogoriesoft.lukeriaerpapi.models;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailContentBuilder {
    public String generateStockReportEmail(List<Product> productList) {
        StringBuilder emailContent = new StringBuilder();

        // Начало на HTML съдържанието
        emailContent.append("<html>")
                .append("<body>")
                .append("<h2>Доклад за наличност на продукти</h2>")
                .append("<table border='1' cellpadding='10' cellspacing='0'>")
                .append("<tr>")
                .append("<th>Код на продукта</th>")
                .append("<th>Име на продукт</th>")
                .append("<th>Налично количество на готови продукти</th>")
                .append("<th>Налично количество на кутии</th>")
                .append("<th>Налично количество на тарелки</th>")
                .append("<th>Налично количество на кашони</th>")
                .append("</tr>");

        for (Product product : productList) {
            emailContent.append("<tr>");
            emailContent.append("<td>").append(product.getPackageId().getProductCode() != null ? product.getPackageId().getProductCode() : "Без код").append("</td>");
            emailContent.append("<td>").append(product.getPackageId().getName() != null ? product.getPackageId().getName() : "Без име").append("</td>");

            if (product.getPackageId() != null) {
                emailContent.append("<td>").append(product.getAvailableQuantity()).append("</td>");
            } else {
                emailContent.append("<td>Няма информация</td>");
            }
            if (product.getPackageId() != null) {
                emailContent.append("<td>").append(product.getPackageId().getAvailableQuantity()).append("</td>");
            } else {
                emailContent.append("<td>Няма информация</td>");
            }

            if (product.getPackageId() != null && product.getPackageId().getPlateId() != null) {
                emailContent.append("<td>").append(product.getPackageId().getPlateId().getAvailableQuantity()).append("</td>");
            } else {
                emailContent.append("<td>Няма информация</td>");
            }

            if (product.getPackageId() != null && product.getPackageId().getCartonId() != null) {
                emailContent.append("<td>").append(product.getPackageId().getCartonId().getAvailableQuantity()).append("</td>");
            } else {
                emailContent.append("<td>Няма информация</td>");
            }

            emailContent.append("</tr>");
        }

        emailContent.append("</table>")
                .append("</body>")
                .append("</html>");

        return emailContent.toString();
    }


    public String buildResetPasswordEmail(String frontendUrl, String token) {
        String resetLink = frontendUrl + "/user/reset-password?token=" + token;

        return "<html>" +
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
