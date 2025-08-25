package com.littlehikerfriends.dto;

import com.littlehikerfriends.entity.Provider;

public class LoginResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;
    
    // 사용자 정보 내부 클래스
    public static class UserInfo {
        private Integer id;
        private String email;
        private String nickname;
        private String imageUrl;
        private Provider provider;
        
        // 기본 생성자
        public UserInfo() {}
        
        // 생성자
        public UserInfo(Integer id, String email, String nickname, String imageUrl, Provider provider) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.provider = provider;
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
    }
    
    // 기본 생성자
    public LoginResponse() {}
    
    // 생성자
    public LoginResponse(String accessToken, Long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
}
