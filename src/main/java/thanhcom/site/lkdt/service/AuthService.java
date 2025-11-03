package thanhcom.site.lkdt.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import thanhcom.site.lkdt.dto.request.CheckTokenRequest;
import thanhcom.site.lkdt.dto.request.LoginRequest;
import thanhcom.site.lkdt.dto.request.LogoutRequest;
import thanhcom.site.lkdt.dto.request.TokenRefreshRequest;
import thanhcom.site.lkdt.dto.response.CheckTokenResponse;
import thanhcom.site.lkdt.dto.response.TokenResponse;
import thanhcom.site.lkdt.dto.response.UserResponse;
import thanhcom.site.lkdt.entity.Account;
import thanhcom.site.lkdt.entity.InvalidatedToken;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.mapper.AccountMapper;
import thanhcom.site.lkdt.repository.AccountRepository;
import thanhcom.site.lkdt.repository.InvalidatedTokenRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthService {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;
    public AuthService(AccountRepository accountRepository, AccountMapper accountMapper, PasswordEncoder passwordEncoder, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    //Gán Key Ở FIle Application.yaml

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Value("${jwt.signerKey_refresh}")
    private String signerKey1;

    @Value("${jwt.token-time}")
    private Long TOKEN_TIME;

    @Value("${jwt.refresh-token-time}")
    private Long REFRESH_TOKEN_TIME;

    public Account CheckUser(LoginRequest loginRequest) {
        boolean authenticated;
        Optional<Account> account = accountRepository.findByUsername(loginRequest.getUsername());
        if(account.isEmpty())
        {
            throw new AppException(ErrCode.USER_NOT_EXISTED);
        }
        authenticated = passwordEncoder.matches(loginRequest.getPassword(),account.get().getPassword());
        if(!authenticated) {
            throw new AppException(ErrCode.USER_PASSWORD_NOT_MATH);
        }
        return account.get();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> FindAll() {
        List<Account> all = accountRepository.findAll();
        return accountMapper.ListResToDto(all);
    }

    //2 cách lọc chỉ trả về thông tin nếu trùng với thông tin mình đăng nhập
    //@PostAuthorize("returnObject.username == authentication.name")
    public Account FindById(Long id) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountRepository.findById(id).orElseThrow(()->new AppException(ErrCode.USER_NOT_EXISTED));
        if(!authenticated.getName().equals(account.getUsername())) {
            throw new AppException(ErrCode.USER_VIEW_INFO);
        }
        return account;
    }

    public UserResponse myInfo() {
        //Lấy Thông Tin Đăng Nhập Bằng SecurityContextHolder
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        if (authenticated instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Object scopeValue = jwt.getClaims().get("scope");
            Object issValue = jwt.getClaims().get("iss");
            Object jtiValue = jwt.getClaims().get("jti");
            Object typeValue = jwt.getClaims().get("type");
            System.out.println("scope: " + scopeValue);
            System.out.println("iss: " + issValue);
            System.out.println("jti: " + jtiValue);
            System.out.println("type: " + typeValue);
        }

        System.out.println("authenticated :"+authenticated);
        return accountMapper.ResToDto(accountRepository.findByUsername(authenticated.getName()).orElseThrow(()->new AppException(ErrCode.USER_NOT_EXISTED)));
    }

    public TokenResponse Login(LoginRequest loginRequest)
    {
        boolean authenticated;
        String jwt_id = UUID.randomUUID().toString();
        Optional<Account> account =  accountRepository.findByUsername(loginRequest.getUsername());
        if(account.isPresent())
        {
            authenticated = passwordEncoder.matches(loginRequest.getPassword(),account.get().getPassword());
        }else
        {
            throw new AppException(ErrCode.USER_NOT_EXISTED);
        }

        if(!authenticated)
        {
            throw new AppException(ErrCode.USER_PASSWORD_NOT_MATH);
        }
        return TokenResponse.builder()
                .token(GeneratorToken(account.get(),jwt_id))
                .refresh_token(GeneratorRefreshToken(account.get(),jwt_id))
                .build();
    }

    public TokenResponse refresh_token(TokenRefreshRequest request) throws ParseException, JOSEException
    {
        var token = verifyToken(request.getRefresh_token(),true);
        Optional<Account> account =  accountRepository.findByUsername(token.getJWTClaimsSet().getSubject());
        String jwt_id = token.getJWTClaimsSet().getJWTID();
        return TokenResponse.builder()
                .token(GeneratorToken(account.orElseThrow(),jwt_id))
                .build();

    }

    public void LogOut(LogoutRequest request) throws ParseException, JOSEException {
        var signToken =  verifyToken(request.getToken(),false);
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(LocalDateTime.ofInstant(expiryTime.toInstant(), ZoneId.systemDefault()))
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    public CheckTokenResponse CheckToken(CheckTokenRequest introspectRequest) throws JOSEException, ParseException
    {
        var token = introspectRequest.getToken();
        boolean isValid = true;
        try {
            verifyToken(token,false);
        } catch (Exception e) {
            isValid = false;
        }
        return CheckTokenResponse.builder()
                .tokenIsTrue(isValid)
                .build();
    }


    private String GeneratorToken(Account username,String jwt_id)
    {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username.getUsername())
                .issuer("thanhcom.site")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(TOKEN_TIME,ChronoUnit.MINUTES).toEpochMilli()
                ))
                .claim("type", "token")
                //JWT ID Để Log Out
                .jwtID(jwt_id)
                //Claim scope này giúp phân quyền
                .claim("scope", buildScope(username))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jWSObject = new JWSObject(header, payload);

        //Ki token
        try {
            jWSObject.sign(new MACSigner(signerKey));
            return jWSObject.serialize();
        } catch (JOSEException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String GeneratorRefreshToken(Account username,String jwt_id)
    {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username.getUsername())
                .issuer("thanhcom.site")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(REFRESH_TOKEN_TIME,ChronoUnit.DAYS).toEpochMilli()
                ))
                .claim("name", "refresh_token")
                .jwtID(jwt_id)
                .claim("scope", buildScope(username))
                .build();
        var payload = new Payload(claimsSet.toJSONObject());
        JWSObject jWSObject = new JWSObject(header, payload);

        //Ki token
        try {
            jWSObject.sign(new MACSigner(signerKey1));
            return jWSObject.serialize();
        } catch (JOSEException e)
        {
            throw new RuntimeException(e);
        }
    }
    //VerifyToken đúng trả về JWSVerifier từ 1 token
    private SignedJWT verifyToken(String token,boolean refresh_token) throws JOSEException, ParseException {
        JWSVerifier verifier;
        if(!refresh_token) {
            verifier = new MACVerifier(signerKey.getBytes());
        }else {
            verifier = new MACVerifier(signerKey1.getBytes());
        }

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrCode.UNAUTHORIZED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrCode.UNAUTHORIZED);

        return signedJWT;
    }

    //buildScope Để Phân Quyền
    private String buildScope(Account a)
    {
        StringJoiner joiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(a.getRoles()))
            a.getRoles().forEach(action-> {
                //Add Role to Scope
                joiner.add(action.getName());
                if(!CollectionUtils.isEmpty(action.getPermissions()))
                    action.getPermissions().forEach(item->{
                        //Add Permission To Scope
                        joiner.add("_"+item.getName());
                    });
            });
        return joiner.toString();
    }

}