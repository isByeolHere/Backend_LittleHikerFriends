package com.littlehikerfriends.dto;

import com.littlehikerfriends.entity.Provider;
import com.littlehikerfriends.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "사용자 정보 응답")
public class UserResponse {
    
    @Schema(description = "사용자 ID", example = "123")
    private Integer id;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @Schema(description = "닉네임", example = "등산러버")
    private String nickname;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String imageUrl;
    
    @Schema(description = "가입 방식", example = "EMAIL")
    private Provider provider;
    
    @Schema(description = "가입일", example = "2025-08-15T10:30:00")
    private LocalDateTime createdAt;
    
    // 기본 생성자
    public UserResponse() {}
    
    // User 엔티티로부터 생성하는 생성자
    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.imageUrl = user.getImageUrl();
        this.provider = user.getProvider();
        this.createdAt = user.getCreatedAt();
    }
    
    // 전체 필드 생성자
    public UserResponse(Integer id, String email, String nickname, String imageUrl, 
                       Provider provider, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
