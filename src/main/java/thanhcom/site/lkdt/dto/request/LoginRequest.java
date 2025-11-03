package thanhcom.site.lkdt.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    //Truyền Key Enum ERR ĐỂ Bắn Ra Exception
    @Size(min = 8,message = "USER_USERNAME_SIZE")
    private String username;
    @Size(min = 8,message = "USER_PASSWORD_SIZE")
    private String password;
}