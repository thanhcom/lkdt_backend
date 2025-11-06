package thanhcom.site.lkdt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;
import thanhcom.site.lkdt.responseApi.ResponseApi;

import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            handleException(response, "Token Expired !!!", 1501);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof JwtException jwtEx) {
                handleException(response, jwtEx.getMessage(), 1501);
            } else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("expired")) {
                handleException(response, "Token Expired !!!", 1501);
            } else {
                throw e;
            }
        }
    }

    private void handleException(HttpServletResponse response, String msg, int code) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ResponseApi<?> apiResponse = ResponseApi.builder()
                .Messenger(msg)
                .Author("Copyright 2025 thanhtrang.online")
                .ResponseCode(code)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}
