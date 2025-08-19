package com.littlehikerfriends.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (REST API이므로)
            .csrf(csrf -> csrf.disable())
            
            // 세션 사용하지 않음 (JWT 사용 예정)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 엔드포인트별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능한 엔드포인트
                .requestMatchers("/api/auth/**").permitAll()           // 회원가입, 로그인
                .requestMatchers("/swagger-ui/**").permitAll()         // Swagger UI
                .requestMatchers("/api-docs/**").permitAll()           // API 문서
                .requestMatchers("/actuator/health").permitAll()       // 헬스체크
                
                // 나머지는 모두 인증 필요 (나중에 JWT 구현 후 활성화)
                .anyRequest().permitAll()  // 임시로 모든 요청 허용
            );
            
        return http.build();
    }
}
