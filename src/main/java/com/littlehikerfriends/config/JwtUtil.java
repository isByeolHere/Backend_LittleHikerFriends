package com.littlehikerfriends.config;

import com.littlehikerfriends.entity.Provider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // JWT 토큰 생성 (소셜 로그인 포함)
    public String generateToken(String email, Integer userId, Provider provider) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)                    // 사용자 이메일
                .claim("userId", userId)              // 사용자 ID
                .claim("provider", provider.name())   // 로그인 제공자
                .setIssuedAt(now)                     // 발급 시간
                .setExpiration(expiryDate)            // 만료 시간
                .signWith(getSigningKey())            // 서명
                .compact();
    }

    // JWT 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    // JWT 토큰에서 사용자 ID 추출
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Integer.class);
    }

    // JWT 토큰에서 Provider 추출
    public Provider getProviderFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String providerName = claims.get("provider", String.class);
        return Provider.valueOf(providerName);
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 토큰이 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    // JWT 토큰에서 Claims 추출
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 서명 키 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
