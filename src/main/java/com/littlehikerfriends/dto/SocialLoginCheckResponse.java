package com.littlehikerfriends.dto;

public class SocialLoginCheckResponse {
    
    private boolean isExistingUser;
    private String email;
    private LoginResponse loginResponse; // 기존 사용자용 로그인 정보
    
    // 기본 생성자
    public SocialLoginCheckResponse() {}
    
    // 기존 사용자용 생성자
    public SocialLoginCheckResponse(boolean isExistingUser, String email, LoginResponse loginResponse) {
        this.isExistingUser = isExistingUser;
        this.email = email;
        this.loginResponse = loginResponse;
    }
    
    // 신규 사용자용 생성자
    public SocialLoginCheckResponse(boolean isExistingUser, String email) {
        this.isExistingUser = isExistingUser;
        this.email = email;
    }
    
    // Getters and Setters
    public boolean isExistingUser() { return isExistingUser; }
    public void setExistingUser(boolean existingUser) { isExistingUser = existingUser; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LoginResponse getLoginResponse() { return loginResponse; }
    public void setLoginResponse(LoginResponse loginResponse) { this.loginResponse = loginResponse; }
}
