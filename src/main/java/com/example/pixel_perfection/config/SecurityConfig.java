package com.example.pixel_perfection.config;

import com.example.pixel_perfection.service.CustomOAuth2UserService;
import com.example.pixel_perfection.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;

        @Value("${jwt.base64-secret}")
        private String jwtKey;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(csrf -> csrf.disable())
                        .cors(Customizer.withDefaults())
                        .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(
                                        "/login/**",
                                        "/oauth2/**",
                                        "/error",
                                        "/hello",
//                                        "/api/auth/google",
                                        "/api/auth/login/**",
                                        "/api/public/**"
                                ).permitAll()
                                .anyRequest().authenticated()
                        )
                        .oauth2Login(oauth2 -> oauth2
                                .loginPage("/login")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                                // false để redirect về trang người dùng yêu cầu ban đầu nếu có
                                .defaultSuccessUrl("/api/auth/login/success", false)
                                .failureUrl("/login?error=true")
                        )
                        .logout(logout -> logout
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                        );

                return http.build();
        }


        //  Giải mã base64 secret để tạo key JWT
        private SecretKey getSecretKey() {
                byte[] keyBytes = Base64.from(jwtKey).decode();
                return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
        }

        // Cấu hình JWT Encoder
        @Bean
        public JwtEncoder jwtEncoder() {
                return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
        }

        // CORS: Cho phép frontend gọi API từ domain khác
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:3000")); // đổi nếu deploy
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true); // cho phép gửi cookie nếu có

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
