package com.littlehikerfriends.service;

import com.littlehikerfriends.entity.Provider;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SocialAuthService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 소셜 토큰으로 사용자 이메일 조회
     */
    public String getEmailFromSocialToken(Provider provider, String socialToken) {
        switch (provider) {
            case KAKAO:
                return getKakaoEmail(socialToken);
            case GOOGLE:
                return getGoogleEmail(socialToken);
            case APPLE:
                return getAppleEmail(socialToken);
            default:
                throw new RuntimeException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }
    }
    
    /**
     * 카카오 API로 이메일 조회
     */
    private String getKakaoEmail(String accessToken) {
        try {
            String url = "https://kapi.kakao.com/v2/user/me";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.path("kakao_account").path("email").asText();
            
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 검증 실패: " + e.getMessage());
        }
    }
    
    /**
     * 구글 API로 이메일 조회
     */
    private String getGoogleEmail(String accessToken) {
        try {
            String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.path("email").asText();
            
        } catch (Exception e) {
            throw new RuntimeException("구글 토큰 검증 실패: " + e.getMessage());
        }
    }
    
    /**
     * 애플 API로 이메일 조회 (JWT 토큰 디코딩)
     */
    private String getAppleEmail(String idToken) {
        try {
            // Apple ID Token은 JWT 형태이므로 디코딩 필요
            // 실제 구현시에는 Apple의 공개키로 서명 검증도 해야 함
            String[] chunks = idToken.split("\\.");
            if (chunks.length != 3) {
                throw new RuntimeException("잘못된 Apple ID Token 형식");
            }
            
            String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            return jsonNode.path("email").asText();
            
        } catch (Exception e) {
            throw new RuntimeException("애플 토큰 검증 실패: " + e.getMessage());
        }
    }
}
