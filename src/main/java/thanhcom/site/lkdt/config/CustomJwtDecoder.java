package thanhcom.site.lkdt.config;

import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import thanhcom.site.lkdt.dto.request.CheckTokenRequest;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.exception.TokenExpiredException;
import thanhcom.site.lkdt.service.AuthService;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final AuthService authService;
    private NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(AuthService authService) {
        this.authService = authService;
    }

    @PostConstruct
    private void initDecoder() {
        // Chỉ khởi tạo 1 lần sau khi Spring inject signerKey
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) {
        try {
            // 1️⃣ Kiểm tra token có bị thu hồi hoặc không hợp lệ trong DB/Redis
            var response = authService.introspect(CheckTokenRequest.builder().token(token).build());
            if (!response.isTokenIsTrue()) {
                throw new JwtException("Token invalid or revoked");
            }

            // 2️⃣ Decode JWT
            Jwt jwt = nimbusJwtDecoder.decode(token);

            // 3️⃣ Kiểm tra thời hạn token (expiration)
            Instant exp = jwt.getExpiresAt();
            if (exp == null) {
                throw new JwtException("Token missing expiration claim");
            }
            if (exp.isBefore(Instant.now())) {
                throw new JwtException("Token expired");
            }

            return jwt;

        } catch (TokenExpiredException e) {
            // 🔥 Bắt riêng token hết hạn — để entrypoint hiểu đúng
            throw new JwtException("Token expired", e);

        } catch (AppException e) {
            // 🔥 Các lỗi AppException khác (ví dụ UNAUTHENTICATED, UNAUTHORIZED)
            throw new JwtException(e.getMessage(), e);

        } catch (JwtException e) {
            // Lỗi chữ ký, format JWT
            throw new JwtException("Failed to decode token: " + e.getMessage(), e);

        } catch (JOSEException e) {
            throw new JwtException("Invalid JWT signature: " + e.getMessage(), e);

        } catch (Exception e) {
            // Tránh nuốt lỗi không mong muốn
            throw new JwtException("Unexpected error while decoding token: " + e.getMessage(), e);
        }
    }
}
