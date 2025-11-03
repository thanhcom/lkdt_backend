package thanhcom.site.lkdt.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CheckTokenResponse {
    boolean tokenIsTrue;
}