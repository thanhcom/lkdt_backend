package thanhcom.site.lkdt.controller;

import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thanhcom.site.lkdt.dto.request.CheckTokenRequest;
import thanhcom.site.lkdt.dto.request.LoginRequest;
import thanhcom.site.lkdt.dto.request.LogoutRequest;
import thanhcom.site.lkdt.dto.request.TokenRefreshRequest;
import thanhcom.site.lkdt.dto.response.TokenResponse;
import thanhcom.site.lkdt.enums.SuccessCode;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.AuthService;

import java.text.ParseException;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/auth")
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    ResponseEntity<?> AccountCheck(@RequestBody @Validated LoginRequest loginRequest)
    {
        TokenResponse login = authService.Login(loginRequest);
        ResponseApi<TokenResponse> responseApi = new ResponseApi<>();
        responseApi.setData(login);
        responseApi.setResponseCode(1001);
        responseApi.setMessenger("Đăng nhập thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/logout")
    ResponseEntity<?> Logout(@RequestBody @Validated LogoutRequest request)
    {
        ResponseApi<String> responseApi = new ResponseApi<>();
        try {
            authService.LogOut(request);
            responseApi.setResponseCode(1001);
            responseApi.setMessenger("Đăng xuất thành công");
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/refresh_token")
    ResponseEntity<?> refreshToken(@RequestBody @Validated TokenRefreshRequest request)
    {
        ResponseApi<TokenResponse> responseApi = new ResponseApi<>();
        try {
            responseApi.setData(authService.refresh_token(request));
            responseApi.setMessenger("Refresh Token Cusses !!!");
            responseApi.setResponseCode(1688);
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/check_token")
    ResponseEntity<?> Login(@RequestBody @Validated CheckTokenRequest request)
    {
        try {
            return ResponseEntity.ok(ResponseApi.builder()
                    .Messenger(SuccessCode.USER_LOGIN_OKE.getMessage())
                    .ResponseCode(SuccessCode.USER_LOGIN_OKE.getCode())
                    .data(authService.CheckToken(request))
                    .ResponseCode(SuccessCode.TOKEN_CHECK_OK.getCode())
                    .Messenger(SuccessCode.TOKEN_CHECK_OK.getMessage())
                    .build());
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
