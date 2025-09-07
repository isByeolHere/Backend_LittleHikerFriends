package com.littlehikerfriends.dto;

import com.littlehikerfriends.entity.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SocialLoginCheckRequest {
    
    @NotNull(message = "로그인 제공자는 필수입니다")
    private Provider provider;
    
    @NotBlank(message = "소셜 로그인 토큰은 필수입니다")
    private String socialToken;
    
    // 기본 생성자
    public SocialLoginCheckRequest() {}
    
    // 생성자
    public SocialLoginCheckRequest(Provider provider, String socialToken) {
        this.provider = provider;
        this.socialToken = socialToken;
    }
    
    // Getters and Setters
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    
    public String getSocialToken() { return socialToken; }
    public void setSocialToken(String socialToken) { this.socialToken = socialToken; }
}
