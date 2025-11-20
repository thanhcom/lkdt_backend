package thanhcom.site.lkdt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Value("${brevo.host}")
    private String resetPasswordHost;

    public void sendResetPasswordEmail(String toEmail, String username, String token) {
        String resetLink = resetPasswordHost+"/reset-password?token=" + token;
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setApiKey(apiKey);
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);
        SendSmtpEmail email = getSendSmtpEmail(toEmail, username, resetLink);
        try {
            apiInstance.sendTransacEmail(email);
            log.info("Email đặt lại mật khẩu đã được gửi tới: {}", toEmail);
        } catch (ApiException e) {
            log.error("Lỗi khi gửi email: {}", e.getResponseBody(), e);
        }
    }

    @NotNull
    private SendSmtpEmail getSendSmtpEmail(String toEmail, String username, String resetLink) {
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName(senderName);

        SendSmtpEmailTo to = new SendSmtpEmailTo();
        to.setEmail(toEmail);
        to.setName(username);

        String htmlContent = "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>Đặt lại mật khẩu</title>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin:0; padding:0; }" +
                "  .container { max-width: 600px; margin: 30px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                "  .btn { display: inline-block; padding: 12px 25px; font-size: 16px; color: #fff; background-color: #1e88e5; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                "  .footer { margin-top: 30px; font-size: 12px; color: #888; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Xin chào " + username + ",</h2>" +
                "<p>Bạn vừa yêu cầu <strong>đặt lại mật khẩu</strong> cho tài khoản của mình trên <strong>LKDT</strong>.</p>" +
                "<p>Vui lòng nhấn nút bên dưới để đặt lại mật khẩu. Liên kết chỉ có hiệu lực trong <strong>10 phút</strong>:</p>" +
                "<a class='btn' href='" + resetLink + "'>Đặt lại mật khẩu</a>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.</p>" +
                "<p class='footer'>Trân trọng,<br/>Thanh Trang Electronic </p>" +
                "</div>" +
                "</body>" +
                "</html>";

        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(sender);
        email.setTo(Collections.singletonList(to));
        email.setSubject("Yêu cầu đặt lại mật khẩu LKDT");
        email.setHtmlContent(htmlContent);
        return email;
    }
}
