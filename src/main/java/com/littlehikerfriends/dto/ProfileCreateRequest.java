package com.littlehikerfriends.dto;

import com.littlehikerfriends.entity.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProfileCreateRequest {
    
    @NotNull(message = "로그인 제공자는 필수입니다")
    private Provider provider;
    
    @NotBlank(message = "소셜 로그인 토큰은 필수입니다")
    private String socialToken;
    
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;
    
    private String imageUrl; // 프로필 이미지 (선택사항)
    
    // 기본 생성자
    public ProfileCreateRequest() {}
    
    // 생성자
    public ProfileCreateRequest(Provider provider, String socialToken, String nickname, String imageUrl) {
        this.provider = provider;
        this.socialToken = socialToken;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    
    public String getSocialToken() { return socialToken; }
    public void setSocialToken(String socialToken) { this.socialToken = socialToken; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
