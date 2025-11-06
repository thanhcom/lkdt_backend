package thanhcom.site.lkdt.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import thanhcom.site.lkdt.dto.request.CheckTokenRequest;
import thanhcom.site.lkdt.dto.request.LoginRequest;
import thanhcom.site.lkdt.dto.request.LogoutRequest;
import thanhcom.site.lkdt.dto.request.TokenRefreshRequest;
import thanhcom.site.lkdt.dto.response.CheckTokenResponse;
import thanhcom.site.lkdt.dto.response.TokenRefreshResponse;
import thanhcom.site.lkdt.dto.response.TokenResponse;
import thanhcom.site.lkdt.entity.Account;
import thanhcom.site.lkdt.entity.InvalidatedToken;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.exception.TokenExpiredException;
import thanhcom.site.lkdt.repository.AccountRepository;
import thanhcom.site.lkdt.repository.InvalidatedTokenRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    AccountRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.token-time}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refresh-token-time}")
    protected long REFRESHABLE_DURATION;

    public CheckTokenResponse introspect(CheckTokenRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return CheckTokenResponse.builder().tokenIsTrue(isValid).build();
    }

    public TokenResponse authenticate(LoginRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrCode.UNAUTHENTICATED);

        var token = generateToken(user);
        var refreshToken = generateToken(user);

        return TokenResponse.builder().token(token)
                .refresh_token(refreshToken)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getRefresh_token(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return TokenRefreshResponse.builder().refresh_token(token).authenticated(true).build();
    }

    private String generateToken(Account user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("thanhcom.site")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.MINUTES).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new TokenExpiredException("TOKEN_EXPIRED");;

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(Account user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}