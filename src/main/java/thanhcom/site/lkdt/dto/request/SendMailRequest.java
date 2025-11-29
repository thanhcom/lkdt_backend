package thanhcom.site.lkdt.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class SendMailRequest {
    String htmlContent;
    String subject;
    List<String> toEmail;
}
