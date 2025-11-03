package thanhcom.site.lkdt.config;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.responseApi.ResponseApi;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrCode errorCode = ErrCode.USER_PASSWORD_NOT_MATH;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseApi<?> apiResponse = ResponseApi.builder()
                .ResponseCode(errorCode.getCode())
                .Messenger(errorCode.getMessage())
                .Author("Copyright 2025 thanhtrang.online")
                //.timestamp(LocalDateTime.now())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}