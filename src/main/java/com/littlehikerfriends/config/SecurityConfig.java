package com.littlehikerfriends.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // CSRF 비활성화 (REST API이므로)
            .csrf(csrf -> csrf.disable())
            
            // 세션 사용하지 않음 (JWT 사용)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 엔드포인트별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능한 엔드포인트
                .requestMatchers("/").permitAll()                      // 루트 경로
                .requestMatchers("/api/health/**").permitAll()         // 헬스체크
                .requestMatchers("/api/auth/**").permitAll()           // 회원가입, 로그인
                .requestMatchers("/swagger-ui/**").permitAll()         // Swagger UI
                .requestMatchers("/swagger-ui.html").permitAll()       // Swagger UI (구버전 호환)
                .requestMatchers("/api-docs/**").permitAll()           // API 문서
                .requestMatchers("/v3/api-docs/**").permitAll()        // OpenAPI 3.0 문서
                .requestMatchers("/actuator/health").permitAll()       // 헬스체크
                .requestMatchers("/h2-console/**").permitAll()         // H2 콘솔 (개발용)
                
                // 나머지는 모든 인증 필요
                .anyRequest().authenticated()
            )
            
            // H2 콘솔을 위한 설정 (개발용)
            .headers(headers -> headers.frameOptions().disable());
            
        return http.build();
    }
}
