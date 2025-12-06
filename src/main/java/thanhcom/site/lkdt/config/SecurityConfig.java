package thanhcom.site.lkdt.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/acc/create_user",
            "/test/**"
    };

    private final CustomJwtDecoder customJwtDecoder;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.customJwtDecoder = customJwtDecoder;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // ===== CORS CONFIGURATION =====
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép tất cả origin dev + production + default
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",       // dev frontend
                "http://127.0.0.1:*",       // dev frontend
                "http://api-lkdt.thanhcom.site",  // prod API
                "http://app.thanhcom.site"        // prod frontend
                // default sẽ match origin nếu bạn thêm origin khác
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    // ===== SECURITY FILTER CHAIN =====
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()          // public endpoints
                        .anyRequest().authenticated()                            // endpoint khác require JWT
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(authenticationConverter())
                        )
                );

        // Custom filter bắt lỗi TokenExpiredException / JwtException
        http.addFilterBefore(new JwtException(), BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    // ===== JWT AUTH CONVERTER =====
    @Bean
    JwtAuthenticationConverter authenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("scope");
        authoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    // ===== SWAGGER IGNORE =====
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**");
    }
}
