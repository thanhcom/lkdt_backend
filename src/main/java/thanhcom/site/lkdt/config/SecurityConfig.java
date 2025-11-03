package thanhcom.site.lkdt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import thanhcom.site.lkdt.exception.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] Public_endpoint ={"/auth","/auth/check_token","/auth/login","/auth/logout","/auth/refresh_token","/acc/create_user","/test/**"};
    //protected static final String key ="GFr3kGYFSz/gxxAmJMy3y8lOWOzhx0+nI8jDDUzRuBvKcajs+IDVdKErGnKeuaJJ";
    @Value("${jwt.signerKey}")
    private String signerKey;

    private final CustomJwtDecoder customJwtDecoder;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    public SecurityConfig(CustomJwtDecoder customJwtDecoder , CustomAccessDeniedHandler accessDeniedHandler) {
        this.customJwtDecoder = customJwtDecoder;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //Trừ các [] Public_endpoint đã định ghĩa ở trên , Tất cả các tài nguyen muốn truy cập cần phải đăng nhập .
        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.GET, Public_endpoint).permitAll()
                .requestMatchers(HttpMethod.POST, Public_endpoint).permitAll()
                .requestMatchers(HttpMethod.PUT, Public_endpoint).permitAll()
                .anyRequest().authenticated()

        ).exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler) // dùng custom handler
        );

        // Cấu Hình JWT với OAuth2 Resource  Server cho phép đăng nhập từ JWT
        http.oauth2ResourceServer(oauth2->oauth2.jwt(jwtCustomizer->
                        jwtCustomizer.decoder(customJwtDecoder)
                                //Chuyển từ SCOPE_ SANG ROLE_
                                .jwtAuthenticationConverter(authenticationConverter()))
                //Bắt lỗi 401 từ FilterChain Bắn Ra
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())

        );

        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    //Chuyển từ SCOPE_ SANG ROLE_
    @Bean
    JwtAuthenticationConverter  authenticationConverter()
    {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }
    //Cấu Hình Cho Phép Chạy OpenAPI
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**");
    }
}