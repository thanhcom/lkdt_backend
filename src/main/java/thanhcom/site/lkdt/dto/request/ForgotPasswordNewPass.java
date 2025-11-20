package thanhcom.site.lkdt.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordNewPass {
    private String token;
    private String newPassword;
}
