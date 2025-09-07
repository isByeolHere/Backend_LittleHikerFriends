package com.littlehikerfriends.controller;

import com.littlehikerfriends.dto.*;
import com.littlehikerfriends.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/email-signup")
    @Operation(summary = "이메일 회원가입", description = "이메일과 비밀번호로 새 계정을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "입력 데이터가 올바르지 않음"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    public ResponseEntity<UserResponse> emailSignup(@RequestBody @Valid SignupRequest request) {
        // UserService의 signup 메서드 호출
        UserResponse userResponse = userService.signup(request);
        
        // 201 Created 상태코드와 함께 생성된 사용자 정보 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    
    @PostMapping("/email-login")
    @Operation(summary = "이메일 로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "입력 데이터가 올바르지 않음"),
        @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 오류)")
    })
    public ResponseEntity<LoginResponse> emailLogin(@RequestBody @Valid LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 로그인 실패시 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/social-login-check")
    @Operation(summary = "소셜 로그인 체크", description = "소셜 토큰으로 기존 회원인지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "체크 완료 - 기존 회원이면 로그인 정보, 신규면 임시 토큰 반환"),
        @ApiResponse(responseCode = "400", description = "입력 데이터가 올바르지 않음"),
        @ApiResponse(responseCode = "401", description = "소셜 토큰 검증 실패")
    })
    public ResponseEntity<SocialLoginCheckResponse> socialLoginCheck(@RequestBody @Valid SocialLoginCheckRequest request) {
        try {
            SocialLoginCheckResponse response = userService.socialLoginCheck(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/profile-create")
    @Operation(summary = "프로필 생성", description = "소셜 토큰으로 닉네임과 프로필 이미지를 설정하여 회원가입을 완료합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 완료"),
        @ApiResponse(responseCode = "400", description = "입력 데이터가 올바르지 않음"),
        @ApiResponse(responseCode = "401", description = "소셜 토큰이 유효하지 않음"),
        @ApiResponse(responseCode = "409", description = "이미 가입된 사용자")
    })
    public ResponseEntity<LoginResponse> createProfile(@RequestBody @Valid ProfileCreateRequest request) {
        try {
            LoginResponse response = userService.createProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("이미 가입된")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 체크", description = "이메일이 이미 사용 중인지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "중복 체크 완료")
    })
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        
        EmailCheckResponse response = new EmailCheckResponse(email, exists);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 세션을 종료합니다. (JWT는 클라이언트에서 삭제)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    public ResponseEntity<LogoutResponse> logout() {
        // JWT는 stateless이므로 서버에서 할 일이 없음
        // 클라이언트에서 토큰을 삭제하면 됨
        LogoutResponse response = new LogoutResponse("로그아웃되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    // 이메일 중복 체크 응답 클래스
    public static class EmailCheckResponse {
        private String email;
        private boolean exists;
        private String message;
        
        public EmailCheckResponse(String email, boolean exists) {
            this.email = email;
            this.exists = exists;
            this.message = exists ? "이미 사용 중인 이메일입니다" : "사용 가능한 이메일입니다";
        }
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    // 로그아웃 응답 클래스
    public static class LogoutResponse {
        private String message;
        private String timestamp;
        
        public LogoutResponse(String message) {
            this.message = message;
            this.timestamp = java.time.Instant.now().toString();
        }
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
