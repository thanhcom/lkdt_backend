package thanhcom.site.lkdt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String message = authException.getMessage();
        if (authException.getCause() != null) {
            message = authException.getCause().getMessage();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("author", "Copyright 2025 thanhtrang.online");

        // 🔥 Nhận biết chính xác lỗi token
        if (message != null) {
            String lower = message.toLowerCase();
            if (lower.contains("expired")) {
                body.put("messenger", "Token Expired !!!");
                body.put("responseCode", 1501);
            } else if (lower.contains("invalid")) {
                body.put("messenger", "Token Invalid !!!");
                body.put("responseCode", 1502);
            } else if (lower.contains("unauthorized")) {
                body.put("messenger", "Warning !!! UNAUTHORIZED");
                body.put("responseCode", 9999);
            } else {
                body.put("messenger", "Authentication failed !!!");
                body.put("responseCode", 1999);
            }
        } else {
            body.put("messenger", "Authentication failed !!!");
            body.put("responseCode", 1999);
        }

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
