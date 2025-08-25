package com.littlehikerfriends.config;

import com.littlehikerfriends.entity.Provider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Authorization 헤더에서 JWT 토큰 추출
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // Bearer 토큰 형식 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " 제거
            
            try {
                // 토큰에서 이메일 추출
                email = jwtUtil.getEmailFromToken(token);
            } catch (Exception e) {
                logger.warn("JWT 토큰에서 이메일 추출 실패: " + e.getMessage());
            }
        }

        // 토큰이 유효하고 현재 인증되지 않은 상태라면
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 토큰 유효성 검증
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                
                // 사용자 정보 추출
                Integer userId = jwtUtil.getUserIdFromToken(token);
                Provider provider = jwtUtil.getProviderFromToken(token);
                
                // UserDetails 객체 생성 (Spring Security용)
                UserDetails userDetails = User.builder()
                        .username(email)
                        .password("") // 소셜 로그인이므로 비밀번호 불필요
                        .authorities(new ArrayList<>()) // 권한은 나중에 추가
                        .build();

                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                // 추가 정보 설정 (사용자 ID, Provider 등)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // 요청에 사용자 정보 추가 (컨트롤러에서 사용 가능)
                request.setAttribute("userId", userId);
                request.setAttribute("email", email);
                request.setAttribute("provider", provider);
            }
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
