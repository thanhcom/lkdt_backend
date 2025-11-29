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
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import thanhcom.site.lkdt.exception.CustomAccessDeniedHandler;
import thanhcom.site.lkdt.exception.JwtException;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] Public_endpoint = {
            "/auth", "/auth/check_token", "/auth/login",
            "/auth/logout", "/auth/refresh_token",
            "/acc/create_user", "/test/**"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final CustomJwtDecoder customJwtDecoder;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder, CustomAccessDeniedHandler accessDeniedHandler) {
        this.customJwtDecoder = customJwtDecoder;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // ===== CORS CONFIGURATION =====
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*")); // cho phép bất kỳ port localhost
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // ===== SECURITY FILTER CHAIN =====
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors() // bật CORS
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, Public_endpoint).permitAll()
                        .requestMatchers(HttpMethod.POST, Public_endpoint).permitAll()
                        .requestMatchers(HttpMethod.PUT, Public_endpoint).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // lỗi 401
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtCustomizer -> jwtCustomizer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(authenticationConverter())
                        )
                );

        // Thêm filter custom để bắt lỗi TokenExpiredException / JwtException
        http.addFilterBefore(new JwtException(), BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    // ===== JWT AUTH CONVERTER =====
    @Bean
    JwtAuthenticationConverter authenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("scope"); // đọc claim "scope" từ token
        authoritiesConverter.setAuthorityPrefix(""); // token đã có ROLE_, không thêm prefix
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    // ===== SWAGGER IGNORE =====
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**");
    }
}
