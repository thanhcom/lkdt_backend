package thanhcom.site.lkdt.dto.request;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenRefreshRequest {
    String refresh_token;
}