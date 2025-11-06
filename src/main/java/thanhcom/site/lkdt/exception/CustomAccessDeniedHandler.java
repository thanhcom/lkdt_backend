package thanhcom.site.lkdt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.responseApi.ResponseApi;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ResponseApi<Void> resp = new ResponseApi<>();
        resp.setResponseCode(ErrCode.UNAUTHORIZED.getCode());
        resp.setMessenger("Bạn không có quyền truy cập tài nguyên này.");

        response.getWriter().write(objectMapper.writeValueAsString(resp));
    }
}
